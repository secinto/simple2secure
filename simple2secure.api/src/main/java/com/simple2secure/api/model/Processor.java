/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.api.model;

import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simple2secure.api.dbo.GenericDBObject;

@Entity
@Table(
		name = "Processor")
public class Processor extends GenericDBObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 6776396393360976891L;

	@JsonProperty
	private boolean groovy = false;
	private String name;
	private String processor_class;

	@Lob
	@Column(
			columnDefinition = "CLOB")
	private String groovyProcessor;

	private long analysisInterval;
	private TimeUnit analysisIntervalUnit;

	/**
	 * Default Constructor
	 */
	public Processor() {
	}

	public Processor(String name, String processor_class, String groovyProcessor) {
		super();
		groovy = true;
		this.name = name;
		this.processor_class = processor_class;
		this.groovyProcessor = groovyProcessor;
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
		return groovy;
	}

	public void setGroovy(boolean groovy) {
		this.groovy = groovy;
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
}
