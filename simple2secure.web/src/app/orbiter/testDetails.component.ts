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
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {Timeunit} from '../_models';
import {TestObjWeb} from '../_models/testObjWeb';
import {AlertService, DataService, HttpService} from '../_services/index';

@Component({
	moduleId: module.id,
	templateUrl: 'testDetails.component.html'
})

export class TestDetailsComponent{
	loading = false;
	test: TestObjWeb;
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
			this.test = new TestObjWeb();
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
				this.close(true);
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


	public close(value: boolean){
		this.dialogRef.close(value);
	}

}
