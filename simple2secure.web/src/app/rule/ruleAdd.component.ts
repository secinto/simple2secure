import {
	Component, ComponentFactory,
	ComponentFactoryResolver,
	ComponentRef,
	ElementRef,
	Inject,
	ViewChild,
	ViewContainerRef
} from '@angular/core';
import {AlertService, HttpService, DataService} from '../_services/index';
import {
    MatTableDataSource,
    MatSort,
    MatPaginator,
    MatDialog,
    MatDialogConfig,
    MatDialogRef,
    MAT_DIALOG_DATA,
    MatTabChangeEvent, MatTabGroup
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
import {container} from '@angular/core/src/render3';
import {TemplateAction} from '../_models/templateAction';
import {RuleParam} from '../_models/ruleParam';
import {RuleParamArray} from '../_models/ruleParamArray';
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
	loading = false;
	context: ContextDTO;
	template = 'template';
	allTemplateConditions: TemplateCondition[];
	allTemplateActions: TemplateAction[];
	currentConditionParams:  RuleParam<any>[];
	currentConditionParamArrays: RuleParamArray<any>[];
	currentActionParams:  RuleParam<any>[];
	currentActionParamArrays: RuleParamArray<any>[];
	selectedCondition: TemplateCondition;
	selectedAction: TemplateAction;

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
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.context = JSON.parse(localStorage.getItem('context'));
		this.ruleExpert = data.rule;
		this.ruleTemplate = new TemplateRule();

		/*
		if (data.rule.id) {
			this.isNewRuleAdded = false;
		}
		else {
			this.isNewRuleAdded = true;
		}*/

		this.getTemplates();
	}

    ngAfterViewInit() {

		// set start code:
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


	showConditionParams(value: TemplateCondition){
	    this.selectedCondition = value;
		this.currentConditionParams = value.params;
		this.currentConditionParamArrays = value.paramArrays;

		this.currentConditionParamArrays.forEach(paramArray => {
            paramArray.values = new Array()
            paramArray.values.push("");
        });
	}

	showActionParams(value: TemplateAction){
	    this.selectedAction = value;
		this.currentActionParams = value.params;
		this.currentActionParamArrays = value.paramArrays;

        this.currentActionParamArrays.forEach(paramArray => {
            paramArray.values = new Array()
            paramArray.values.push("");
        });
	}

	addValueConditionParamArray(arrayIndex: number){
	    if (this.currentConditionParamArrays[arrayIndex].values[this.currentConditionParamArrays[arrayIndex].values.length - 1] != "")
        {
            this.currentConditionParamArrays[arrayIndex].values.push("");
        }
	}

    addValueActionParamArray(arrayIndex: number){
        if (this.currentActionParamArrays[arrayIndex].values[this.currentActionParamArrays[arrayIndex].values.length - 1] != "")
        {
            this.currentConditionParamArrays[arrayIndex].values.push("");
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

					this.alertService.success(this.translate.instant("Sever connection for loading action templates worked")); // TODO: change hardcodedtext
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

					this.alertService.success(this.translate.instant("Sever connection for loading condition templates worked")); // TODO: change hardcodedtext
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


	private saveRule() {

        console.log("Name: " + this.ruleName);
        console.log("Description: " + this.ruleDescription);


        switch (this.tabGroup.selectedIndex) {
            case 0: { // template mode
                console.log("template mode");

                if(!this.ruleTemplate.name){
                    this.ruleTemplate.name = this.ruleName;
                }


                if(!this.ruleTemplate.description){
                    this.ruleTemplate.description = this.ruleDescription;
                }

                if (!this.ruleTemplate.contextID) {
                    this.ruleTemplate.contextID = this.context.context.id;
                }

                if(!this.ruleTemplate.templateCondition){
                    console.log(this.selectedCondition);
                    this.selectedCondition.paramArrays = this.currentConditionParamArrays;
                    this.selectedCondition.params = this.currentConditionParams;
                    this.ruleTemplate.templateCondition = this.selectedCondition;

                }

                if(!this.ruleTemplate.templateAction){
                    this.selectedAction.paramArrays = this.currentActionParamArrays;
                    this.selectedAction.params = this.currentActionParams;
                    this.ruleTemplate.templateAction = this.selectedAction;
                }


                console.log(this.ruleTemplate);

                this.httpService.post(this.ruleTemplate, environment.apiEndpoint + 'rule/templaterule/').subscribe(
                    data => {
                        console.log("here2");
                        this.dialogRef.close(true);
                    },
                    error => {
                        console.log("here1");
                        this.dialogRef.close(error);
                    });

                break;
            }
            case 1: { // expert mode
                console.log("expert mode");

                if(!this.ruleExpert.name){
                    this.ruleExpert.name = this.ruleName;
                }

                if(!this.ruleExpert.description){
                    this.ruleExpert.description = this.ruleDescription;
                }

                if (!this.ruleExpert.contextID) {
                    this.ruleExpert.contextID = this.context.context.id;
                }

                console.log(this.ruleExpert);

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


        //console.log("Name: " + this.ruleTemplate);


		/*
		if (!this.rule.contextID) {
			this.rule.contextID = this.context.context.id;
		}
		this.httpService.post(this.rule, environment.apiEndpoint + 'rule/templateRule/').subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
			});
		this.loading = false;
		*
		 */
	}
}
