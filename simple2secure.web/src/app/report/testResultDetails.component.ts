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

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TestResult} from '../_models';
import {DataService} from '../_services';

@Component({
	moduleId: module.id,
	templateUrl: 'testResultDetails.component.html'
})

export class TestResultDetailsComponent {
	testResult: TestResult;
	loading = false;
	result: string;

	constructor(
		private dataService: DataService,
		private dialogRef: MatDialogRef<TestResultDetailsComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		let b64EncResult = JSON.parse(data.result.result);
		let b64DecResult = atob(b64EncResult.step);
		b64EncResult.step = b64DecResult;
		data.result.result = JSON.stringify(b64EncResult);
		this.testResult = data.result;
	}

}
