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

import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services';
import {RuleWithSourcecode} from '../_models';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {HttpErrorResponse} from '@angular/common/http';
import {ContextDTO, TemplateRule} from '../_models';
import {RuleAddComponent} from './ruleAdd.component';

@Component({
	moduleId: module.id,
	templateUrl: 'ruleOverview.component.html',
	selector: 'ruleOverview'
})

export class RuleOverviewComponent {

	// rules (with source) objects from the database
	expertRules: RuleWithSourcecode[];
	// rules which will be built from the predefined action/conditons from the database
	templateRules: TemplateRule[];
	// rule which has been selected in the table, can be TemplateRule or RuleWithSourcecode
	selectedRule;
	context: ContextDTO;
	loading = false;

	// columns for the table where rules will be displayed
	displayedColumns = ['name', 'description', 'action']
	// source for the table data, (capsuled all rules)
	dataSource = new MatTableDataSource();


	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService)
	{
	}

	ngOnInit() {

		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadRules();
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
		this.dataSource.filter = filterValue;
	}

	/**
	 * Method to fetch all rules from the database and display them in the table
	 */
	private loadRules() {
		this.loading = true;

		// fetching the rules with source code
		this.httpService.get(environment.apiEndpoint + 'rule/rulewithsource/' + this.context.context.id)
			.subscribe(
				data => {
					this.expertRules = data;
					this.dataSource.data = this.expertRules;
					this.loading = false;
					this.alertService.success(this.translate.instant('message.rule'));
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.loading = false;
				});

		// fetching the template rules
		this.httpService.get(environment.apiEndpoint + 'rule/templaterule/' + this.context.context.id)
			.subscribe(
				data => {
					this.templateRules = data;
					this.dataSource.data = this.dataSource.data.concat(this.templateRules);
					this.loading = false;
					this.alertService.success(this.translate.instant('message.rule'));
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.loading = false;
				});
	}

	public onMenuTriggerClick(rule) {
		this.selectedRule = rule;
	}

	public onOpenDialogAddRule(){
		this.openDialogAddRule();
	}

	public onEditClick() {
		this.editRule(this.selectedRule);
	}

	public onDeleteClick() {
		this.onDeleteDialog();
	}

	private openDialogAddRule(){
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		dialogConfig.data = {};

		const dialogRef = this.dialog.open(RuleAddComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.rule.dialog'));
				this.loadRules();
			}
			else {
                 if (result instanceof HttpErrorResponse) {
                    if (result.status == 0) {
                    	this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else {
                    	this.alertService.error(result.error.errorMessage);
                    }
                 }
			}
		});
	}


	private editRule(rule){

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		// gives the rule which should be edited
		dialogConfig.data = {
			rule: rule,
		};

		const dialogRef = this.dialog.open(RuleAddComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.rule.dialog'));
				this.loadRules();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
				}
			}
		});
	}


	private onDeleteDialog() {
		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.rule.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteRule(this.selectedRule);
			}
		});
	}

	private deleteRule(rule) {

		// must be an expert Rule if not undefined
		if(rule.sourcecode)
		{
			this.httpService.delete(environment.apiEndpoint + 'rule/rulewithsource/' + rule.id).subscribe(
				data => {
					this.alertService.success(this.translate.instant('message.rule.delete'));
					this.loadRules();
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.loading = false;
				});

		}

		// must be a template rule if not undefined
		if(rule.templateCondition)
		{
			this.httpService.delete(environment.apiEndpoint + 'rule/templaterule/' + rule.id).subscribe(
				data => {
					this.alertService.success(this.translate.instant('message.rule.delete'));
					this.loadRules();
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.loading = false;
				});
		}
	}
}
