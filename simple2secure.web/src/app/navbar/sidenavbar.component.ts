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

import {ViewChild, Component} from '@angular/core';
import {MatDialog, MatDialogConfig, MatMenuTrigger} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {ContextDTO, UserRole, Notification} from '../_models';
import {environment} from '../../environments/environment';
import {SelectContextDialog} from '../dialog/select-context';
import {AlertService, AuthenticationService, DataService, HttpService} from '../_services';
import {FormControl} from '@angular/forms';
import {Title} from '@angular/platform-browser';

@Component({
	moduleId: module.id,
	templateUrl: 'sidenavbar.component.html',
	styleUrls: ['sidenavbar.component.css'],
	selector: 'sidenavbar'
})

export class SidenavbarComponent {
	@ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;
	currentUser: any;
	currentContext: ContextDTO;
	pageTitle: string;
	loggedIn: boolean;
	showSettings: boolean;
	returnUrl: string;
	showTitle: boolean;
	searchValue: string;

	constructor(private translate: TranslateService,
	            private router: Router,
	            private route: ActivatedRoute,
	            private httpService: HttpService,
	            private dataService: DataService,
	            private alertService: AlertService,
				private titleService: Title,
	            private authenticationService: AuthenticationService,
	            private dialog: MatDialog)
	{
		this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
	}

	ngDoCheck() {
		this.pageTitle = this.titleService.getTitle();
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.currentContext = JSON.parse(localStorage.getItem('context'));

		if (this.pageTitle == 'simple2secure Portal'){
			this.showTitle = false;
		}
		else{
			this.showTitle = true;
		}

		if (this.currentUser && this.currentContext) {
			this.loggedIn = true;
			this.showSettings = false;
			if (this.currentContext.userRole == UserRole.SUPERADMIN) {
				this.showSettings = true;
			}
		}
		else {
			this.loggedIn = false;
		}
	}

	changeContext() {
		// if number of contexts is greater than 1 open dialog to change context
		this.getContexts(this.currentUser.userID);
	}

	private getContexts(userId: string) {
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
				title: this.translate.instant('change.context'),
				content: this.translate.instant('message.contextDialogDashboard'),
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
			this.alertService.error(this.translate.instant('message.contextChangeError'));
		}

		// In this case some error occured and user needs to be redirect again to login page, call logout function
		else {
			this.alertService.error(this.translate.instant('server.notresponding'));
			this.authenticationService.logout();
		}
	}

	navigateToTheSearchPage() {
		if (this.searchValue.trim()){
			this.router.navigate(['search', this.searchValue]);
		}
	}
}
