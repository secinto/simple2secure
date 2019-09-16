import {
	Component,
	ElementRef,
	Inject,
	ViewChild,
} from '@angular/core';
import {AlertService, HttpService, DataService} from '../_services/index';
import {
    MatDialogRef,
    MAT_DIALOG_DATA,
    MatTabGroup, MatDialog, MatSnackBar, MatTab,
} from '@angular/material';
import {RuleWithSourcecode} from '../_models/ruleWithSourcecode';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy, Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {ContextDTO, TemplateRule} from '../_models';
import 'brace';
import 'ace-builds/src-noconflict/mode-groovy';
import {AceEditorComponent} from 'ng2-ace-editor';
import {TemplateCondition} from '../_models/templateCondition';
import {TemplateAction} from '../_models/templateAction';
import {DataType} from '../_models/dataType';


@Component({
	moduleId: module.id,
	styleUrls: ['rule.component.css'],
	templateUrl: 'ruleAdd.component.html',
	selector: 'addRule'
})
export class RuleAddComponent {
    ruleName: string;
    ruleDescription: string;
	ruleExpert: RuleWithSourcecode;
	ruleTemplate: TemplateRule;
	context: ContextDTO;
	allTemplateConditions: TemplateCondition[];
	allTemplateActions: TemplateAction[];
	selectedCondition: TemplateCondition;
	selectedAction: TemplateAction;
	dataType = DataType;
    disableEditorTab: boolean;
    disableTemplateTab: boolean;
    selectedTab: number;
    dialogTitle: string;

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
		this.context = JSON.parse(localStorage.getItem('context'));

        this.selectedTab = 0;
        if(data.rule)
        {
            this.dialogTitle = this.translate.instant('rule.edit');

            this.ruleName = data.rule.name;
            this.ruleDescription = data.rule.description;

            if(data.rule.sourcecode)
            {
                this.selectedTab = 1;
                this.disableTemplateTab = true; // only show expert mode
                this.ruleExpert = data.rule;
            }

            if(data.rule.templateCondition)
            {
                this.selectedTab = 0;
                this.disableEditorTab = true; // only show template mode
                this.ruleTemplate = data.rule;
                this.ruleExpert = new RuleWithSourcecode(); // will be needed for the ace-editor ngModel, but not used in this case
                this.selectedCondition = this.ruleTemplate.templateCondition;
                console.log(this.selectedCondition.paramArrays[0].values);
                console.log(this.ruleTemplate.templateCondition.paramArrays[0].values);
                this.selectedAction = this.ruleTemplate.templateAction;
            }
        }
        else
        {
            this.dialogTitle = this.translate.instant('button.addRule');

            this.ruleExpert = new RuleWithSourcecode();

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

            this.ruleTemplate = new TemplateRule();

            this.disableTemplateTab = false;
            this.disableEditorTab = false;
        }

        this.getTemplates();
	}

    ngAfterViewInit() {

    }


    // helping method to iterate over array of primitive in ngFor (ngModel)
    // otherwise the input field loses focus on every character which has been typed by user
    trackByFn(index, item) {
        return index;
    }

	showConditionParams(value: TemplateCondition){
	    this.selectedCondition = value;
	    this.selectedCondition.paramArrays.forEach(paramArray => {
	       paramArray.values = new Array();
	       paramArray.values.push("");
        });
	}

	showActionParams(value: TemplateAction){
	    this.selectedAction = value;
	    this.selectedAction.paramArrays.forEach(paramArray => {
	    	paramArray.values = new Array();
	    	paramArray.values.push("");
		});
	}

	addValueConditionParamArray(arrayIndex: number){

        if (this.selectedCondition.paramArrays[arrayIndex].values[this.selectedCondition.paramArrays[arrayIndex].values.length - 1] != "")
        {
            this.selectedCondition.paramArrays[arrayIndex].values.push("");
        }
	}

    addValueActionParamArray(arrayIndex: number){
		if (this.selectedAction.paramArrays[arrayIndex].values[this.selectedAction.paramArrays[arrayIndex].values.length - 1] != "")
		{
			this.selectedAction.paramArrays[arrayIndex].values.push("");
		}
    }

	private getTemplates()
	{
		this.httpService.get(environment.apiEndpoint + 'rule/template_actions/')
			.subscribe(
				data => {
					this.allTemplateActions = data;

					if(this.allTemplateActions == null)
					    return;

					this.alertService.success(this.translate.instant('rule.loadTemplateConditionsSucces'));
				},
				error => {

					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
                    else {
                        this.alertService.error(error.error.errorMessage);
                    }
				});

		this.httpService.get(environment.apiEndpoint + 'rule/template_conditions/')
			.subscribe(
				data => {
					this.allTemplateConditions = data;

					if(this.allTemplateConditions == null)
						return;

					this.alertService.success(this.translate.instant('rule.loadTemplateActionsSucces'));
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

	private isStringEmptyOrUndefined(_string: string)
    {
        if(!_string || _string.replace(/\s/g, '').length == 0 )
        {
            return true;
        }
        return false;
    }

    private removeArrayElement(index : number, array : any[])
    {
        if(array.length == 1) {
            array[0] = "";
        }else {
            array.splice(index, 1);
        }
    }


	private openSnackbar(message: string, action: string)
    {
        this.snackBar.open(message, action, {
            duration: 2000,
        });
    }

	private saveRule() {

	    if(this.isStringEmptyOrUndefined(this.ruleName)){
	        this.openSnackbar(this.translate.instant('rule.errorValueEmptyOrInvalide') + this.translate.instant('table.name'), "");
	        return;
        }

        if(this.isStringEmptyOrUndefined(this.ruleDescription)){
            this.openSnackbar(this.translate.instant('rule.errorValueEmptyOrInvalide') + this.translate.instant('table.description'), "");
            return;
        }

        switch (this.tabGroup.selectedIndex) {
            case 0: { // template mode

                if(!this.selectedCondition)
                {
                    this.openSnackbar(this.translate.instant('rule.noConditionChosen'), "");
                    return;
                }

                for(const param of this.selectedCondition.params)
                {
                    if(!param.value)
                    {
                        this.openSnackbar(this.translate.instant('rule.missingParamInCondition') + param.name, "");
                        return;
                    }
                }

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

                    if(!paramArray.values[0])
                    {
                        this.openSnackbar(this.translate.instant('rule.missingParamInCondition') + paramArray.name, "");
                        return;
                    }

                }

                if(!this.selectedAction)
                {
                    this.openSnackbar(this.translate.instant('rule.noActionChosen'), "");
                    return;
                }

                for(const param of this.selectedAction.params)
                {
                    if(!param.value)
                    {
                        this.openSnackbar(this.translate.instant('rule.missingParamInAction') + param.name, "");
                        return;
                    }
                }

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

                    if(!paramArray.values[0])
                    {
                        this.openSnackbar(this.translate.instant('rule.missingParamInAction') + paramArray.name, "");
                        return;
                    }
                }



                this.ruleTemplate.name = this.ruleName;
                this.ruleTemplate.description = this.ruleDescription;

                if (!this.ruleTemplate.contextID) {
                    this.ruleTemplate.contextID = this.context.context.id;
                }

                this.ruleTemplate.templateCondition = this.selectedCondition;
                this.ruleTemplate.templateAction = this.selectedAction;

                this.httpService.post(this.ruleTemplate, environment.apiEndpoint + 'rule/templaterule/').subscribe(
                    data => {
                        this.dialogRef.close(true);
                    },
                    error => {
                        this.dialogRef.close(error);
                    });
                break;
            }

            case 1: { // expert mode

                if(this.isStringEmptyOrUndefined(this.ruleExpert.sourcecode)) {
                    this.openSnackbar(this.translate.instant('rule.noCodeGiven'), "");
                    return;
                }

                this.ruleExpert.name = this.ruleName;
                this.ruleExpert.description = this.ruleDescription;

                if (!this.ruleExpert.contextID) {
                    this.ruleExpert.contextID = this.context.context.id;
                }

                this.httpService.post(this.ruleExpert, environment.apiEndpoint + 'rule/rulewithsource/').subscribe(
                    data => {
                        this.dialogRef.close(true);
                    },
                    error => {
                        this.dialogRef.close(error);
                    });

                break;
            }
            default: {
                //statements;
                break;
            }
        }
	}
}
