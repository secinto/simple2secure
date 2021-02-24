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

import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { environment } from '../../../environments/environment';
import { EmailConfiguration } from '../../_models/emailconfig';
import { HttpService } from '../../_services/http.service';


@Component({
    moduleId: module.id,
    styleUrls: ['email.component.css'],
    templateUrl: 'emailAccountAdd.component.html',
    selector: 'emailAccountAdd'
})
export class EmailAccountAddComponent {

    public config: EmailConfiguration;
    loading = false;
    isConfigAdded = false;

    constructor(

        private httpService: HttpService,
        private dialogRef: MatDialogRef<EmailAccountAddComponent>,
        @Inject(MAT_DIALOG_DATA) data) {

        this.config = data.config;

        if (this.config.id) {
            this.isConfigAdded = false;
        } else {
            this.isConfigAdded = true;
        }
    }

    saveConfig() {
        this.httpService.post(this.config, environment.apiEmail).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
            });
        this.loading = false;
    }
}
