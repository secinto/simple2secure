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

import {Component, OnInit} from '@angular/core';
import {CompanyGroup} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {MatDialog} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'userGroup.component.html',
	selector: 'UserGroupComponent',
})

export class UserGroupComponent implements OnInit {

	group = new CompanyGroup();
	loading = false;
	id: string;
	private sub: any;
	url: string;
	groupEditable: boolean;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private dialog: MatDialog,
		private alertService: AlertService,
		private translate: TranslateService)
	{
	}

	ngOnInit() {
		this.sub = this.route.params.subscribe(params => {
			this.id = params['id'];
		});

		this.groupEditable = this.dataService.isGroupEditable();
		this.loadGroup();
	}

	public loadGroup() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'group/' + this.id)
			.subscribe(
				data => {
					this.group = data;
					if (this.group) {
						this.alertService.success(this.translate.instant('message.data'));
					}
					else {
						this.alertService.error(this.translate.instant('message.data.notProvided'));
					}
					this.loading = false;

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

	saveGroup() {
		this.loading = true;

		this.url = environment.apiEndpoint + 'group/' + 'null';
		this.httpService.post(this.group, this.url).subscribe(
			data => {
				this.group = data;

				if (this.id === 'new') {
					this.alertService.success(this.translate.instant('message.user.create'));
				}
				else {
					this.alertService.success(this.translate.instant('message.user.update'));
				}
				this.cancel();
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

	cancel() {
		this.location.back();
	}
}
