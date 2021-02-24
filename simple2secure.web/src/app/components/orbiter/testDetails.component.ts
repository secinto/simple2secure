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
import { TestObjWeb } from '../../_models/testObjWeb';
import { Timeunit } from '../../_models/timeunit';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { Parameter } from '../../_models/parameter';
import { OrbiterComponent } from './orbiter.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
    moduleId: module.id,
    templateUrl: 'testDetails.component.html'
})

export class TestDetailsComponent extends OrbiterComponent {
    loading = false;
    test: TestObjWeb;
    type: string;
    isNewTest = false;
    url: string;
    timeUnits = Timeunit;
    isReadOnly = false;

    constructor(
        private alertService: AlertService,
        private dialogRef: MatDialogRef<TestDetailsComponent>,
        private httpService: HttpService,
        dialog: MatDialog,
        @Inject(MAT_DIALOG_DATA) data) {

        super(dialog);

        this.type = data.type;
        if (
            this.type == 'new') {
            this.isNewTest = true;
            this.test = new TestObjWeb();
            this.test.podId = data.podId;
            this.test.testId = null;
        } else {
            this.test = data.tests.test;
        }
    }

    extractTimeUnits(): Array<string> {
        const keys = Object.keys(this.timeUnits);
        return keys.slice();
    }


    public updateSaveTest() {
        this.loading = true;

        this.httpService.post(this.test, environment.apiTestSave).subscribe(
            data => {
                if (this.type === 'new') {
                    this.alertService.showSuccessMessage(data, 'message.test.create');
                } else {
                    this.alertService.showSuccessMessage(data, 'message.test.update');
                }
                this.close(true);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
                this.close(true);
            });
    }


    public close(value: boolean) {
        this.dialogRef.close(value);
    }

    public addParameter(part: string) {
        if (part == 'precondition') {
            this.test.test_content.test_definition.precondition.command.parameter.push(new Parameter());
        }
        else if (part == 'step') {
            this.test.test_content.test_definition.step.command.parameter.push(new Parameter());
        }
        else if (part == 'postcondition') {
            this.test.test_content.test_definition.postcondition.command.parameter.push(new Parameter());
        }
    }

    public removeParameter(part: string, index: number) {
        if (part == 'precondition') {
            this.test.test_content.test_definition.precondition.command.parameter.splice(index, 1);
        }
        else if (part == 'step') {
            this.test.test_content.test_definition.step.command.parameter.splice(index, 1);
        }
        else if (part == 'postcondition') {
            this.test.test_content.test_definition.postcondition.command.parameter.splice(index, 1);
        }
    }
}
