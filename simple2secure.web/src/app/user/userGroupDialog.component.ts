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
	templateUrl: 'userGroupDialog.component.html',
	selector: 'UserGroupDialogComponent',
	providers: [DatePipe]
})

export class UserGroupDialogComponent {
	public group: CompanyGroup;
	loading = false;
	id: string;
	private sub: any;
	url: string;
	parentGroup: CompanyGroup;
	parentGroupId: string;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private alertService: AlertService,
		private translate: TranslateService,
		private datePipe: DatePipe,
		private dialogRef: MatDialogRef<UserGroupDialogComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.group = new CompanyGroup();
		this.parentGroup = data;
	}

	ngOnInit() {
		this.sub = this.route.params.subscribe(params => {
			this.id = params['id'];
		});
	}

	saveGroup() {
		this.loading = true;
		if (!this.parentGroup) {
			this.parentGroupId = null;
		}
		else {
			this.parentGroupId = this.parentGroup.id;
		}

		this.url = environment.apiEndpoint + 'group/' + this.parentGroupId;
		this.httpService.post(this.group, this.url).subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
				this.loading = false;
			});
	}

	cancel() {
		this.location.back();
	}
}
