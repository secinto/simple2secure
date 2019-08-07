package com.simple2secure.api.model;

import java.util.List;

import javax.persistence.OneToMany;

import com.simple2secure.api.dbo.GenericDBObject;

public class Test extends GenericDBObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4242956528915218942L;
	private String name;
	@OneToMany
	private List<Command> commands;
	private boolean scheduled;
	private boolean finished;
	@OneToMany
	private List<TestResult> result;
	private boolean customTest;
	private boolean createInstance;

	public Test() {
	}

	public Test(String name, List<Command> commands, boolean scheduled, boolean finished, List<TestResult> result, boolean customTest) {
		this.name = name;
		this.scheduled = scheduled;
		this.finished = finished;
		this.result = result;
		this.commands = commands;
		this.customTest = customTest;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isScheduled() {
		return this.scheduled;
	}

	public void setIsScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}
	
	public boolean isFinished() {
		return this.finished;
	}
	
	public void setIsFinished(boolean finished) {
		this.finished = finished;
	}
	public void setTestResult(List<TestResult> result) {
		this.result = result;
	}

	public List<TestResult> getTestResult() {
		return this.result;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public boolean isCustomTest() {
		return customTest;
	}

	public void setIsCustomTest(boolean customTest) {
		this.customTest = customTest;
	}
	
	public void SetCreateInstance(boolean createInstance) {
		this.createInstance = createInstance;
	}
	
	public boolean getCreateInstance() {
		return this.createInstance;
	}
}
