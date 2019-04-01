package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestStep extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1998429205709387726L;

	private String description;
	private Command command;
	private Rule condition;

	public TestStep() {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Rule getCondition() {
		return condition;
	}

	public void setCondition(Rule condition) {
		this.condition = condition;
	}

}
