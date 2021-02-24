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

import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpService } from '../../_services/http.service';
import { Notification } from '../../_models/notification';
import { AuthenticationService } from '../../_services/authentication.service';

@Component({
    moduleId: module.id,
    templateUrl: 'notification.component.html',
    selector: 'notificationList',
    styleUrls: ['notification.scss'],
})

export class NotificationComponent {

    notifications: Notification[];
    url: string;
    dataRefresher: any;

    constructor(private httpService: HttpService,
        private authService: AuthenticationService) {
    }

    ngOnInit() {
        if (this.authService.isLoggedIn) {
            this.getNotifications();
            this.refreshNotifications();
        }
    }

    ngDoCheck() {
        if (!this.authService.isLoggedIn) {
            this.notifications = [];
            this.dataRefresher.unsubscribe();
        }
    }

    public getNotifications() {
        if (this.authService.isLoggedIn) {
            this.httpService.get(environment.apiNotification)
                .subscribe(
                    data => {
                        this.notifications = data;
                    },
                    error => {
                    });
        }
    }

    isRead(notification: Notification) {
        if (this.authService.isLoggedIn) {
            if (!notification.read && this.authService.isLoggedIn) {
                this.httpService.post(notification, environment.apiNotificationRead).subscribe(
                    data => {
                        notification.read = true;
                    },
                    error => {
                    });
            }
        }
    }

    refreshNotifications() {
        if (this.authService.isLoggedIn) {
            this.dataRefresher =
                setInterval(() => {
                    this.getNotifications();
                }, 60000);
        }
    }
}
