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

import { Component, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig, MatSort, MatTableDataSource } from '@angular/material';
import { environment } from '../../../environments/environment';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { RuleAddComponent } from './ruleAdd.component';
import { RuleDTO } from '../../_models/DTO/ruleDTO';
import { RuleFactType } from '../../_models/ruleFactType';
import { TemplateRule } from '../../_models/templateRule';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { RuleMappingComponent } from './ruleMapping.component';
import { PageEvent } from '@angular/material/paginator';

@Component({
	moduleId: module.id,
	templateUrl: 'ruleList.component.html',
	selector: 'ruleList'
})

export class RuleListComponent {

	ruleDTO: RuleDTO;
	// rules which will be built from the predefined action/conditions from the database
	templateRules: TemplateRule[];
	selectedRule: TemplateRule;
	loading = true;
	// columns for the table where rules will be displayed
	displayedColumns = ['name', 'description', 'action'];
	// source for the table data, (capsuled all rules)
	public pageEvent: PageEvent;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	public currentFactTpye: RuleFactType;
	public filterText: string;
	dataSource = new MatTableDataSource();
	RuleFactType = RuleFactType;

	@ViewChild(MatSort) sort: MatSort;

	constructor(
		private httpService: HttpService,
		private alertService: AlertService,
		private dialog: MatDialog,
		private translate: TranslateService) {
	}

	ngOnInit() {
		this.currentFactTpye = RuleFactType.GENERAL;
		this.loadRules();

		this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
			this.loadRules();
		});
	}

	ngAfterViewInit() {
	}

	applyFilter(filterValue: string) {
		this.filterText = filterValue;
		this.loadRules();
	}

	public handlePage(e?: PageEvent) {
		this.currentPage = e.pageIndex;
		this.pageSize = e.pageSize;

		this.loadRules();

		return e;
	}


	/**
	 * Method to fetch all rules from the database and display them in the table
	 */
	private loadRules() {
		this.loading = true;
		// fetching the template rules
		const params = new HttpParams()
			.set('ruleFactType', String(this.currentFactTpye))
			.set('page', String(this.currentPage))
			.set('size', String(this.pageSize))
			.set('filter', this.filterText);

		this.httpService.getWithParams(environment.apiRuleRulesFactTypePagination, params)
			.subscribe(
				data => {
					this.ruleDTO = data;
					this.dataSource.data = this.ruleDTO.templateRules;
					this.totalSize = this.ruleDTO.totalSize;
					this.alertService.showSuccessMessage(data, 'message.rule', false, true);
				},
				error => {
					this.alertService.showErrorMessage(error);
				});
		this.loading = false;
	}

	public onMenuTriggerClick(rule) {
		this.selectedRule = rule;
	}

	public onOpenDialogAddRule(factType) {
		this.openDialogAddOrEditRule(undefined, factType);
	}

	public onEditClick(factType: RuleFactType) {
		this.openDialogAddOrEditRule(this.selectedRule, factType);
	}

	public onMapClick(factType: RuleFactType) {
		this.openDialogMapRule(this.selectedRule, factType);
	}

	public getTypeName(type) {
		switch (type) {
			case RuleFactType.GENERAL: {
				return this.translate.instant('menu.general');
			}
			case RuleFactType.EMAIL: {
				return this.translate.instant('menu.emails');
			}
			case RuleFactType.OSQUERYREPORT: {
				return this.translate.instant('menu.queryReport');
			}
			case RuleFactType.NETWORKREPORT: {
				return this.translate.instant('menu.networkreports');
			}
			case RuleFactType.TESTSEQUENCERESULT: {
				return this.translate.instant('menu.testSequenceResults');
			}
			case RuleFactType.TESTRESULT: {
				return this.translate.instant('menu.testResults');
			}
		}
	}

	public onDeleteClick() {
		this.onDeleteDialog();
	}

	/**
	 * Method to open a dialog for creating/editing a rule
	 * @param rule for editing, if this param is undefined it will create a new one
	 */
	public openDialogAddOrEditRule(rule: TemplateRule, ruleFactType) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		if (rule) // if rule is given it will be provided the RuleAdd dialog for editing
		{
			// gives the rule which should be edited
			dialogConfig.data = {
				rule: rule,
				ruleFactType: ruleFactType
			};
		}
		else // otherwise it will create a new one
		{
			dialogConfig.data = {
				ruleFactType: ruleFactType
			};
		}

		const dialogRef = this.dialog.open(RuleAddComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.showSuccessMessage(result, 'message.rule.dialog');
				this.loadRules();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					this.alertService.showErrorMessage(result);
				}
			}
		});
	}

	public openDialogMapRule(rule: TemplateRule, ruleFactType: RuleFactType) {

		if (!rule && !ruleFactType) {
			return;
		}

		this.loading = true;

		const apiUrl = environment.apiRuleMappingFactType.replace('{ruleFactType}', ruleFactType.toString());
		const apiUrlFull = apiUrl.replace('{ruleId}', rule.id);

		this.httpService.get(apiUrlFull)
			.subscribe(
				data => {
					const ruleMappingDTO = data;
					this.loading = false;
					const dialogConfig = new MatDialogConfig();
					dialogConfig.width = '500px';

					dialogConfig.data = {
						ruleMappingDTO: ruleMappingDTO
					};

					const dialogRef = this.dialog.open(RuleMappingComponent, dialogConfig);

					dialogRef.afterClosed().subscribe(result => {
						if (result == true) {
							this.alertService.showSuccessMessage(result, 'message.rule.dialog');
							this.loadRules();
						}
						else {
							if (result instanceof HttpErrorResponse) {
								this.alertService.showErrorMessage(result);
							}
						}
					});


				},
				error => {
					this.alertService.showErrorMessage(error);
					this.loading = false;
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
		const apiUrl = environment.apiRuleTemplateId.replace('{ruleId}', rule.id);
		this.httpService.delete(apiUrl).subscribe(
			data => {
				this.alertService.showSuccessMessage(data, 'message.rule.delete');
				this.loadRules();
			},
			error => {
				this.alertService.showErrorMessage(error);
				this.loadRules();
				this.loading = false;

			});
	}

	_setDataSource(indexNumber) {
		setTimeout(() => {
			this.currentPage = 0;
			this.filterText = '';


			let typeIndex = 0;
			for (const type of RuleFactType.values()) {
				if (typeIndex == indexNumber) {
					this.currentFactTpye = RuleFactType[type];
					break;
				}
				typeIndex++;
			}

			this.loadRules();
		});
	}
}
