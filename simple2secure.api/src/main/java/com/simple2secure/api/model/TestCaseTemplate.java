package com.simple2secure.api.model;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestCaseTemplate extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 9158531943618048576L;

	private String name;
	private String toolId;
	private List<Command> commands = new ArrayList<>();

	public TestCaseTemplate() {

	}

	public TestCaseTemplate(String name, String toolId, List<Command> commands) {
		this.name = name;
		this.toolId = toolId;
		this.commands = commands;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getToolId() {
		return toolId;
	}

	public void setToolId(String toolId) {
		this.toolId = toolId;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}
}
