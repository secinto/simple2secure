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

import {Component} from '@angular/core';
import {environment} from '../../environments/environment';
import {DataService, HttpService} from '../_services';
import {Notification} from '../_models';

@Component({
	moduleId: module.id,
	templateUrl: 'notification.component.html',
	selector: 'notificationList',
	styleUrls: ['notification.scss'],
})

export class NotificationComponent {

	notifications: Notification[];
	url: string;

	constructor(private httpService: HttpService,
	            private dataService: DataService){
	}

	ngDoCheck() {
		this.notifications = this.dataService.getNotifications();
	}

	isRead(notification: Notification){

		if (!notification.read){
			// console.log('Notification ID ' + notification.id);
			this.url = environment.apiEndpoint + 'notification/read';
			this.httpService.post(notification, this.url).subscribe(
				data => {
					notification.read = true;
				},
				error => {
				});
		}

	}
}
