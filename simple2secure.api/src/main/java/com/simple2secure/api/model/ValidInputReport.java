package com.simple2secure.api.model;

public class ValidInputReport extends ValidatedInput{
	
	private String reportId;
	
	public ValidInputReport(String reportId) {
		this.reportId = reportId;
	}

	@Override
	public String getValue() {
		return reportId;
	}

	@Override
	public void setValue(String value) {
		this.reportId = value;
	}
	
}
