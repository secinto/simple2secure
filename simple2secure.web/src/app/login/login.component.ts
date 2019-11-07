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
import {Router, ActivatedRoute} from '@angular/router';
import {AlertService, AuthenticationService, HttpService} from '../_services/index';
import {TranslateService} from '@ngx-translate/core';
import {JwtHelper} from 'angular2-jwt';
import {environment} from '../../environments/environment';
import {ContextDTO} from '../_models';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {SelectContextDialog} from '../dialog/select-context';

@Component({
	moduleId: module.id,
	selector: 'loginComponent',
	styleUrls: ['login.component.css'],
	templateUrl: 'login.component.html'
})

export class LoginComponent implements OnInit {
	model: any = {};
	loading = false;
	returnUrl: string;
	hide: boolean;
	currentUser: any;
	jwtHelper: JwtHelper = new JwtHelper();

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private translate: TranslateService,
		private authenticationService: AuthenticationService,
		private httpService: HttpService,
		private dialog: MatDialog,
		private alertService: AlertService)
	{}

	ngOnInit() {
		this.hide = true;

		// reset login status
		this.authenticationService.logout();

		// get return url from route parameters or default to '/'
		this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
	}

	login() {
		this.loading = true;
		this.httpService.postLogin(this.model.username, this.model.password)
			.subscribe(
				response => {
					const decodedToken = this.jwtHelper.decodeToken(response.headers.get('Authorization'));
					const userId = decodedToken.userID;
					localStorage.setItem('token', response.headers.get('Authorization'));
					localStorage.setItem('currentUser', JSON.stringify({
						firstName: this.model.username,
						token: response.headers.get('Authorization'), userID: userId
					}));
					// after successful login choose the context
					this.getContexts(userId);
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
						this.loading = false;
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.loading = false;

				});
	}

	private getContexts(userId: string) {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'context/' + userId)
			.subscribe(
				data => {
					this.openSelectContextModal(data);
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

	openSelectContextModal(contexts: ContextDTO[]) {
		// If size of the contexts is greater than 0 open dialog
		if (contexts.length > 1) {
			const dialogConfig = new MatDialogConfig();

			dialogConfig.disableClose = true;
			dialogConfig.autoFocus = true;
			dialogConfig.width = '450px';

			dialogConfig.data = {
				id: 1,
				title: this.translate.instant('login.successful'),
				content: this.translate.instant('message.contextDialog'),
				selectMessage: this.translate.instant('message.contextDialog.select'),
				contextList: contexts
			};

			const dialogRef = this.dialog.open(SelectContextDialog, dialogConfig);

			dialogRef.afterClosed().subscribe(result => {
				if (result == true) {
					this.router.navigate([this.returnUrl]);
				}
				else {
					this.authenticationService.logout();
				}
			});
		}
		// If size of the contexts is equal to 1, set currentContext automatically
		else if (contexts.length == 1) {
			localStorage.setItem('context', JSON.stringify(contexts[0]));
			this.currentUser = JSON.parse(localStorage.getItem('currentUser'));

			this.httpService.updateContext(contexts[0].context, this.currentUser.userID);
		}

		// In this case some error occured and user needs to be redirect again to login page, call logout function
		else {
			this.alertService.error(this.translate.instant('server.notresponding'));
			this.authenticationService.logout();
		}
	}

	showPassword() {
		if (this.hide) {
			this.hide = false;
		}
		else {
			this.hide = true;
		}
	}


}
