package com.simple2secure.api.model;

import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(name = "Processor")
public class Processor extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 6776396393360976891L;
	private boolean isGroovy;
	private String probeId;
	private String groupId;
	private String name;
	private String processor_class;
	private boolean isGroupProcessor;

	@Lob
	@Column(columnDefinition = "CLOB")
	private String groovyProcessor;

	private long analysisInterval;
	private TimeUnit analysisIntervalUnit;

	/**
	 * Default Constructor
	 */
	public Processor() {
		isGroovy = false;
	}

	public Processor(String probeId, String name, String processor_class, String groovyProcessor) {
		super();
		isGroovy = true;
		this.probeId = probeId;
		this.name = name;
		this.processor_class = processor_class;
		this.groovyProcessor = groovyProcessor;
	}

	public String getProbeId() {
		return probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProcessor_class() {
		return processor_class;
	}

	public void setProcessor_class(String processor_class) {
		this.processor_class = processor_class;
	}

	public boolean isGroovy() {
		return isGroovy;
	}

	public void setGroovy(boolean isGroovy) {
		this.isGroovy = isGroovy;
	}

	public String getGroovyProcessor() {
		return groovyProcessor;
	}

	public void setGroovyProcessor(String groovyProcessor) {
		this.groovyProcessor = groovyProcessor;
	}

	public long getAnalysisInterval() {
		return analysisInterval;
	}

	public void setAnalysisInterval(long analysisInterval) {
		this.analysisInterval = analysisInterval;
	}

	public TimeUnit getAnalysisIntervalUnit() {
		return analysisIntervalUnit;
	}

	public void setAnalysisIntervalUnit(TimeUnit analysisIntervalUnit) {
		this.analysisIntervalUnit = analysisIntervalUnit;
	}

	public boolean isGroupProcessor() {
		return isGroupProcessor;
	}

	public void setGroupProcessor(boolean isGroupProcessor) {
		this.isGroupProcessor = isGroupProcessor;
	}
}
