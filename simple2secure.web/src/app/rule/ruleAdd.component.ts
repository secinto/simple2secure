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

import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FrontendRule} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy, Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	styleUrls: ['rule.component.css'],
	templateUrl: 'ruleAdd.component.html',
	selector: 'addRule'
})
export class RuleAddComponent {

	rule: FrontendRule;
	loading = false;
	isNewRuleAdded = false;


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
		this.rule = data.rule;
		if (data.rule.id) {
			this.isNewRuleAdded = false;
		}
		else {
			this.isNewRuleAdded = true;
		}
	}

	saveRule() {
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
