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

@Component({
    moduleId: module.id,
    templateUrl: 'userGroupApplyConfig.component.html',
    selector: 'UserGroupApplyConfig',
    providers: [DatePipe]
})

export class UserGroupApplyConfigComponent {
    public destGroup: CompanyGroup;
    groups: CompanyGroup[];
    sourceGroup: CompanyGroup;
    url: string;
    loading = false;

    constructor(
        private httpService: HttpService,
        private alertService: AlertService,
        private dialogRef: MatDialogRef<UserGroupApplyConfigComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
        this.destGroup = data.destGroup;
    }

    ngOnInit() {
        this.loadGroups();
    }

    private loadGroups() {
        this.httpService.get(environment.apiGroup)
            .subscribe(
                data => {
                    this.extractGroups(data);
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
    }

    applyConfig() {
        const apiUrl = environment.apiGroupCopy.replace('{groupId}', this.sourceGroup.id);
        this.httpService.post(this.destGroup, apiUrl).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
                this.loading = false;
            });
    }

    extractGroups(groups: CompanyGroup[]) {
        this.groups = [];
        for (let i = 0; i < groups.length; i++) {
            if (groups[i].id == this.destGroup.id) {
            } else {
                this.groups.push(groups[i]);
            }
        }
    }
}
