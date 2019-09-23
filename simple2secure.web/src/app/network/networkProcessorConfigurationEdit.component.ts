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
import {Processor, Timeunit, UrlParameter} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'networkProcessorConfigurationEdit.component.html'
})

export class NetworkProcessorConfigurationEditComponent {

	processor: Processor;
	action: string;
	groupId: string;
	timeUnits = Timeunit;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private translate: TranslateService,
		private router: Router,
		private route: ActivatedRoute,
		private location: Location,
		private dialogRef: MatDialogRef<NetworkProcessorConfigurationEditComponent>,
		@Inject(MAT_DIALOG_DATA) data
	)
	{
		if (data.processor == null) {
			this.action = UrlParameter.NEW;
			this.processor = new Processor();
			this.groupId = data.groupId;
		}
		else {
			this.action = UrlParameter.EDIT;
			this.processor = data.processor;
			this.groupId = data.groupId;
		}
	}

	extractTimeUnits(): Array<string> {
		const keys = Object.keys(this.timeUnits);
		return keys.slice();
	}

	saveProcessor() {

		if (this.action == UrlParameter.NEW) {
			this.processor.groupId = this.groupId;
		}

		console.log('HEREEEEEEEEEEE');
		this.httpService.post(this.processor, environment.apiEndpoint + 'processors').subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
			});
	}

	cancel() {
		this.location.back();
	}
}
