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
import { UserRegistration } from '../../../_models/userRegistration';
import { HttpService } from '../../../_services/http.service';
import { AlertService } from '../../../_services/alert.service';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
    moduleId: module.id,
    styleUrls: ['../login/login.component.css'],
    templateUrl: 'register.component.html'
})

export class RegisterComponent {
    user: UserRegistration;
    loading: boolean;

    constructor(
        private httpService: HttpService,
        private alertService: AlertService,
        private dialogRef: MatDialogRef<RegisterComponent>) {
        this.user = new UserRegistration();
        this.loading = false;
    }

    register() {
        this.httpService.postRegister(this.user)
            .subscribe(
                data => {
                    this.alertService.showSuccessMessage(data, 'message.registration', true);
                    this.dialogRef.close();
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.dialogRef.close();
                });
    }
}
