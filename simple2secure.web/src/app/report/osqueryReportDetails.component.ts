import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

import {DataService} from '../_services/index';

@Component({
	moduleId: module.id,
	templateUrl: 'osqueryReportDetails.component.html'
})

export class OsQueryReportDetailsComponent {
	report: any;
	queryResult: string;
	loading = false;

	constructor(
		private dataService: DataService,
		private dialogRef: MatDialogRef<OsQueryReportDetailsComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.report = data.report;
	}

	getQueryResult() {
		this.queryResult = JSON.parse(this.report.queryResult);
		return this.queryResult;
	}
}
