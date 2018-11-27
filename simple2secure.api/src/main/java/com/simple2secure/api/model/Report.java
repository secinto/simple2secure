package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Report")
public class Report extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -7217360147886001090L;
	private String groupId;
	private String probeId;
	private String query;

	@Lob
	private String queryResult;

	private String queryTimestamp;
	private boolean isSent;

	public Report() {

	}

	/**
	 *
	 * @param name
	 * @param report_class
	 * @param interval
	 */
	public Report(String probeId, String query, String queryResult, String queryTimestamp, boolean isSent) {
		super();
		this.probeId = probeId;
		this.query = query;
		this.queryResult = queryResult;
		this.queryTimestamp = queryTimestamp;
		this.isSent = isSent;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(String queryResult) {
		this.queryResult = queryResult;
	}

	public String getQueryTimestamp() {
		return queryTimestamp;
	}

	public void setQueryTimestamp(String queryTimestamp) {
		this.queryTimestamp = queryTimestamp;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

}
