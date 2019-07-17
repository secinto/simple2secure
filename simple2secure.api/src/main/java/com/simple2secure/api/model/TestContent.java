package com.simple2secure.api.model;

public class TestContent {
	private String name;
	private TestDefinition test_definition;

	public TestContent() {

	}

	public TestContent(String name, TestDefinition test_definition) {
		this.name = name;
		this.test_definition = test_definition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TestDefinition getTest_definition() {
		return test_definition;
	}

	public void setTest_definition(TestDefinition test_definition) {
		this.test_definition = test_definition;
	}

}
