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
import {Location} from '@angular/common';
import {QueryRun, Timeunit, UrlParameter} from '../_models/index';

import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'osqueryConfigurationEdit.component.html'
})

export class OsqueryConfigurationEditComponent {

	queryRun: QueryRun;
	id: string;
	type: number;
	action: string;
	probeId: string;
	groupId: string;
	alwaysFalseButton = false;
	timeUnits = Timeunit;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private router: Router,
		private translate: TranslateService,
		private route: ActivatedRoute,
		private location: Location,
		private dialogRef: MatDialogRef<OsqueryConfigurationEditComponent>,
		@Inject(MAT_DIALOG_DATA) data
	)
	{
		if (data.queryRun == null) {
			this.action = UrlParameter.NEW;
			this.queryRun = new QueryRun();
			this.groupId = data.groupId;
		}
		else {
			this.action = UrlParameter.EDIT;
			this.queryRun = data.queryRun;
			this.groupId = data.groupId;
		}
		this.alwaysFalseButton = false;
	}

	extractTimeUnits(): Array<string> {
		const keys = Object.keys(this.timeUnits);
		return keys.slice();
	}

	saveQueryRun() {

		if (this.action == UrlParameter.NEW) {
			this.queryRun.groupId = this.groupId;
		}

		this.httpService.post(this.queryRun, environment.apiEndpoint + 'query').subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
			});
	}

	setCheckedStatus(value) {
		if (value == 0) {
			this.alwaysFalseButton = true;
		} else {
			this.alwaysFalseButton = false;
		}
	}
}
