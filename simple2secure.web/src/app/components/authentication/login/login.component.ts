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
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthenticationService } from '../../../_services/authentication.service';
import { HttpService } from '../../../_services/http.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DataService } from '../../../_services/data.service';
import { AlertService } from '../../../_services/alert.service';
import { UserLogin } from '../../../_models/userLogin';
import { environment } from '../../../../environments/environment';
import { SelectContextDialog } from '../../dialog/select-context';
import { ContextDTO } from '../../../_models/DTO/contextDTO';
import { TokenObject } from '../../../_models/tokenObject';
import { HttpErrorResponse } from '@angular/common/http';
import { RegisterComponent } from '../register/register.component';
import { ForgotPasswordComponent } from '../forgotPassword/forgotPassword.component';

@Component({
    moduleId: module.id,
    selector: 'loginComponent',
    styleUrls: ['login.component.css'],
    templateUrl: 'login.component.html'
})

export class LoginComponent {
    loading = false;
    userLogin = new UserLogin();
    hide: boolean;
    returnUrl: string;
    userLoginResponse: TokenObject;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private translate: TranslateService,
        private authService: AuthenticationService,
        private httpService: HttpService,
        private dialog: MatDialog,
        private alertService: AlertService,
        private dataService: DataService
    ) {
        this.hide = true;
        // get return url from route parameters or default to '/'
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }

    login() {
        this.loading = true;
        this.httpService.postLogin(this.userLogin, environment.apiUserLogin).shareReplay()
            .subscribe(
                data => {
                    this.userLoginResponse = data;
                    this.dataService.setAuthToken(this.userLoginResponse.token);

                    // after successful login choose the context
                    this.getContexts();

                    this.authService.isLoggedIn = true;
                },
                error => {
                    this.httpService.logout();
                    this.alertService.showErrorMessage(error);
                    this.loading = false;

                });
    }

    openDialogRegister(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '350px';

        dialogConfig.data = {
        };
        const dialogRef = this.dialog.open(RegisterComponent, dialogConfig);

    }

    openDialogForgotPwd(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '350px';

        dialogConfig.data = {
        };
        const dialogRef = this.dialog.open(ForgotPasswordComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.context.add');
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    private getContexts() {
        this.loading = true;
        this.httpService.get(environment.apiContext)
            .subscribe(
                data => {
                    this.openSelectContextModal(data);
                },
                error => {

                    this.alertService.showErrorMessage(error);
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
                    this.httpService.logout();
                }
            });
        }
        // If size of the contexts is equal to 1, set currentContext automatically
        else if (contexts.length == 1) {
            this.dataService.setRole(contexts[0].userRole);
            this.httpService.updateContext(contexts[0].context);
        }

        // In this case some error occured and user needs to be redirect again to login page, call logout function
        else {
            this.alertService.showErrorMessage(null, false, 'server.notresponding');
            this.httpService.logout();
        }
    }

    showPassword() {
        this.hide = !this.hide;
    }


}
