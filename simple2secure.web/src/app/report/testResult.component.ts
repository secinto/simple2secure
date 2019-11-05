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
import {AlertService, DataService, HttpService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {ContextDTO, TestResultDTO} from '../_models';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TestResultDetailsComponent} from './testResultDetails.component';
import {TestRunDTO} from '../_models/DTO/testRunDTO';

@Component({
	moduleId: module.id,
	templateUrl: 'testResult.component.html'
})

export class TestResultComponent {

	currentUser: any;
	testResults: TestRunDTO[];
	selectedResult: TestRunDTO;
	loading = false;
	context: ContextDTO;
	displayedColumns = ['podId', 'hostname', 'testname', 'timestamp', 'action'];
	dataSource = new MatTableDataSource();
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private router: Router,
		private dataService: DataService,
		private route: ActivatedRoute,
		private dialog: MatDialog,
		private translate: TranslateService
	)
	{}

	ngOnInit() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadTestResults(0, 10);
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
		this.dataSource.filter = filterValue;
	}

	public handlePage(e: any) {
		this.currentPage = e.pageIndex;
		this.pageSize = e.pageSize;
		this.loadTestResults(e.pageIndex, e.pageSize);
	}

	loadTestResults(page: number, size: number) {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'test/testresult/' + this.context.context.id
			+ '/' + page + '/' + size)
			.subscribe(
				data => {
					this.testResults = data.tests;
					this.dataSource.data = this.testResults;
					this.totalSize = data.totalSize;
					if (data.tests.length > 0) {
						this.alertService.success(this.translate.instant('message.data'));
					}
					else {
						this.alertService.error(this.translate.instant('message.data.notProvided'));
					}
					this.loading = false;
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

	openDialogShowTestResult(result: TestResultDTO): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			result: result.testResult,
		};

		this.dialog.open(TestResultDetailsComponent, dialogConfig);

	}

	public onMenuTriggerClick(testResult: TestRunDTO) {
		this.selectedResult = testResult;
	}

	public openDialogDeleteTestResult() {
		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.test.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteTestResult();
			}
		});
	}

	public deleteTestResult() {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'test/testresult/delete/' + this.selectedResult.testResult.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.test.delete'));
				this.loading = false;
				this.loadTestResults(this.currentPage, this.pageSize);
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
