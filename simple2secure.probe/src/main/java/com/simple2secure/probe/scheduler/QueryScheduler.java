/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.probe.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.OsQuery;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.osquery.QueryRunnable;
import com.simple2secure.probe.utils.LocaleHolder;

public class QueryScheduler extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(QueryScheduler.class);

	private ScheduledThreadPoolExecutor scheduler;
	private Map<String, QueryRunnable> currentlyRunning;

	public QueryScheduler() {
		currentlyRunning = new HashMap<>();
		int processors = Runtime.getRuntime().availableProcessors();

		if (processors > 4) {
			processors = processors / 2;
		}

		scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(processors);
		scheduler.setRemoveOnCancelPolicy(true);
	}

	@Override
	public void run() {
		try {
			log.debug("Checking scheduled queries");
			/*
			 * Check if one of the currently running ones is not contained in the currentQueries anymore. All not available ones are canceled.
			 */
			if (currentlyRunning != null && currentlyRunning.size() > 0) {
				List<QueryRunnable> runningQueries = new ArrayList<>(currentlyRunning.values());
				for (QueryRunnable queryRunnable : runningQueries) {
					if (!ProbeConfiguration.getInstance().getCurrentQueries().containsKey(queryRunnable.getQuery().getName())) {
						queryRunnable.getScheduledFuture().cancel(false);
						currentlyRunning.remove(queryRunnable.getQuery().getName());
					}
				}
			}
			for (OsQuery query : ProbeConfiguration.getInstance().getCurrentQueries().values()) {
				/*
				 * Check if the query is already in the currently running ones. Only for queries which are executed always. If not create a
				 * scheduled future and add it to the currently running ones.
				 */
				if (!currentlyRunning.containsKey(query.getName())) {
					QueryRunnable queryRunnable = new QueryRunnable(query);
					ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(queryRunnable, 0, query.getAnalysisInterval(),
							query.getAnalysisIntervalUnit());
					queryRunnable.setScheduledFuture(scheduledFuture);
					currentlyRunning.put(query.getName(), queryRunnable);
				} else {
					/*
					 * Check when to update the currently running queries.
					 */
					QueryRunnable currentQueryRunnable = currentlyRunning.get(query.getName());
					if (currentQueryRunnable != null) {
						OsQuery currentQuery = currentQueryRunnable.getQuery();
						/*
						 * Check if something has changed for the current query. If yes cancel it and create a new scheduled future.
						 */
						if (currentQuery != null && (currentQuery.getAnalysisInterval() != query.getAnalysisInterval()
								|| currentQuery.getAnalysisIntervalUnit() != query.getAnalysisIntervalUnit())) {
							currentQueryRunnable.getScheduledFuture().cancel(false);
						}
					}

				}

			}
		} catch (Exception e) {
			log.error(LocaleHolder.getMessage("pcap_interface_open_error").getMessage());
		}
	}
}
