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

import com.simple2secure.api.model.QueryRun;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.osquery.QueryRunnable;
import com.simple2secure.probe.utils.LocaleHolder;

public class QueryScheduler extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(QueryScheduler.class);

	private ScheduledThreadPoolExecutor scheduler;
	private Map<String, QueryRunnable> currentlyRunning;

	public QueryScheduler() {
		currentlyRunning = new HashMap<String, QueryRunnable>();
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
			/*
			 * Check if one of the currently running ones is not contained in the
			 * currentQueries anymore. All not available ones are canceled.
			 */
			if (currentlyRunning != null && currentlyRunning.size() > 0) {
				List<QueryRunnable> runningQueries = new ArrayList<QueryRunnable>(currentlyRunning.values());
				for (QueryRunnable queryRunnable : runningQueries) {
					if (!ProbeConfiguration.getInstance().getCurrentQueries().containsKey(queryRunnable.getQuery().getName())) {
						queryRunnable.getScheduledFuture().cancel(false);
						currentlyRunning.remove(queryRunnable.getQuery().getName());
					}
				}
			}
			for (QueryRun query : ProbeConfiguration.getInstance().getCurrentQueries().values()) {
				/*
				 * Check if the query is already in the currently running ones. Only for queries
				 * which are executed always. If not create a scheduled future and add it to the
				 * currently running ones.
				 */
				if (!currentlyRunning.containsKey(query.getName())) {
					QueryRunnable queryRunnable = new QueryRunnable(query);
					if (query.isAlways()) {
						ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(queryRunnable, 0, query.getAnalysisInterval(),
								query.getAnalysisIntervalUnit());
						queryRunnable.setScheduledFuture(scheduledFuture);
						currentlyRunning.put(query.getName(), queryRunnable);
					} else {
						scheduler.schedule(queryRunnable, 0, query.getAnalysisIntervalUnit());
					}
				} else {
					/*
					 * Check when to update the currently running queries.
					 */
					QueryRunnable currentQueryRunnable = currentlyRunning.get(query.getName());
					if (currentQueryRunnable != null) {
						QueryRun currentQuery = currentQueryRunnable.getQuery();
						/*
						 * Check if something has changed for the current query. If yes cancel it and
						 * create a new scheduled future.
						 */
						if (currentQuery != null && currentQuery.isAlways()
								&& (currentQuery.getAnalysisInterval() != query.getAnalysisInterval()
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
