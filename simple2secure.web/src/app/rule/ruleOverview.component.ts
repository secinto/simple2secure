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

import {Component, Inject, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {FrontendRule} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	styleUrls: ['rule.component.css'],
	templateUrl: 'ruleOverview.component.html',
	selector: 'ruleOverview'
})
export class RuleOverviewComponent {

	rules: FrontendRule[];
	loading = false;
	selectedRule: FrontendRule;
	toolId: string;
	currentUser: any;
	deleted = false;

	displayedColumns = ['name', 'description', 'priority', 'action'];
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
		private translate: TranslateService,
		private dialogRef: MatDialogRef<RuleOverviewComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.rules = data.rules;
		this.dataSource.data = this.rules;
	}

	ngOnInit() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.toolId = this.route.snapshot.paramMap.get('id');
		//this.loadRules();
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

	private loadRules() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'rule/' + this.toolId + '/' + this.currentUser.userID)
			.subscribe(
				data => {
					this.rules = data;
					this.dataSource.data = this.rules;
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

	public onMenuTriggerClick(rule: FrontendRule) {
		this.selectedRule = rule;
	}

	public onEditClick() {
		this.editRule(this.selectedRule);
	}

	public onDeleteClick() {
		this.openDialog(this.selectedRule);
	}

	public editRule(rule: FrontendRule) {
		this.dataService.set(rule);
		this.router.navigate(['../edit'], {relativeTo: this.route, queryParams: {action: 'edit'}});
	}

	public openDialog(config: FrontendRule) {
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

	public deleteRule(rule: FrontendRule) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'email/' + rule.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.rule.delete'));
				this.deleted = true;
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
