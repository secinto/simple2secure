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
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { DatePipe, Location } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Context } from '../../_models/context';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';

@Component({
    moduleId: module.id,
    templateUrl: 'userContextAddDialog.component.html',
    selector: 'userContextAddDialog',
    providers: [DatePipe]
})

export class UserContextAddDialogComponent {
    loading = false;
    id: string;
    private sub: any;
    url: string;
    isDialogOpen: boolean;
    context = new Context();
    isNewContext: boolean;

    constructor(
        private route: ActivatedRoute,
        private httpService: HttpService,
        private location: Location,
        private dialogRef: MatDialogRef<UserContextAddDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
        if (data.context != null) {
            this.context = data.context;
            this.isNewContext = false;
        } else {
            this.isNewContext = true;
        }
    }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.id = params['id'];
        });
    }

    saveContext() {
        this.loading = true;
        this.httpService.post(this.context, environment.apiContextAdd).subscribe(
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
