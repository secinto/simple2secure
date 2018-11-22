package com.simple2secure.api.dto;

import com.simple2secure.api.model.ServiceLibrary;

public class ServiceLibraryDTO extends ServiceLibrary {

	private static final long serialVersionUID = -4739342507571172854L;

	private byte[] libraryData;

	public ServiceLibraryDTO(String name, String version, String filename) {
		super(name, version, filename);
	}

	public byte[] getLibraryData() {
		return libraryData;
	}

	public void setLibraryData(byte[] libraryData) {
		this.libraryData = libraryData;
	}

}
