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
import {DataService} from '../_services/index';

@Component({
	moduleId: module.id,
	templateUrl: 'networkReportDetails.component.html'
})

export class NetworkReportDetailsComponent {
	report: any;
	loading = false;
	result: string;

	constructor(
		private dataService: DataService,
		private dialogRef: MatDialogRef<NetworkReportDetailsComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.report = data.report;
	}

	getStringResult() {
		this.result = JSON.parse(this.report.stringContent);
		return this.result;
	}
}
