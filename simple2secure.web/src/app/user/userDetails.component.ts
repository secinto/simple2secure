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
import {CompanyGroup, UrlParameter, UserRegistration, UserRegistrationType, UserRole} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'userDetails.component.html',
	selector: 'UserDetailsComponent'
})

export class UserDetailsComponent {
	public user: UserRegistration;
	url: string;
	showGroupSelectBox: boolean;
	rolesArray: UserRole[];
	groups: CompanyGroup[];
	action: string;
	isEmailFieldDisabled = false;
	userRole: string;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private alertService: AlertService,
		private dialogRef: MatDialogRef<UserDetailsComponent>,
		@Inject(MAT_DIALOG_DATA) data,
		private translate: TranslateService)
	{
		this.userRole = localStorage.getItem('role');
		if (data.user == null) {
			this.action = UrlParameter.NEW;
			this.user = new UserRegistration();
		}
		else {
			this.action = UrlParameter.EDIT;
			this.user = new UserRegistration();
			this.user.id = data.user.user.id;
			this.user.email = data.user.user.email;
			this.user.userRole = data.user.userRole;
			this.user.groupIds = data.user.groupIds;
			if (this.user.userRole === UserRole.SUPERUSER) {
				this.showGroupSelectBox = true;
			}
			this.isEmailFieldDisabled = true;
		}
	}

	ngOnInit() {
		this.loadGroups();
	}

	userRoleKeys(): Array<string> {
		if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN) {
			// Return ADMIN, SUPERUSER and USER
			this.rolesArray = [UserRole.ADMIN, UserRole.SUPERUSER, UserRole.USER];
			return this.rolesArray;
		}
		else if (this.userRole == UserRole.SUPERUSER) {
			// Return USER
			this.rolesArray = [UserRole.USER];
			return this.rolesArray;
		}
	}

	onRoleChange(value: string) {
		if (value === UserRole.SUPERUSER) {
			this.showGroupSelectBox = true;
		}
		else if (value === UserRole.USER) {
			this.showGroupSelectBox = false;
		}
		else {
			this.showGroupSelectBox = false;
		}
	}

	private loadGroups() {
		this.httpService.get(environment.apiEndpoint + 'group/context')
			.subscribe(
				data => {
					this.groups = data;
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

	saveUser() {
		this.url = environment.apiEndpoint + 'user';
		if (this.action === UrlParameter.NEW) {
			this.user.registrationType = UserRegistrationType.ADDED_BY_USER;
		}
		else {
			this.user.registrationType = UserRegistrationType.UPDATE_USER_INFO;
		}

		this.httpService.post(this.user, this.url).subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
			});
	}
}
