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

import { Component, Inject, Input } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatSnackBar, MatSelect, MatStepper } from '@angular/material';
import { Router, ActivatedRoute } from '@angular/router';
import { LocationStrategy, Location } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { FormBuilder } from '@angular/forms';
import { RuleDTO } from '../../_models/DTO/ruleDTO';
import { TemplateRule } from '../../_models/templateRule';
import { TemplateCondition } from '../../_models/templateCondition';
import { TemplateAction } from '../../_models/templateAction';
import { RuleConditionActionTemplatesDTO } from '../../_models/DTO/ruleConditionActionTemplatesDTO';
import { DataType } from '../../_models/dataType';
import { RuleFactType } from '../../_models/ruleFactType';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';
import { ConditionExpressionDTO } from '../../_models/DTO/conditionExpressionDTO';
import { environment } from '../../../environments/environment';
import { CheckRuleNameDTO } from '../../_models/DTO/checkRuleNameDTO';

@Component({
    moduleId: module.id,
    styleUrls: ['rule.component.css'],
    templateUrl: 'ruleAdd.component.html',
    selector: 'addRule',

})

export class RuleAddComponent {
    ruleDTO: RuleDTO;
    ruleName: string;
    ruleId: string;
    ruleDescription: string;
    // rule which will be built with predefined actions/conditions
    ruleTemplate: TemplateRule;
    // predefined template conditions which will be fetched from the database
    allTemplateConditions: TemplateCondition[];
    // predefined template actions which will be fetched from the database
    allTemplateActions: TemplateAction[];
    // type limit
    typeLimit: number;
    // condition expression
    conditionExpression: string;
    // selected (by the user) condition which will be display
    selectedConditions: TemplateCondition[];
    // selected (by the user) action which will be display
    selectedActions: TemplateAction[];
    // object of the enum for the params, will be needed in the html, otherwise the enum cant be find
    dataType = DataType;
    // title which will be displayed at the head of the dialog
    dialogTitle: string;
    loading = false;
    // chosen email configs for mapping rule to email
    emailConfigsChosen: boolean[];
    // DTO to fetch all available condtions/actions
    ruleConditionActionTemplatesDTO: RuleConditionActionTemplatesDTO;
    // boolean to disable stepper in lenear mode
    isLinear: boolean;
    stringHelper: String;
    ruleFactType: RuleFactType;

    constructor(
        private httpService: HttpService,
        private alertService: AlertService,
        private translate: TranslateService,
        private dialogRef: MatDialogRef<RuleAddComponent>,
        private snackBar: MatSnackBar,
        @Inject(MAT_DIALOG_DATA) data) {
        this.dialogRef.disableClose = true;
        this.ruleDTO = data.ruleDTO;
        this.emailConfigsChosen = new Array();

        // if a rule should be edited the old data of the rule will be displayed
        if (data.rule) {
            // enable linear mode when editing a rule
            this.isLinear = false;
            // saving the old data
            this.ruleTemplate = data.rule;
            // set the title to "editing rule"
            this.dialogTitle = this.translate.instant('rule.edit');
            // loading old name and description for the gui
            this.ruleName = data.rule.name;
            this.ruleId = data.rule.id;
            this.ruleDescription = data.rule.description;
            this.typeLimit = data.rule.limit;
            this.conditionExpression = data.rule.conditionExpression;
            // displays the old condition
            this.selectedConditions = this.ruleTemplate.templateConditions;
            // displays action
            this.selectedActions = this.ruleTemplate.templateActions;

        }
        else // a new rule will be added
        {
            // disable linear mode when a new rule is created
            this.isLinear = true;
            // set the title to "editing rule"
            this.dialogTitle = this.translate.instant('button.addRule');
            this.ruleTemplate = new TemplateRule();
            this.selectedActions = new Array();
            this.selectedConditions = new Array();
        }

        this.ruleFactType = data.ruleFactType;

        // fetches the template actions/conditions from the database
        this.getTemplates();
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
    }

    /**
     * Method to load template actions and conditions from the database
     */
    private getTemplates() {
        const apiUrl = environment.apiRuleTemplateCondActionFactType.replace('{ruleFactType}', this.ruleFactType.toString());
        this.httpService.get(apiUrl)
            .subscribe(
                data => {
                    this.ruleConditionActionTemplatesDTO = data;
                    this.allTemplateActions = this.ruleConditionActionTemplatesDTO.actions;
                    this.allTemplateConditions = this.ruleConditionActionTemplatesDTO.conditions;

                    if (this.allTemplateActions == null || this.allTemplateActions.length == 0 ||
                        this.allTemplateConditions == null || this.allTemplateConditions.length == 0) {
                        this.alertService.showErrorMessage(null, false, 'message.rule.no.templates')
                        this.dialogRef.close();
                    }
                    else {
                        this.alertService.showSuccessMessage(data, 'message.rule.templates');
                    }
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
    }


    /**
     * helping method to iterate over array of primitive in ngFor (ngModel)
     * otherwise the input field loses focus on every character which has been typed by user
     * @param index of the selected item
     * @param item
     */
    trackByFn(index, item) {
        return index;
    }

    /**
     * funtion to make a deep copy by parsing to JSON and back
     * @param value
     */
    makeDeepCopy(value) {
        return JSON.parse(JSON.stringify(value));
    }

    /**
     * Function to return the ASCII char from the given number
     * @param ASCII value
     */
    getBigLetterFromInt(value: number) {
        return String.fromCharCode(value);
    }

    /**
     * Method to prepare the selected template condition
     * @param value
     */
    addCondition(value: TemplateCondition, selector: MatSelect) {
        this.selectedConditions.push(this.makeDeepCopy(value));
        const indexLast = this.selectedConditions.length - 1;
        this.selectedConditions[indexLast].paramArrays.forEach(paramArray => {
            paramArray.values = new Array(); // sets new array, otherwise it will be undefined
            paramArray.values.push(''); // necessary to show a input field in the html
        });
        selector.writeValue(null);
    }


    /**
     * Method to prepare the selected template action
     *
     * @param value selected TemplateAction
     */
    addAction(value: TemplateAction, selector: MatSelect) {
        this.selectedActions.push(this.makeDeepCopy(value));
        const indexLast = this.selectedActions.length - 1;
        this.selectedActions[indexLast].paramArrays.forEach(paramArray => {
            paramArray.values = new Array(); // sets new array, otherwise it will be undefined
            paramArray.values.push(''); // necessary to show a input field in the html
        });
        selector.writeValue(null);
    }


    /**
     * add a new empty element to a condition param array if the last one is not empty
     * @param arrayIndex of the chosen param array
     */
    addValueConditionParamArray(condtionIndex: number, arrayIndex: number) {
        if (this.selectedConditions[condtionIndex].paramArrays[arrayIndex].
            values[this.selectedConditions[condtionIndex].paramArrays[arrayIndex].values.length - 1] != '')
            this.selectedConditions[condtionIndex].paramArrays[arrayIndex].values.push('');
    }


    /**
     * add a new empty element to a action param array if the last one is not empty
     * @param arrayIndex of the chosen param array
     */
    addValueActionParamArray(actionIndex: number, arrayIndex: number) {
        if (this.selectedActions[actionIndex].paramArrays[arrayIndex].
            values[this.selectedActions[actionIndex].paramArrays[arrayIndex].values.length - 1] != '')
            this.selectedActions[actionIndex].paramArrays[arrayIndex].values.push('');
    }


    /**
     * Method to check if given strung is empty (whitespace also count as emptry) or undefined
     * @param _string which should be checked
     */
    private isStringEmptyOrUndefined(_string: string) {
        if (!_string || _string.replace(/\s/g, '').length == 0)
            return true;
        return false;
    }


    /**
     * Method to remove one element of an array, if it is the first element it will only set to an empty string
     * @param index
     * @param array
     */
    private removeArrayElement(index: number, array: any[]) {
        if (array.length == 1) {
            array[0] = '';
        } else {
            array.splice(index, 1);
        }
    }

    /**
     * removes an chosen action
     * @param index of the action which should be removed
     */
    private removeAction(index: number) {
        this.selectedActions.splice(index, 1);
    }

    /**
     * removes an chosen condition
     * @param index of the condition which should be removed
     */
    private removeCondition(index: number) {
        this.selectedConditions.splice(index, 1);
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
     * Removes not alloweded character from the given input field. This funtion
     * does NOT check if the given conditionExpression is valid
     * @param conditionExpression
     */
    public removeNotAllowedChars(conditionExpression: any) {
        if (this.selectedConditions.length != 0) {
            /*
            The regexString will lock like this [^A-Z&!|()] except that the
            Z will be replaced with the highest possible Letter. That means that
            the user only can give as much different variables as there are conditions chosen.
            Example: 3 chosen condtions => only A,B,C are possible
            */
            const regexString = '[^A-' + String.fromCharCode(this.selectedConditions.length + 64) + '&!|()]';
            const regexAllowed = new RegExp(regexString, 'g');
            conditionExpression.value = conditionExpression.value.replace(regexAllowed, '');
        }
        else {
            conditionExpression.value = null;
            this.openSnackbar(this.translate.instant('rule.condition.notChosen'), '');
        }
    }


    /**
     * Function checks if the given conditionExpression is valid.
     * @param conditionExpression
     */
    public lostFocusConditionExpression(conditionExpression: any) {
        if (this.isStringEmptyOrUndefined(conditionExpression.value))
            return;

        const conditionExpressionDTO = new ConditionExpressionDTO();
        conditionExpressionDTO.expression = conditionExpression.value;
        conditionExpressionDTO.variableCount = this.selectedConditions.length;

        this.httpService.post(conditionExpressionDTO, environment.apiRuleCheckCondExpr).subscribe(
            data => {
                const newConditionExpressionDTO = data;
                conditionExpression.value = newConditionExpressionDTO.expression;
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.openSnackbar(this.translate.instant('rule.conditionExpression.not.valid'), '');
            });
    }

    /**
     * Function checks if the given rule name is already used
     * @param ruleName
     */
    public lostFocusRuleName(ruleName: any) {
        if (this.isStringEmptyOrUndefined(ruleName.value))
            return;

        const name = new String(ruleName.value);
        const dto = new CheckRuleNameDTO();
        dto.name = ruleName.value;
        dto.ruleId = this.ruleId;
        this.httpService.post(dto, environment.apiRuleCheckNameUsed).subscribe(
            data => {
                if (data == true) {
                    ruleName.value = null;
                    this.openSnackbar(this.translate.instant('rule.name.already.used'), '');
                }
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.openSnackbar(error.error.errorMessage, '');
            });
    }


    /**
     * Saves the new/updated rule if all needed parameter are given by the user
     */
    public saveRule() {

        // check if name is given
        if (this.isStringEmptyOrUndefined(this.ruleName)) {
            this.openSnackbar(this.translate.instant('rule.name.notgiven'), '');
            return;
        }

        // check if description is given
        if (this.isStringEmptyOrUndefined(this.ruleDescription)) {
            this.openSnackbar(this.translate.instant('rule.description.notgiven'), '');
            return;
        }

        // checks if a type limit has been given
        if (this.typeLimit == undefined) {
            this.openSnackbar(this.translate.instant('rule.limit.notgiven'), '');
            return;
        }

        // checks if condition has been chosen
        if (this.selectedConditions.length == 0) {
            this.openSnackbar(this.translate.instant('rule.condition.notChosen'), '');
            return;
        }

        // checks if a condition expression has been given
        if (this.isStringEmptyOrUndefined(this.conditionExpression)) {
            this.openSnackbar(this.translate.instant('rule.conditionExpression.notgiven'), '');
            return;
        }

        for (const condition of this.selectedConditions) {
            // checks if all params are given by user
            for (const param of condition.params) {
                if (!param.value) {
                    this.openSnackbar(this.translate.instant('rule.condition.param.missing'), '');
                    return;
                }
            }

            // checks if all param arrays are given by user
            for (const paramArray of condition.paramArrays) {
                // remove elements which are only whitespaces
                for (let i = 0; i < paramArray.values.length; i++) {
                    if (this.isStringEmptyOrUndefined(paramArray.values[i])) {
                        paramArray.values.splice(i, 1);
                        i--;
                    }
                }

                // checks if first element is not undefined or null
                if (!paramArray.values[0]) {
                    this.openSnackbar(this.translate.instant('rule.condition.param.missing'), '');
                    return;
                }
            }
        }

        // checks if action has ben chosen
        if (this.selectedActions.length == 0) {
            this.openSnackbar(this.translate.instant('rule.action.notChosen'), '');
            return;
        }

        for (const action of this.selectedActions) {
            // checks if all params are given by user
            for (const param of action.params) {
                if (!param.value) {
                    this.openSnackbar(this.translate.instant('rule.action.param.missing'), '');
                    return;
                }
            }

            // checks if all param arrays are given by user
            for (const paramArray of action.paramArrays) {
                // remove elements which are only whitespaces
                for (let i = 0; i < paramArray.values.length; i++) {
                    if (this.isStringEmptyOrUndefined(paramArray.values[i])) {
                        paramArray.values.splice(i, 1);
                        i--;
                    }
                }

                // checks if first element is not undefined or null
                if (!paramArray.values[0]) {
                    this.openSnackbar(this.translate.instant('rule.action.param.missing'), '');
                    return;
                }
            }
        }

        // saves all data from gui into the rule object
        this.ruleTemplate.name = this.ruleName;
        this.ruleTemplate.description = this.ruleDescription;
        this.ruleTemplate.limit = this.typeLimit;
        this.ruleTemplate.conditionExpression = this.conditionExpression;
        this.ruleTemplate.templateConditions = new Array();
        this.selectedConditions.forEach(condition => {
            this.ruleTemplate.templateConditions.push(condition);
        });
        this.ruleTemplate.templateActions = new Array();
        this.selectedActions.forEach(action => {
            this.ruleTemplate.templateActions.push(action);
        });


        // tries to save the rule
        const apiUrl = environment.apiRuleTemplateFactType.replace('{ruleFactType}', this.ruleFactType.toString());
        this.httpService.post(this.ruleTemplate, apiUrl).subscribe(
            data => {
                this.ruleTemplate = data;
                this.dialogRef.close(true);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.dialogRef.close(error);
            });
    }
}
