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
import { HttpService } from '../../_services/http.service';

@Component({
    selector: 'add-query-dialog',
    templateUrl: 'addQueryDialog.html'
})

export class AddQueryDialog {

    selectedQuery: any;
    deviceId: any;

    constructor(private dialogRef: MatDialogRef<AddQueryDialog>,
        private httpService: HttpService,
        @Inject(MAT_DIALOG_DATA) public data: any) {
        this.deviceId = data.deviceId;
    }

    selectQuery() {
        const apiUrl = environment.apiReportsByDeviceIdName.replace('{deviceId}', this.deviceId);
        const apiUrlFull = apiUrl.replace('{name}', this.selectedQuery.name);

        this.httpService.get(apiUrlFull)
            .subscribe(
                dataAPI => {
                    this.dialogRef.close(dataAPI);
                });
    }
}
