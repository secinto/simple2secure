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

import com.simple2secure.api.dbo.GenericDBObject;

public class TestSequenceResult extends GenericDBObject {
	/**
	 *
	 */
    private static final long serialVersionUID = 8963088362714211548L;
    private String sequence_run_id;
    private String sequence_id;
    private String pod_id;
    private String sequence_name;
    private String sequence_result;
	private long time_stamp;

	public TestSequenceResult() {

	}

	public TestSequenceResult(String sequenceRunId, String sequenceId, String podId, String sequenceName, String sequenceResult, long timestamp) {
        this.sequence_run_id = sequenceRunId;
        this.sequence_id = sequenceId;
        this.pod_id = podId;
        this.sequence_name = sequenceName;
        this.sequence_result = sequenceResult;
		this.time_stamp = timestamp;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getSequence_run_id() {
        return sequence_run_id;
    }

    public void setSequence_run_id(String sequence_run_id) {
        this.sequence_run_id = sequence_run_id;
    }

    public String getSequence_id() {
        return sequence_id;
    }

    public void setSequence_id(String sequence_id) {
        this.sequence_id = sequence_id;
    }

    public String getPod_id() {
        return pod_id;
    }

    public void setPod_id(String pod_id) {
        this.pod_id = pod_id;
    }

    public String getSequence_name() {
        return sequence_name;
    }

    public void setSequence_name(String sequence_name) {
        this.sequence_name = sequence_name;
    }

    public String getSequence_result() {
        return sequence_result;
    }

    public void setSequence_result(String sequence_result) {
        this.sequence_result = sequence_result;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

}
