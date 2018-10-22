package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "QueryConfig")
public class QueryConfig extends GenericDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6832309845224642179L;
	private String userUUID;
	private String querystrings;
	private String queryconfig;
	private String osquerypath;

	public QueryConfig() {

	}

	public QueryConfig(String userUUID, String querystrings, String queryconfig, String osquerypath) {
		super();
		this.userUUID = userUUID;
		this.querystrings = querystrings;
		this.queryconfig = queryconfig;
		this.osquerypath = osquerypath;
	}

	public String getQuerystrings() {
		return this.querystrings;
	}

	public void setQuerystrings(String querystrings) {
		this.querystrings = querystrings;
	}

	public String getQueryconfig() {
		return this.queryconfig;
	}

	public void setQueryconfig(String queryconfig) {
		this.queryconfig = queryconfig;
	}

	public String getOsquerypath() {
		return this.osquerypath;
	}

	public void setOsquerypath(String osquerypath) {
		this.osquerypath = osquerypath;
	}

	public String getUserUUID() {
		return this.userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}

}
