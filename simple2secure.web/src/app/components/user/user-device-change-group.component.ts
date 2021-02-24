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
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Device } from '../../_models/device';
import { HttpService } from '../../_services/http.service';
import { CompanyGroup } from '../../_models/companygroup';
import { AlertService } from '../../_services/alert.service';

@Component({
    moduleId: module.id,
    templateUrl: 'user-device-change-group.component.html',
    selector: 'UserDeviceChangeGroupComponent',
})

export class UserDeviceChangeGroupComponent {
    loading = false;
    id: string;
    url: string;
    groups: CompanyGroup[];
    device: Device;
    selectedGroup: CompanyGroup;

    constructor(
        private httpService: HttpService,
        private alertService: AlertService,
        private dialogRef: MatDialogRef<UserDeviceChangeGroupComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
        this.device = data;
        this.selectedGroup = this.device.group;
    }

    ngOnInit() {
        this.loadGroups();

    }

    private loadGroups() {
        this.httpService.get(environment.apiGroupByContext)
            .subscribe(
                data => {
                    this.extractGroups(data);
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
    }

    public changeGroup() {
        this.loading = true;
        const apiUrl = environment.apiDevicesChangeGroup.replace('{deviceId}', this.device.info.id);
        this.httpService.post(this.selectedGroup, apiUrl).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
                this.loading = false;
            });
    }

    public extractGroups(groups: CompanyGroup[]) {
        this.groups = [];
        for (let i = 0; i < groups.length; i++) {
            if (groups[i].id == this.selectedGroup.id) {
            } else {
                this.groups.push(groups[i]);
            }
        }
    }
}
