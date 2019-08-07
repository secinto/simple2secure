package com.simple2secure.probe.osquery;

import java.io.BufferedReader;
import java.io.File;
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
import com.simple2secure.commons.config.StaticConfigItems;
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
		String queryResult = executeQuery(queryString);
		if (!Strings.isNullOrEmpty(queryResult)) {
			Report result = new Report(ProbeConfiguration.probeId, queryString, queryResult, new Date().toString(), false);
			result.setGroupId(ProbeConfiguration.groupId);
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
	public String executeQuery(String query) {
		String result = "";
		Process p;

		File queryExec = new File(StaticConfigItems.OSQUERY_PATH + File.separator + "osqueryi.exe");
		String myCommand = queryExec.getAbsolutePath();
		String myArgs0 = "--json";
		String myArgs1 = "--config-path=" + StaticConfigItems.OSQUERY_PATH + File.separator + "osquery.conf";
		String myArgs2 = query;

		ProcessBuilder pb = new ProcessBuilder(myCommand, myArgs0, myArgs1, myArgs2).redirectErrorStream(true);
		// pb.directory(directory);

		try {
			p = pb.start();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			result = IOUtils.toString(reader);
			log.debug("OSQuery {} resulted {}", query, result);
			result = "[" + StringUtils.substringBetween(result, "[", "]").trim() + "]";
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
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
