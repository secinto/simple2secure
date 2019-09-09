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
    MatTabGroup, MatDialog, MatSnackBar,
} from '@angular/material';
import {GroovyRule} from '../_models/groovyRule';
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
	ruleExpert: GroovyRule;
	ruleTemplate: TemplateRule;
	context: ContextDTO;
	allTemplateConditions: TemplateCondition[];
	allTemplateActions: TemplateAction[];
	selectedCondition: TemplateCondition;
	selectedAction: TemplateAction;
	dataType = DataType;

    @ViewChild('ace_editor') editor: ElementRef;
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
		//this.ruleExpert = data.rule;																					// TODO:
		this.ruleExpert = new GroovyRule();
		this.ruleTemplate = new TemplateRule();

        this.getTemplates();

	}

    ngAfterViewInit() {

		// set start code:                                                                                              // TODO:
		/*
        this.editor.getEditor().setText("" +
			"import org.jeasy.rules.annotation.Action;\n" +
			"import org.jeasy.rules.annotation.Condition;\n" +
			"import org.jeasy.rules.annotation.Fact;\n" +
			"import org.jeasy.rules.annotation.Rule;\n" +
			"import com.simple2secure.api.model.Email;\n" +
			"import com.simple2secure.portal.utils.NotificationUtils;\n" +
			"import com.simple2secure.portal.repository.EmailConfigurationRepository;\n" +
			"\n" +
			"@Rule(name = \"rulename here\",\n" +
			"             description = \"description\",\n" +
			"             priority = 10)\n" +
			"public class MyRule\n" +
			"{\n" +
			"\t\n" +
			"\t@Autowired\n" +
			"\tNotificationUtils notificationUtils;\n" +
			"\n" +
			"\t@Autowired\n" +
			"\tEmailConfigurationRepository emailConfigRepository;\n" +
			"\t\n" +
			"\t@Condition\n" +
			"\tpublic boolean condition(@Fact(\"com.simple2secure.api.model.Email\") Email email)\n" +
			"\t\t// implement your condition for the email checker here...\n" +
			"\t\t\n" +
			"\t\treturn true; //if action should be performed\n" +
			"\t\treturn false; // false otherwise"+
			"\t}\n" +
			"\t\n" +
			"\t@Action\n" +
			"\tpublic void action(@Fact(\"com.simple2secure.api.model.Email\") Email email)\n" +
			"\t{\n" +
			"\t\t\n" +
			"\t\t// implement your action for the email checker here...\n" +
			"\t\t\n" +
			"\t\tString contextID = emailConfigRepository.find(email.getConfigId()).getContextId();\n" +
			"\t\tnotificationUtils.addNewNotificationPortal(\"something is wrong with that email\", contextID);\n" +
			"\t}\n" +
			"}");
*/
    }


    // helping method to iterate over array of primitive in ngFor (ngModel); otherwise it will lose focus
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

                if(this.isStringEmptyOrUndefined(this.ruleExpert.groovyCode)) {
                    this.openSnackbar(this.translate.instant('rule.noCodeGiven'), "");
                    return;
                }

                this.ruleExpert.name = this.ruleName;
                this.ruleExpert.description = this.ruleDescription;

                if (!this.ruleExpert.contextID) {
                    this.ruleExpert.contextID = this.context.context.id;
                }

                this.httpService.post(this.ruleExpert, environment.apiEndpoint + 'rule/groovyrule/').subscribe(
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
