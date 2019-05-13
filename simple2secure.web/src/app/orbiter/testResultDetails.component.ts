import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TestResult} from '../_models';
import {DataService} from '../_services/index';

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
		this.testResult = data.result;
	}

}
