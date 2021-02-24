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

import { Component, DoCheck, ViewChild } from '@angular/core';
import { MatMenuTrigger } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { HttpService } from '../../_services/http.service';
import { AuthenticationService } from '../../_services/authentication.service';

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

export class NavbarComponent implements DoCheck {
    @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;
    notifications: Notification[];
    numOfUnreadNotification: number;
    loggedIn: boolean;
    userRole: string;
    currentLang: string;
    returnUrl: string;
    showNotifications: boolean;
    showUserModal: boolean;
    searchValue: string;

    languages: Language[] = [
        { value: 'en', viewValue: 'English', localeVal: 'EN' },
        { value: 'de', viewValue: 'German', localeVal: 'DE' }
    ];

    constructor(private translate: TranslateService,
        private router: Router,
        private route: ActivatedRoute,
        private httpService: HttpService,
        private authService: AuthenticationService) {
        this.showNotifications = false;
        this.showUserModal = false;
        this.notifications = [];
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }

    public getNumOfUnreadNotifications() {
        if (this.authService.isLoggedIn) {
            this.httpService.get(environment.apiNotificationRead)
                .subscribe(
                    data => {
                        this.numOfUnreadNotification = data;
                    },
                    error => {
                    });
        }
    }

    ngDoCheck() {
        this.currentLang = this.translate.currentLang;
        if (!this.currentLang) {
            this.currentLang = this.translate.defaultLang;
        }

        if (this.authService.isLoggedIn) {
            this.loggedIn = true;
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
        if (this.showNotifications == true) {
            this.showNotifications = false;
        } else {
            this.showNotifications = true;
        }
    }

    navigateToTheSearchPage() {
        if (this.searchValue.trim()) {
            this.router.navigate(['search', this.searchValue]);
        }
    }

    logout() {
        this.httpService.logout();
    }
}
