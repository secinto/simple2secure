package com.simple2secure.probe.osquery;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.util.Strings;

import com.simple2secure.api.model.QueryRun;
import com.simple2secure.api.model.Report;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.utils.DBUtil;

public class QueryRunnable implements Runnable {

	private QueryRun query;

	private ScheduledFuture<?> scheduledFuture;

	public QueryRunnable(QueryRun queryRun) {
		query = queryRun;
	}

	@Override
	public void run() {
		String queryString = query.getSqlQuery();
		String queryResult = executeQuery(queryString);
		if (Strings.isNotNullAndNotEmpty(queryResult)) {
			Report result = new Report(ProbeConfiguration.probeId, queryString, queryResult, new Date().toString(), false);
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

		File queryExec = new File(
				ProbeConfiguration.getInstance().getCurrentConfigObj().getQueries().getOsquerypath() + File.separator + "osqueryi.exe");
		String myCommand = queryExec.getAbsolutePath();
		String myArgs0 = "--json";
		String myArgs1 = "--config-path=" + ProbeConfiguration.getInstance().getCurrentConfigObj().getQueries().getOsquerypath()
				+ File.separator + "osquery.conf";
		String myArgs2 = query;

		ProcessBuilder pb = new ProcessBuilder(myCommand, myArgs0, myArgs1, myArgs2).redirectErrorStream(true);
		// pb.directory(directory);

		try {
			p = pb.start();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			result = IOUtils.toString(reader);
			result = StringUtils.substringBetween(result, "[", "]").trim();
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
