import {Component, ElementRef, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Rule} from '../_models/rule';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy, Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {ContextDTO} from '../_models';
import 'brace';
import 'ace-builds/src-noconflict/mode-groovy';
import {AceEditorComponent} from 'ng2-ace-editor';
import {MatButtonToggleChange} from '@angular/material/typings/button-toggle';


@Component({
	moduleId: module.id,
	styleUrls: ['rule.component.css'],
	templateUrl: 'ruleAdd.component.html',
	selector: 'addRule'
})
export class RuleAddComponent {
	rule: Rule;
	loading = false;
	isNewRuleAdded = false;
	context: ContextDTO;
	toggle = 'template';
	expert = 'expert';
	template = 'template';
	jsonString: String;

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

    getTemplates()
	{
		this.httpService.get(environment.apiEndpoint + 'rule/rule_templates')
			.subscribe(
				data => {
					// TODO: handle the response
					console.log(this.jsonString);
					this.alertService.success(this.translate.instant("Sever connection for loading templates worked")); // TODO: change hardcodedtext
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

	saveRule() {

		if (!this.rule.contextID) {
			this.rule.contextID = this.context.context.id;
		}

		this.httpService.post(this.rule, environment.apiEndpoint + 'rule').subscribe(
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
