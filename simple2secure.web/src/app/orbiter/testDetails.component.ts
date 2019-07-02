import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatExpansionPanel} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {Test, Timeunit} from '../_models';
import {AlertService, DataService, HttpService} from '../_services/index';

@Component({
	moduleId: module.id,
	templateUrl: 'testDetails.component.html'
})

export class TestDetailsComponent{
	loading = false;
	test: Test;
	type: string;
	isNewTest = false;
	url: string;
	timeUnits = Timeunit;

	constructor(
		private dataService: DataService,
		private alertService: AlertService,
		private dialogRef: MatDialogRef<TestDetailsComponent>,
		private httpService: HttpService,
		private translate: TranslateService,
		@Inject(MAT_DIALOG_DATA) data)
	{

		this.type = data.type;
		if (
			this.type == 'new'){
			this.isNewTest = true;
			this.test = new Test();
			this.test.podId = data.podId;
		}
		else{
			this.test = data.tests;
		}
	}

	extractTimeUnits(): Array<string> {
		const keys = Object.keys(this.timeUnits);
		return keys.slice();
	}


	public updateSaveTest() {
		this.loading = true;

		this.url = environment.apiEndpoint + 'test';
		this.httpService.post(this.test, this.url).subscribe(
			data => {
				if (this.type === 'new') {
					this.alertService.success(this.translate.instant('message.test.create'));
				}
				else {
					this.alertService.success(this.translate.instant('message.test.update'));
				}
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
				this.loading = false;
			});
	}

}
