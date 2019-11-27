package com.simple2secure.api.model.validation;

public class ValidInputReport extends ValidatedInput<String>{
	
	private String reportId;
	private String tag = "/{reportId}";
	
	public ValidInputReport() {
	}
	
	public ValidInputReport(String reportId) {
		this.reportId = reportId;
	}

	@Override
	public String getValue() {
		return reportId;
	}


	@Override
	public String getTag() {
		return tag;
	}
}
