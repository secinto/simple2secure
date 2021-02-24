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
import { environment } from '../../../environments/environment';
import { DatePipe } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { HttpService } from '../../_services/http.service';
import { CompanyGroup } from '../../_models/companygroup';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';

@Component({
    moduleId: module.id,
    templateUrl: 'userGroupEdit.component.html',
    selector: 'UserGroupEditComponent',
    providers: [DatePipe]
})

export class UserGroupEditComponent {
    public group: CompanyGroup;
    loading = false;
    url: string;
    groupEditable: boolean;

    constructor(

        private httpService: HttpService,
        private dataService: DataService,
        private alertService: AlertService,
        private dialogRef: MatDialogRef<UserGroupEditComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
        this.group = data.group;
    }

    ngOnInit() {
        this.groupEditable = this.dataService.isGroupEditable();
    }

    saveGroup() {
        this.loading = true;
        const apiUrl = environment.apiGroupGroupId.replace('{groupId}', 'null');
        this.httpService.post(this.group, apiUrl).subscribe(
            data => {
                this.group = data;
                this.alertService.showSuccessMessage(data, 'message.user.group.update');
            },
            error => {
                this.alertService.showErrorMessage(error);
            });

        this.loading = false;
        this.dialogRef.close();
    }
}
