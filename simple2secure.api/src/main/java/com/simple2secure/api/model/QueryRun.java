package com.simple2secure.api.model;

import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "QueryRun")
public class QueryRun extends GenericDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4400048729580737036L;
	private String groupId;
	private String name;
	private boolean always;
	private long analysisInterval;
	private TimeUnit analysisIntervalUnit;

	private String sqlQuery;
	private int active;

	public QueryRun() {

	}

	public QueryRun(String groupId, String name, boolean always, long analysisInterval, TimeUnit analysisIntervalUnit,
			String sqlQuery, int active) {
		super();
		this.groupId = groupId;
		this.name = name;
		this.always = always;
		this.analysisInterval = analysisInterval;
		this.analysisIntervalUnit = analysisIntervalUnit;
		this.sqlQuery = sqlQuery;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAlways() {
		return always;
	}

	public void setAlways(boolean always) {
		this.always = always;
	}

	public long getAnalysisInterval() {
		return analysisInterval;
	}

	public void setAnalysisInterval(long analysisInterval) {
		this.analysisInterval = analysisInterval;
	}

	public TimeUnit getAnalysisIntervalUnit() {
		return analysisIntervalUnit;
	}

	public void setAnalysisIntervalUnit(TimeUnit analysisIntervalUnit) {
		this.analysisIntervalUnit = analysisIntervalUnit;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}
}
