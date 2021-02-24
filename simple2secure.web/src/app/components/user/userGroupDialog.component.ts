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
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { DatePipe, Location } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { HttpService } from '../../_services/http.service';
import { CompanyGroup } from '../../_models/companygroup';

@Component({
    moduleId: module.id,
    templateUrl: 'userGroupDialog.component.html',
    selector: 'UserGroupDialogComponent',
    providers: [DatePipe]
})

export class UserGroupDialogComponent {
    public group: CompanyGroup;
    loading = false;
    id: string;
    private sub: any;
    url: string;
    parentGroup: CompanyGroup;
    parentGroupId: string;

    constructor(
        private route: ActivatedRoute,
        private httpService: HttpService,
        private location: Location,
        private dialogRef: MatDialogRef<UserGroupDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
        this.group = new CompanyGroup();
        this.parentGroup = data;
    }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.id = params['id'];
        });
    }

    saveGroup() {
        this.loading = true;
        if (!this.parentGroup) {
            this.parentGroupId = null;
        } else {
            this.parentGroupId = this.parentGroup.id;
        }
        const apiUrl = environment.apiGroupGroupId.replace('{groupId}', this.parentGroupId);
        this.httpService.post(this.group, apiUrl).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
                this.loading = false;
            });
    }

    cancel() {
        this.location.back();
    }
}
