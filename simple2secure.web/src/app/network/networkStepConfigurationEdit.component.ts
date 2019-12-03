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
import {Step, Processor, UrlParameter} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'networkStepConfigurationEdit.component.html'
})

export class NetworkStepConfigurationEditComponent {

	step: Step;
	id: string;
	type: number;
	action: string;
	groupId: string;
	processors: Processor[];
	private sub: any;


	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private router: Router,
		private route: ActivatedRoute,
		private translate: TranslateService,
		private location: Location,
		private dialogRef: MatDialogRef<NetworkStepConfigurationEditComponent>,
		@Inject(MAT_DIALOG_DATA) data
	)
	{
		if (data.step == null) {
			this.action = UrlParameter.NEW;
			this.step = new Step();
			this.groupId = data.groupId;
		}
		else {
			this.action = UrlParameter.EDIT;
			this.step = data.step;
			this.groupId = data.groupId;
		}
		this.getProcessorsByGroupId();

	}

	saveStep() {

		if (this.action == UrlParameter.NEW) {
			this.step.groupId = this.groupId;
		}
		this.httpService.post(this.step, environment.apiEndpoint + 'steps').subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
			});
	}

	getProcessorsByGroupId() {
		this.httpService.get(environment.apiEndpoint + 'processors')
			.subscribe(
				data => {
					this.processors = data;
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
				});
	}

	cancel() {
		this.location.back();
	}
}
