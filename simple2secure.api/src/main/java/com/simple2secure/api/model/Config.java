package com.simple2secure.api.model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Config")
public class Config extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -3153781553493971957L;

	private int version;
	private boolean showInterfaces;
	private String externalAddress;
	private String processingFactory;

	@Lob
	private String bpfFilter;

	public Config() {

	}

	public Config(int version, boolean showInterfaces, String externalAddress, String processing_factory, String bpfFilter) {
		this.version = version;
		this.showInterfaces = showInterfaces;
		this.externalAddress = externalAddress;
		this.processingFactory = processing_factory;
		this.bpfFilter = bpfFilter;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isShowInterfaces() {
		return showInterfaces;
	}

	public void setShowInterfaces(boolean showInterfaces) {
		this.showInterfaces = showInterfaces;
	}

	public String getExternalAddress() {
		return externalAddress;
	}

	public void setExternalAddress(String externalAddress) {
		this.externalAddress = externalAddress;
	}

	public String getProcessingFactory() {
		return processingFactory;
	}

	public void setProcessingFactory(String processingFactory) {
		this.processingFactory = processingFactory;
	}

	public String getBpfFilter() {
		return bpfFilter;
	}

	public void setBpfFilter(String bpfFilter) {
		this.bpfFilter = bpfFilter;
	}

}
