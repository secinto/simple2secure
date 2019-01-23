package com.simple2secure.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.model.TestCaseTemplate;
import com.simple2secure.api.model.Tool;

public class ToolDTO {

	private Tool tool;

	private List<TestDTO> tests = new ArrayList<>();

	private List<TestCaseTemplate> templates = new ArrayList<>();

	public ToolDTO(Tool tool) {
		this.tool = tool;
	}

	public ToolDTO(Tool tool, List<TestCaseTemplate> templates) {
		this.tool = tool;
		this.templates = templates;
	}

	public ToolDTO(Tool tool, List<TestDTO> tests, List<TestCaseTemplate> templates) {
		this.tool = tool;
		this.tests = tests;
		this.templates = templates;
	}

	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

	public List<TestDTO> getTests() {
		return tests;
	}

	public void setTests(List<TestDTO> tests) {
		this.tests = tests;
	}

	public List<TestCaseTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(List<TestCaseTemplate> templates) {
		this.templates = templates;
	}
}
