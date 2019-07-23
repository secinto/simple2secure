package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class Command extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -2977516759831166624L;

	private String executable;
	private Parameter parameter;

	public Command() {

	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

}
