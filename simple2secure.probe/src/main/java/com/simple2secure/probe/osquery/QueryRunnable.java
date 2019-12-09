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
package com.simple2secure.probe.osquery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Report;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.utils.DBUtil;

public class QueryRunnable implements Runnable {
	private static Logger log = LoggerFactory.getLogger(QueryRunnable.class);

	private QueryRun query;

	private ScheduledFuture<?> scheduledFuture;

	public QueryRunnable(QueryRun queryRun) {
		query = queryRun;
	}

	@Override
	public void run() {
		String queryString = query.getSqlQuery();
		log.info("Executing query {} ", query.getName());
		String queryResult = executeQuery(queryString, query.getName());
		if (!Strings.isNullOrEmpty(queryResult)) {
			Report result = new Report(ProbeConfiguration.probeId, queryString, queryResult, new Date(), false);
			result.setQueryId(query.getId());
			result.setHostname(ProbeConfiguration.hostname);
			result.setName(query.getName());
			DBUtil.getInstance().save(result);
		}
	}

	public ScheduledFuture<?> getScheduledFuture() {
		return scheduledFuture;
	}

	public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}

	/**
	 * Run a osQuery query.
	 *
	 * @param directory
	 *          filepath to directory holding osqueryi executable
	 * @param query
	 * @return
	 */
	public String executeQuery(String query, String name) {
		String result = "";
		Process p;

		String myCommand = ProbeConfiguration.osQueryExecutablePath;
		String myArgs0 = "--json";
		String myArgs1 = "--config-path=" + ProbeConfiguration.osQueryConfigPath;
		String myArgs2 = query;

		ProcessBuilder pb = new ProcessBuilder(myCommand, myArgs0, myArgs1, myArgs2).redirectErrorStream(true);
		// pb.directory(directory);
		log.debug("Using command {} to execute query", pb.command());
		try {
			p = pb.start();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			result = IOUtils.toString(reader);
			log.debug("OSQuery {} resulted {}", name, result);
			result = StringUtils.substringBetween(result, "[", "]").trim();
			if (!Strings.isNullOrEmpty(result)) {
				result = "[" + result + "]";
			}
			p.destroy();
		} catch (Exception e) {
			log.error("Exception during QSQuery. Reason {}", e.getMessage());
		}
		return result;
	}

	public QueryRun getQuery() {
		return query;
	}

	public void setQuery(QueryRun query) {
		this.query = query;
	}

}
