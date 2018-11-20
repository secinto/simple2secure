package com.simple2secure.api.model;

public class ServiceLibrary extends Service {

	private static final long serialVersionUID = 4975292575224536067L;

	private String filename;

	public ServiceLibrary(String name, String version, String filename) {
		super(name, version);
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
