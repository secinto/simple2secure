package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

public class TestMacro extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -8902075451167042255L;

	private String executable;

	private String macro;

	public TestMacro() {
	}

	public TestMacro(String executable, String macro) {
		this.executable = executable;
		this.macro = macro;
	}

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public String getMacro() {
		return macro;
	}

	public void setMacro(String macro) {
		this.macro = macro;
	}

}
