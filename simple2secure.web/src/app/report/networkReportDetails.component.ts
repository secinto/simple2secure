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
