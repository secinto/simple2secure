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

import java.util.List;

import com.simple2secure.api.dbo.GenericDBObject;

public class SequenceRun extends GenericDBObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 8963088362714211548L;
	private String sequenceId;
	private String sequenceName;
	private String deviceId;
	private String contextId;
	private TestRunType testRunType;
	private List<String> sequenceContent;
	private TestStatus sequenceStatus;
	private long timestamp;

	public SequenceRun() {

	}

	public SequenceRun(String sequenceId, String sequenceName, String deviceId, String contextId, TestRunType testRunType, List<String> sequenceContent,
			TestStatus sequenceStatus, long timestamp) {
		this.sequenceId = sequenceId;
		this.sequenceName = sequenceName;
		this.deviceId = deviceId;
		this.contextId = contextId;
		this.testRunType = testRunType;
		this.sequenceContent = sequenceContent;
		this.sequenceStatus = sequenceStatus;
		this.timestamp = timestamp;
	}

	public String getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public TestRunType getTestRunType() {
		return testRunType;
	}

	public void setTestRunType(TestRunType testRunType) {
		this.testRunType = testRunType;
	}

	public List<String> getSequenceContent() {
		return sequenceContent;
	}

	public void setSequenceContent(List<String> sequenceContent) {
		this.sequenceContent = sequenceContent;
	}

	public TestStatus getSequenceStatus() {
		return sequenceStatus;
	}

	public void setSequenceStatus(TestStatus sequenceStatus) {
		this.sequenceStatus = sequenceStatus;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
