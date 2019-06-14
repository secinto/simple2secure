import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {Test} from '../_models';
import {AlertService, DataService, HttpService} from '../_services/index';

@Component({
	moduleId: module.id,
	templateUrl: 'testDetails.component.html'
})

export class TestDetailsComponent{
	loading = false;
	test: Test[];

	constructor(
		private dataService: DataService,
		private alertService: AlertService,
		private dialogRef: MatDialogRef<TestDetailsComponent>,
		private httpService: HttpService,
		private translate: TranslateService,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.test = data.tests;

	}

}
