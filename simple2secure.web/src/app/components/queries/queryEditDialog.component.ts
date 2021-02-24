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
import { OsQuery } from '../../_models/osQuery';
import { Timeunit } from '../../_models/timeunit';
import { HttpService } from '../../_services/http.service';


@Component({
    moduleId: module.id,
    templateUrl: './queryEditDialog.component.html'
})

export class QueryEditDialogComponent {

    queryRun: OsQuery;
    windows: boolean;
    linux: boolean;
    macos: boolean;
    timeUnits = Timeunit;


    constructor(
        private httpService: HttpService,
        private dialogRef: MatDialogRef<QueryEditDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data
    ) {
        this.windows = false;
        this.linux = false;
        this.macos = false;
        if (data.queryRun == null) {
            this.queryRun = new OsQuery();
            this.queryRun.categoryId = data.queryCategory.id;
        } else {
            this.queryRun = data.queryRun;
            if (data.systemsAvailable === 1 || data.systemsAvailable === 3 || data.systemsAvailable === 5 || data.systemsAvailable === 7) {
                this.windows = true;
            }
            if (data.systemsAvailable === 2 || data.systemsAvailable === 3 || data.systemsAvailable === 6 || data.systemsAvailable === 7) {
                this.linux = true;
            }
            if (data.systemsAvailable === 4 || data.systemsAvailable === 5 || data.systemsAvailable === 6 || data.systemsAvailable === 7) {
                this.macos = true;
            }
        }

    }

    extractTimeUnits(): Array<string> {
        const keys = Object.keys(this.timeUnits);
        return keys.slice();
    }

    saveQueryRun() {

        this.systemsValue();

        this.httpService.post(this.queryRun, environment.apiQueries).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
            });
    }

    systemsValue() {
        let test = 0;

        if (this.windows) {
            test = test + 1;
        }
        if (this.linux) {
            test = test + 2;
        }
        if (this.macos) {
            test = test + 4;
        }

        this.queryRun.systemsAvailable = test;

    }
}
