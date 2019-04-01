package com.simple2secure.api.model;

import java.util.ArrayList;

import com.simple2secure.api.dbo.GenericDBObject;

public class Command extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -2977516759831166624L;

	private String command;
	private ArrayList<Parameter> parameters;

	public Command() {

	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}

}
