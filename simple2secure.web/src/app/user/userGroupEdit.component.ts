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
import {CompanyGroup, ContextDTO} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {DatePipe} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'userGroupEdit.component.html',
	selector: 'UserGroupEditComponent',
	providers: [DatePipe]
})

export class UserGroupEditComponent {
	public group: CompanyGroup;
	loading = false;
	url: string;
	groupEditable: boolean;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private alertService: AlertService,
		private translate: TranslateService,
		private datePipe: DatePipe,
		private dialogRef: MatDialogRef<UserGroupEditComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.group = data.group;
	}

	ngOnInit() {
		this.groupEditable = this.dataService.isGroupEditable();
	}

	saveGroup() {
		this.loading = true;

		this.url = environment.apiEndpoint + 'group/' + 'null';
		this.httpService.post(this.group, this.url).subscribe(
			data => {
				this.group = data;
				this.alertService.success(this.translate.instant('message.user.group.update'));
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
			});

		this.loading = false;
		this.dialogRef.close();
	}
}
