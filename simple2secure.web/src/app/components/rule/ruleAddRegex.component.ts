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


import { Component, ElementRef, Inject, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location, LocationStrategy } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from '../../../environments/environment';
import { RuleRegex } from '../../_models/ruleRegex';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';
import { CheckRegexNameDTO } from '../../_models/DTO/checkRegexNameDTO';
import { RegexTestDTO } from '../../_models/DTO/regexTestDTO';


@Component({
    moduleId: module.id,
    styleUrls: ['rule.component.css'],
    templateUrl: 'ruleAddRegex.component.html',
})


export class RuleAddRegexComponent {
    regexObject: RuleRegex;
    name: string;
    description: string;
    regex: string;
    testText: string;
    result: string;

    dialogTitle: string;
    loading = false;

    @ViewChild('test_result') testResultOutput: ElementRef;


    constructor(
        private httpService: HttpService,
        private alertService: AlertService,
        private translate: TranslateService,
        private dialogRef: MatDialogRef<RuleAddRegexComponent>,
        private snackBar: MatSnackBar,
        @Inject(MAT_DIALOG_DATA) data) {
        this.dialogRef.disableClose = true;
        if (data && data.regex) {
            this.regexObject = data.regex;
            this.name = data.regex.name;
            this.description = data.regex.description;
            this.regex = data.regex.regex;
            this.dialogTitle = this.translate.instant('title.regex.edit');
        }
        else {
            this.regexObject = new RuleRegex();
            this.dialogTitle = this.translate.instant('title.regex.add');
        }
    }

    /**
     * Method to check if given strung is empty (whitespace also count as empty) or undefined
     * @param _string which should be checked
     */
    private isStringEmptyOrUndefined(_string: string) {
        if (!_string || _string.replace(/\s/g, '').length == 0)
            return true;
        return false;
    }


    /**
     * Opens a snackbar and displays message and action
     * @param message which should be displayed
     * @param action which should be performed when clicked
     */
    private openSnackbar(message: string, action: any) {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

    /**
     * Function checks if the given regex name is already used
     * @param regexName
     */
    public lostFocusRegexName(regexName: any) {
        if (this.isStringEmptyOrUndefined(regexName.value))
            return;

        const dto = new CheckRegexNameDTO();
        dto.name = regexName.value;
        dto.regexId = this.regexObject.id;
        this.httpService.post(dto, environment.apiRuleRegexCheckName).subscribe(
            data => {
                if (data == true) {
                    regexName.value = null;
                    this.openSnackbar(this.translate.instant('rule.regex.name.already.used'), '');
                }
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.openSnackbar(error.error.errorMessage, '');
            });
    }

    /**
     * Method to test the given regex mathes the given text
     */
    public testRegex() {
        // check if regex is given
        if (this.isStringEmptyOrUndefined(this.regex)) {
            this.openSnackbar(this.translate.instant('rule.regex.notgiven'), '');
            return;
        }

        const regexTestDTO = new RegexTestDTO();
        regexTestDTO.regex = this.regex;
        if (this.testText == undefined)
            this.testText = '';
        regexTestDTO.testText = this.testText;

        // testing if the given text matches the regex
        this.httpService.post(regexTestDTO, environment.apiRuleRegexTest).subscribe(
            data => {
                if (data == true) {
                    this.testResultOutput.nativeElement.value = this.translate.instant('rule.regex.test.true');
                }
                else if (data == false) {
                    this.testResultOutput.nativeElement.value = this.translate.instant('rule.regex.test.false');
                }
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.dialogRef.close(error);
            });
    }


    /**
     * Saves the new/updated regex if all needed parameter are given by the user
     */
    public saveRegex() {


        // check if name is given
        if (this.isStringEmptyOrUndefined(this.name)) {
            this.openSnackbar(this.translate.instant('rule.name.notgiven'), '');
            return;
        }

        // check if description is given
        if (this.isStringEmptyOrUndefined(this.description)) {
            this.openSnackbar(this.translate.instant('rule.description.notgiven'), '');
            return;
        }

        // check if regex is given
        if (this.isStringEmptyOrUndefined(this.regex)) {
            this.openSnackbar(this.translate.instant('rule.regex.notgiven'), '');
            return;
        }


        this.regexObject.name = this.name;
        this.regexObject.description = this.description;
        this.regexObject.regex = this.regex;



        // tries to save the rule
        this.httpService.post(this.regexObject, environment.apiRuleRegex).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.dialogRef.close(error);
            });
    }
}
