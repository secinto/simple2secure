package com.simple2secure.api.model;

import java.util.List;

public class TestCase extends TestCaseTemplate {

	/**
	 *
	 */
	private static final long serialVersionUID = -4242956528915218942L;

	private boolean scheduled;
	private boolean finished;

	public TestCase() {
	}

	public TestCase(String name, String toolId, List<Command> commands, boolean scheduled, boolean finished) {
		super(name, toolId, commands);
		this.scheduled = scheduled;
		this.finished = finished;
	}

	public boolean isScheduled() {
		return scheduled;
	}

	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
}
