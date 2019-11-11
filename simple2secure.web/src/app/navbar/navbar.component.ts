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

declare var $: any;

export interface Language {
	value: string;
	viewValue: string;
	localeVal: string;
}

@Component({
	moduleId: module.id,
	templateUrl: 'navbar.component.html',
	styleUrls: ['navbar.component.css'],
	selector: 'navbar'
})

export class NavbarComponent {
	@ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;
	currentUser: any;
	currentContext: ContextDTO;
	notifications: Notification[];
	numOfUnreadNotification: number;
	loggedIn: boolean;
	currentLang: string;
	showSettings: boolean;
	returnUrl: string;
	private timer;
	showNotifications: boolean;
	showUserModal: boolean;

	languages: Language[] = [
		{value: 'en', viewValue: 'English', localeVal: 'EN'},
		{value: 'de', viewValue: 'German', localeVal: 'DE'}
	];

	constructor(private translate: TranslateService,
	            private router: Router,
	            private route: ActivatedRoute,
	            private httpService: HttpService,
	            private dataService: DataService,
	            private alertService: AlertService,
	            private authenticationService: AuthenticationService,
	            private dialog: MatDialog)
	{
		this.showNotifications = false;
		this.showUserModal = false;
		this.notifications = [];
		this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
		this.timer = Observable.timer(0, 10000);
		this.timer.subscribe((t) => this.getNotifications());
	}

	public getNotifications() {
		if (this.loggedIn){
			this.httpService.get(environment.apiEndpoint + 'notification/' + this.currentContext.context.id)
				.subscribe(
					data => {
						this.notifications = data;
						this.dataService.setNotifications(this.notifications);
						this.countunreadNotifications(this.notifications);
					},
					error => {
					});
		}
	}

	public countunreadNotifications(notifications: Notification[]){
		this.numOfUnreadNotification = 0;

		for (let i = 0; i < notifications.length; i++) {
			if (!notifications[i].read){
				this.numOfUnreadNotification++;
			}
		}
	}

	ngDoCheck() {

		this.currentLang = this.translate.currentLang;
		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}

		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.currentContext = JSON.parse(localStorage.getItem('context'));


		if (this.currentUser && this.currentContext) {
			this.loggedIn = true;

			if (this.currentContext.userRole == UserRole.SUPERADMIN) {
				this.showSettings = true;

			}
			else {
				this.showSettings = false;
			}
		}
		else {
			this.loggedIn = false;
		}
	}

	ngAfterViewInit() {

	}

	public setLocale(lang: string) {
		this.translate.use(lang);
	}

	openNotificationModal() {
		this.showUserModal = false;
		if (this.showNotifications == true){
			this.showNotifications = false;
		}
		else{
			this.showNotifications = true;
		}
	}

	openUserModal(){
		this.showNotifications = false;
		if (this.showUserModal == true){
			this.showUserModal = false;
		}
		else{
			this.showUserModal = true;
		}
	}


}
