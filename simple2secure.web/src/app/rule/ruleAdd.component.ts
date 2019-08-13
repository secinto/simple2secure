import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Rule} from '../_models/rule';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy, Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {ContextDTO} from '../_models';


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
		console.log(this.context.context.id);
		/*
		if (data.rule.id) {
			this.isNewRuleAdded = false;
		}
		else {
			this.isNewRuleAdded = true;
		}*/
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
}
