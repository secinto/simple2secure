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

import { Component, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig, MatMenuTrigger } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { SelectContextDialog } from '../dialog/select-context';
import { Title } from '@angular/platform-browser';
import { ContextDTO } from '../../_models/DTO/contextDTO';
import { HttpService } from '../../_services/http.service';
import { UserRole } from '../../_models/userRole';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';

@Component({
    moduleId: module.id,
    templateUrl: 'sidenavbar.component.html',
    styleUrls: ['sidenavbar.component.css'],
    selector: 'sidenavbar'
})

export class SidenavbarComponent {
    @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;
    pageTitle: string;
    loggedIn: boolean;
    showSettings: boolean;
    returnUrl: string;
    showReportsSubmenu: boolean;
    showEmailsSubmenu: boolean;
    showOrbiterSubmenu: boolean;
    showDevicesSubmenu: boolean;
    showQueriesSubmenu: boolean;
    showRulesSubmenu: boolean;
    userRole: string;

    constructor(private translate: TranslateService,
        private router: Router,
        private route: ActivatedRoute,
        private httpService: HttpService,
        private dataService: DataService,
        private alertService: AlertService,
        private titleService: Title,
        private dialog: MatDialog) {
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
        this.showReportsSubmenu = false;
        this.showEmailsSubmenu = false;
        this.showOrbiterSubmenu = false;
        this.showDevicesSubmenu = false;
        this.showQueriesSubmenu = false;
        this.showRulesSubmenu = false;
    }

    ngDoCheck() {
        this.pageTitle = this.titleService.getTitle();
        this.userRole = this.dataService.getRole();

        if (this.userRole) {
            this.loggedIn = true;
            this.showSettings = false;
            if (this.userRole == UserRole.SUPERADMIN) {
                this.showSettings = true;
            }
        } else {
            this.loggedIn = false;
        }
    }

    changeContext() {
        // if number of contexts is greater than 1 open dialog to change context
        this.getContexts();
    }

    private getContexts() {
        this.httpService.get(environment.apiContext)
            .subscribe(
                data => {
                    this.openSelectContextModal(data);
                },
                error => {
                    this.alertService.showErrorMessage(error);
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

                    this.router.routeReuseStrategy.shouldReuseRoute = function () {
                        return false;
                    };
                    this.router.navigated = false;
                    this.router.navigate([this.returnUrl]);
                } else {
                    this.httpService.logout();
                }
            });
        }
        // If size of the contexts is equal to 1, set currentContext automatically
        else if (contexts.length == 1) {
            this.alertService.showErrorMessage(null, false, 'message.contextChangeError');
        }

        // In this case some error occured and user needs to be redirect again to login page, call logout function
        else {
            this.alertService.showErrorMessage(null, false, 'server.notresponding');
            this.httpService.logout();
        }
    }

    collapseMenu(parent: string) {
        if (parent == 'menu-reports') {
            this.showReportsSubmenu = true;
            this.showEmailsSubmenu = false;
            this.showOrbiterSubmenu = false;
            this.showDevicesSubmenu = false;
            this.showQueriesSubmenu = false;
            this.showRulesSubmenu = false;
        } else if (parent == 'menu-emails') {
            this.showEmailsSubmenu = true;
            this.showReportsSubmenu = false;
            this.showOrbiterSubmenu = false;
            this.showDevicesSubmenu = false;
            this.showQueriesSubmenu = false;
            this.showRulesSubmenu = false;
        } else if (parent == 'menu-orbiter') {
            this.showOrbiterSubmenu = true;
            this.showReportsSubmenu = false;
            this.showEmailsSubmenu = false;
            this.showDevicesSubmenu = false;
            this.showQueriesSubmenu = false;
            this.showRulesSubmenu = false;
        } else if (parent == 'menu-devices') {
            this.showDevicesSubmenu = true;
            this.showOrbiterSubmenu = false;
            this.showReportsSubmenu = false;
            this.showEmailsSubmenu = false;
            this.showQueriesSubmenu = false;
            this.showRulesSubmenu = false;
        } else if (parent == 'menu-queries') {
            this.showQueriesSubmenu = true;
            this.showDevicesSubmenu = false;
            this.showOrbiterSubmenu = false;
            this.showReportsSubmenu = false;
            this.showEmailsSubmenu = false;
            this.showRulesSubmenu = false;
        } else if (parent == 'menu-rules') {
            this.showQueriesSubmenu = false;
            this.showDevicesSubmenu = false;
            this.showOrbiterSubmenu = false;
            this.showReportsSubmenu = false;
            this.showEmailsSubmenu = false;
            this.showRulesSubmenu = true;
        }
    }
}
