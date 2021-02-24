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
import { Location } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { OsQueryCategory } from '../../_models/osQueryCategory';
import { HttpService } from '../../_services/http.service';


@Component({
    moduleId: module.id,
    templateUrl: 'queryCategoryAddDialog.component.html',
})

export class QueryCategoryAddDialog {
    loading = false;
    url: string;
    category = new OsQueryCategory();
    isNewCategory: boolean;
    windows: boolean;
    linux: boolean;
    macos: boolean;

    constructor(
        private httpService: HttpService,
        private location: Location,
        private dialogRef: MatDialogRef<QueryCategoryAddDialog>,
        @Inject(MAT_DIALOG_DATA) data) {
        if (data.context != null) {
            this.category = data.context;
            this.isNewCategory = false;
        } else {
            this.isNewCategory = true;
        }
    }

    saveCategory() {
        this.systemsValue();
        this.loading = true;

        this.httpService.post(this.category, environment.apiQueriesCategory).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
                this.loading = false;
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

        this.category.systemsAvailable = test;

    }

    cancel() {
        this.location.back();
    }
}
