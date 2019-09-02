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
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {GroovyRule} from '../_models/groovyRule';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy, Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {ContextDTO} from '../_models';
import 'brace';
import 'ace-builds/src-noconflict/mode-groovy';
import {AceEditorComponent} from 'ng2-ace-editor';
import {MatButtonToggleChange} from '@angular/material/typings/button-toggle';
import {TemplateCondition} from '../_models/templateCondition';
import {container} from '@angular/core/src/render3';
import {TemplateAction} from '../_models/templateAction';


@Component({
	moduleId: module.id,
	styleUrls: ['rule.component.css'],
	templateUrl: 'ruleAdd.component.html',
	selector: 'addRule'
})
export class RuleAddComponent {
	rule: GroovyRule;
	loading = false;
	isNewRuleAdded = false;
	context: ContextDTO;
	toggle = 'template';
	expert = 'expert';
	template = 'template';
	jsonString: String;
	conditions: TemplateCondition[];
	actions: TemplateAction[];
	conditionsDisplayedColumns = ['selected', 'name', 'description'];
	actionsDisplayedColumns = ['selected', 'name', 'description'];
	conditionsDataSource = new MatTableDataSource();
	actionsDataSource = new MatTableDataSource();

    @ViewChild('ace_editor') editor: ElementRef;

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
		this.rule = data.rule;
		this.toggle = this.template;




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





	private getTemplates()
	{
		this.httpService.get(environment.apiEndpoint + 'rule/template_actions/')
			.subscribe(
				data => {
					this.actions = data;

					if(this.actions == null)
					    return;

					this.actionsDataSource.data = this.actions;

					this.actions.forEach(function (action){
						console.log("Action:");
						console.log(action.name);
						console.log(action.description_de);
						console.log(action.description_en);

						action.params.forEach(function (param) {
							console.log("---> param:");
							console.log(param.name);
							console.log(param.description_de);
							console.log(param.description_en);
							console.log(param.type);
							console.log(param.value);
						});

						action.paramArrays.forEach(function (paramArray) {
							console.log("---> paramArray:");
							console.log(paramArray.name);
							console.log(paramArray.description_de);
							console.log(paramArray.description_en);
							console.log(paramArray.type);
							console.log(paramArray.values);
						});
					});
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
					this.conditions = data;

					if(this.conditions == null)
						return;

					this.conditionsDataSource.data = this.conditions;

					this.conditions.forEach(function (condition){
						console.log("Condition:");
						console.log(condition.name);
						console.log(condition.description_de);
						console.log(condition.description_en);

						condition.params.forEach(function (param) {
							console.log("---> param:");
							console.log(param.name);
							console.log(param.description_de);
							console.log(param.description_en);
							console.log(param.type);
							console.log(param.value);
						});

						condition.paramArrays.forEach(function (paramArray) {
							console.log("---> paramArray:");
							console.log(paramArray.name);
							console.log(paramArray.description_de);
							console.log(paramArray.description_en);
							console.log(paramArray.type);
							console.log(paramArray.values);
						});
					});
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
	}

	toggleView(change: MatButtonToggleChange){
		this.toggle = change.value;
	}


}
