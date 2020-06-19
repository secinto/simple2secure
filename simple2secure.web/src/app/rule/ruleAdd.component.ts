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

import { Component, Inject, ViewChild, } from '@angular/core';
import {AlertService, HttpService, DataService} from '../_services';
import {MatDialogRef, MAT_DIALOG_DATA, MatTabGroup, MatSnackBar, } from '@angular/material';
import {RuleWithSourcecode, RuleDTO, RuleMappingDTO, RuleConditionActionTemplatesDTO} from '../_models';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy, Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {TemplateRule} from '../_models';
import {AceEditorComponent} from 'ng2-ace-editor';
import {TemplateCondition} from '../_models/templateCondition';
import {TemplateAction} from '../_models/templateAction';
import {DataType} from '../_models/dataType';

import 'brace';
import 'ace-builds/src-noconflict/mode-groovy';

@Component({
	moduleId: module.id,
	styleUrls: ['rule.component.css'],
	templateUrl: 'ruleAdd.component.html',
	selector: 'addRule'
})

export class RuleAddComponent {

    ruleDTO: RuleDTO;
    ruleName: string;
    ruleDescription: string;
    // rule where the whole sourcecode has been given by user
	ruleExpert: RuleWithSourcecode;
	// rule which will be built with predefined actions/conditions
	ruleTemplate: TemplateRule;
	// predefined template conditons which will be fetched from the database
	allTemplateConditions: TemplateCondition[];
    // predefined template actions which will be fetched from the database
	allTemplateActions: TemplateAction[];
	// selected (by the user) condition which will be display
	selectedCondition: TemplateCondition;
    // selected (by the user) action which will be display
	selectedAction: TemplateAction;
	// object of the enum for the params, will be needed in the html, otherwise the enum cant be find
	dataType = DataType;
	// helping variables for the tab group
    disableEditorTab: boolean;
    disableTemplateTab: boolean;
    selectedTab: number;
    // title which will be displayed at the head of the dialog
    dialogTitle: string;
    loading = false;
    // chosen email configs for mapping rule to email
    emailConfigsChosen: boolean[];
    ruleConditionActionTemplatesDTO: RuleConditionActionTemplatesDTO;

    @ViewChild('ace_editor') editor: AceEditorComponent;
    @ViewChild('tabGroup') tabGroup: MatTabGroup;


	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private dataService: DataService,
		private url: LocationStrategy,
		private translate: TranslateService,
		private location: Location,
		private dialogRef: MatDialogRef<RuleAddComponent>,
        private snackBar: MatSnackBar,

		@Inject(MAT_DIALOG_DATA) data)
	{
        this.selectedTab = 0;
        this.ruleDTO = data.ruleDTO;


        this.emailConfigsChosen = new Array();


        // if a rule should be edited the old data of the rule will be displayed
        if(data.rule)
        {
            // set the title to "editing rule"
            this.dialogTitle = this.translate.instant('rule.edit');

            // loading old name and description
            this.ruleName = data.rule.name;
            this.ruleDescription = data.rule.description;

            // if sourcecode is defined it must be an RuleWithSourcecode object
            if(data.rule.sourcecode)
            {
                this.selectedTab = 1; // sets expert tab
                this.disableTemplateTab = true; // disables TemplateTab
                this.ruleExpert = data.rule;
            }

            // if templateCondition is defined it must be an TemplateRule object
            if(data.rule.templateCondition)
            {
                this.selectedTab = 0; // sets template tab
                this.disableEditorTab = true; // disables ExpertTab
                this.ruleTemplate = data.rule;

                // ruleExpert object will not be used in this case,
                // but will be needed for the ace-editor ngModel => otherwise there will be errors
                this.ruleExpert = new RuleWithSourcecode();

                // displays the old condition
                this.selectedCondition = this.ruleTemplate.templateCondition;
                // displays action
                this.selectedAction = this.ruleTemplate.templateAction;

                for(let i = 0; i < this.ruleDTO.emailConfigurations.length; i++)
                {
                    if( this.ruleDTO.ruleUserPairs.find( pair =>
                        pair.emailConfigurationId == this.ruleDTO.emailConfigurations[i].id &&
                        pair.ruleId == data.rule.id
                    )) {
                        this.emailConfigsChosen.push(true);
                    } else {
                        this.emailConfigsChosen.push(false);
                    }
                }
            }
        }
        else // a new rule will be added
        {
            // set the title to "editing rule"
            this.dialogTitle = this.translate.instant('button.addRule');

            this.ruleTemplate = new TemplateRule();
            this.ruleExpert = new RuleWithSourcecode();

            // setting an empty sourcecode body so the user knows how to implement the rule
            this.ruleExpert.sourcecode =
                "import org.jeasy.rules.annotation.Action;\n" +
                "import org.jeasy.rules.annotation.Condition;\n" +
                "import org.jeasy.rules.annotation.Fact;\n" +
                "import org.jeasy.rules.annotation.Rule;\n" +
                "import com.simple2secure.api.model.Email;\n" +
                "\n" +
                "@Rule(name = \"rulename here\",\n" +
                "             description = \"description\",\n" +
                "             priority = 1)\n" +
                "public class MyRule\n" +
                "{\n" +
                "\n" +
                "\t\n" +
                "\t@Condition\n" +
                "\tpublic boolean condition(@Fact(\"com.simple2secure.api.model.Email\") Email email)\n" +
                "\t{\n" +
                "\t\t\n" +
                "\t\t// implement your condition for the email checker here...\n" +
                "\t\t\n" +
                "\t\treturn true; //if action should be performed\n" +
                "\t\treturn false; // false otherwise"+
                "\t\t\n" +
                "\t}\n" +
                "\t\n" +
                "\t@Action\n" +
                "\tpublic void action(@Fact(\"com.simple2secure.api.model.Email\") Email email)\n" +
                "\t{\n" +
                "\t\t\n" +
                "\t\t// implement your action for the email checker here...\n" +
                "\t\t\n" +
                "\t}\n" +
                "}";

            // enables both tabs so the user can choose
            this.disableTemplateTab = false;
            this.disableEditorTab = false;

            for(let i = 0; i < this.ruleDTO.emailConfigurations.length; i++)
                this.emailConfigsChosen.push(false);
        }

        // fetches the template actions/conditions from the database
        this.getTemplates();
	}

    ngAfterViewInit() {
    }

    /**
     * Method to load template actions and conditions from the database
     */
    private getTemplates()
    {

        this.httpService.get(environment.apiEndpoint + 'rule/template_conditions_actions/')
            .subscribe(
                data => {
                    this.ruleConditionActionTemplatesDTO = data;
                    this.allTemplateActions = this.ruleConditionActionTemplatesDTO.actions;
                    this.allTemplateConditions = this.ruleConditionActionTemplatesDTO.conditions;

                    if(this.allTemplateActions == null)
                        return;

                    if(this.allTemplateConditions == null)
                        return;

                    this.alertService.success(this.translate.instant('message.rule.templates'));
                },
                error => {

                    if (error.status == 0) {
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else {
                        this.alertService.error(error.error.errorMessage);
                    }
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
     * Method to prepare the selected template condition
     * @param value
     */
	showConditionParams(value: TemplateCondition){
	    this.selectedCondition = value;
	    this.selectedCondition.paramArrays.forEach(paramArray => {
	       paramArray.values = new Array();
	       paramArray.values.push("");
        });
	}

    /**
     * Method to prepare the selected template action
     *
     * @param value selected TemplateAction
     */
	showActionParams(value: TemplateAction){
	    this.selectedAction = value;
	    this.selectedAction.paramArrays.forEach(paramArray => {
	    	paramArray.values = new Array(); // sets new array, otherwise it will be undefined
	    	paramArray.values.push(""); // necessary to show a input field in the html
		});
	}


    /**
     * add a new empty element to a condition param array if the last one is not empty
     * @param arrayIndex of the chosen param array
     */
	addValueConditionParamArray(arrayIndex: number){
        if (this.selectedCondition.paramArrays[arrayIndex].values[this.selectedCondition.paramArrays[arrayIndex].values.length - 1] != "")
        {
            this.selectedCondition.paramArrays[arrayIndex].values.push("");
        }
	}

    /**
     * add a new empty element to a action param array if the last one is not empty
     * @param arrayIndex of the chosen param array
     */
    addValueActionParamArray(arrayIndex: number){
		if (this.selectedAction.paramArrays[arrayIndex].values[this.selectedAction.paramArrays[arrayIndex].values.length - 1] != "")
		{
			this.selectedAction.paramArrays[arrayIndex].values.push("");
		}
    }

    /**
     * Method to check if given strung is empty (whitespace also count as emptry) or undefined
     * @param _string which should be checked
     */
	private isStringEmptyOrUndefined(_string: string)
    {
        if(!_string || _string.replace(/\s/g, '').length == 0 )
        {
            return true;
        }
        return false;
    }

    /**
     * Method to remove one element of an array, if it is the first element it will only set to an empty string
     * @param index
     * @param array
     */
    private removeArrayElement(index : number, array : any[])
    {
        if(array.length == 1) {
            array[0] = "";
        }else {
            array.splice(index, 1);
        }
    }


    /**
     * Opens a snackbar and displays message and action
     * @param message which should be displayed
     * @param action which should be performed when clicked
     */
	private openSnackbar(message: string, action: any)
    {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

    /**
     * Saves the new/updated rule if all needed parameter are given by the user
     */
	public saveRule() {

	    // check if name is given
	    if(this.isStringEmptyOrUndefined(this.ruleName)){
	        this.openSnackbar(this.translate.instant('rule.errorValueEmptyOrInvalide') + this.translate.instant('table.name'), "");
	        return;
        }

	    // check if description is given
        if(this.isStringEmptyOrUndefined(this.ruleDescription)){
            this.openSnackbar(this.translate.instant('rule.errorValueEmptyOrInvalide') + this.translate.instant('table.description'), "");
            return;
        }

        // checks which kind of rule is chosen by user
        switch (this.tabGroup.selectedIndex) {
            // template mode
            case 0: {

                // checks if condition has ben chosen
                if(!this.selectedCondition)
                {
                    this.openSnackbar(this.translate.instant('rule.noConditionChosen'), "");
                    return;
                }

                // checks if all params are given by user
                for(const param of this.selectedCondition.params)
                {
                    if(!param.value)
                    {
                        this.openSnackbar(this.translate.instant('rule.missingParamInCondition') + param.name, "");
                        return;
                    }
                }

                // checks if all param arrays are given by user
                for(const paramArray of this.selectedCondition.paramArrays)
                {

                    // remove elements which are only whitespaces
                   for(let i = 0; i < paramArray.values.length; i++)
                   {
                       if(this.isStringEmptyOrUndefined(paramArray.values[i]))
                       {
                           paramArray.values.splice(i,1);
                           i--;
                       }
                   }

                   // checks if first element is not undefined or null
                    if(!paramArray.values[0])
                    {
                        this.openSnackbar(this.translate.instant('rule.missingParamInCondition') + paramArray.name, "");
                        return;
                    }

                }

                // checks if action has ben chosen
                if(!this.selectedAction)
                {
                    this.openSnackbar(this.translate.instant('rule.noActionChosen'), "");
                    return;
                }

                // checks if all params are given by user
                for(const param of this.selectedAction.params)
                {
                    if(!param.value)
                    {
                        this.openSnackbar(this.translate.instant('rule.missingParamInAction') + param.name, "");
                        return;
                    }
                }

                // checks if all param arrays are given by user
                for(const paramArray of this.selectedAction.paramArrays)
                {
                    // remove elements which are only whitespaces
                    for(let i = 0; i < paramArray.values.length; i++)
                    {
                        if(this.isStringEmptyOrUndefined(paramArray.values[i]))
                        {
                            paramArray.values.splice(i,1);
                            i--;
                        }
                    }

                    // checks if first element is not undefined or null
                    if(!paramArray.values[0])
                    {
                        this.openSnackbar(this.translate.instant('rule.missingParamInAction') + paramArray.name, "");
                        return;
                    }
                }



                // saves all data from gui into the rule object
                this.ruleTemplate.name = this.ruleName;
                this.ruleTemplate.description = this.ruleDescription;
                this.ruleTemplate.templateCondition = this.selectedCondition;
                this.ruleTemplate.templateAction = this.selectedAction;

                // tries to save the rule
                this.httpService.post(this.ruleTemplate, environment.apiEndpoint + 'rule/templaterule').subscribe(
                    data => {
                        this.ruleTemplate = data;
                        this.saveMappingRuleUser(this.ruleTemplate.id);
                        this.dialogRef.close(true);
                    },
                    error => {
                        this.dialogRef.close(error);
                    });
                break;
            }

            // expert mode
            case 1: {

                // checks if sourcecode is given
                if(this.isStringEmptyOrUndefined(this.ruleExpert.sourcecode)) {
                    this.openSnackbar(this.translate.instant('rule.noCodeGiven'), "");
                    return;
                }

                // saves all data from gui into the rule object
                this.ruleExpert.name = this.ruleName;
                this.ruleExpert.description = this.ruleDescription;
                // tries to save the rule
                this.httpService.post(this.ruleExpert, environment.apiEndpoint + 'rule/rulewithsource').subscribe(
                    data => {
                        this.dialogRef.close(true);
                    },
                    error => {
                        this.dialogRef.close(error);
                    });

                break;
            }
            default: {
                break;
            }
        }
	}

    /**
     * sends the rule-user-mapping to the portal
     * @param ruleId
     */
	private saveMappingRuleUser(ruleId: string)
    {
        let ruleMappingDTO = new RuleMappingDTO();
        ruleMappingDTO.contextId = "";
        ruleMappingDTO.ruleId = ruleId;
        ruleMappingDTO.emailConfigurationsIds =  new Array();

        for(let index = 0; index < this.ruleDTO.emailConfigurations.length; index++) {
            if(this.emailConfigsChosen[index] == true)
                ruleMappingDTO.emailConfigurationsIds.push(this.ruleDTO.emailConfigurations[index].id);
        }

        this.httpService.post(ruleMappingDTO, environment.apiEndpoint + 'rule/mapping').subscribe(
            data => {},
            error => {
                if (error.status == 0) {
                    this.alertService.error(this.translate.instant('server.notresponding'));
                }
                else {
                    this.alertService.error(error.error.errorMessage);
                }
            });
    }
}
