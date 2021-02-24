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
import { Processor } from '../../_models/processor';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { UrlParameter } from '../../_models/urlParameter';
import { Step } from '../../_models/step';

@Component({
    moduleId: module.id,
    templateUrl: 'networkStepConfigurationEdit.component.html'
})

export class NetworkStepConfigurationEditComponent {

    step: Step;
    id: string;
    type: number;
    action: string;
    processors: Processor[];


    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private location: Location,
        private dialogRef: MatDialogRef<NetworkStepConfigurationEditComponent>,
        @Inject(MAT_DIALOG_DATA) data
    ) {
        if (data.step == null) {
            this.action = UrlParameter.NEW;
            this.step = new Step();
        } else {
            this.action = UrlParameter.EDIT;
            this.step = data.step;
        }
        this.getProcessorsByGroupId();

    }

    saveStep() {

        this.httpService.post(this.step, environment.apiSteps).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
            });
    }

    getProcessorsByGroupId() {
        this.httpService.get(environment.apiProcessors)
            .subscribe(
                data => {
                    this.processors = data;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
    }

    cancel() {
        this.location.back();
    }
}
