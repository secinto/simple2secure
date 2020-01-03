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

package com.simple2secure.api.dto;

import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.TestSequenceResult;

public class TestSequenceRunDTO {

	private SequenceRun sequenceRun;

	private TestSequenceResult sequenceResult;

	public TestSequenceRunDTO() {

	}

	public TestSequenceRunDTO(SequenceRun sequenceRun, TestSequenceResult sequenceResult) {
		super();
		this.sequenceRun = sequenceRun;
		this.sequenceResult = sequenceResult;
	}

	public SequenceRun getSequenceRun() {
		return sequenceRun;
	}

	public void setSequenceRun(SequenceRun sequenceRun) {
		this.sequenceRun = sequenceRun;
	}

	public TestSequenceResult getSequenceResult() {
		return sequenceResult;
	}

	public void setSequenceResult(TestSequenceResult sequenceResult) {
		this.sequenceResult = sequenceResult;
	}
}
