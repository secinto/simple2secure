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
import {ActivatedRoute, Router} from '@angular/router';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog, PageEvent} from '@angular/material';
import {TestObjWeb} from '../_models/testObjWeb';
import {AlertService, HttpService, DataService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TestDetailsComponent} from './testDetails.component';
import {HttpParams} from "@angular/common/http";

@Component({
	moduleId: module.id,
	styleUrls: ['toolTestList.css'],
	templateUrl: 'orbiterToolTestList.component.html'
})

export class OrbiterToolTestListComponent {

	selectedTest: TestObjWeb = new TestObjWeb();
	podId: string;
	isTestChanged: boolean;
	tests: TestObjWeb[];
	displayedColumns = ['testId', 'version', 'status', 'action'];
	loading = false;
	url: string;
	id: string;
	public pageEvent: PageEvent;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService,
		private router: Router,
		private route: ActivatedRoute
	) {}

	ngOnInit() {
		this.isTestChanged = false;
		this.id = this.route.snapshot.paramMap.get('id');
		this.loadTests(this.id, 0, 10);
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
		this.dataSource.filter = filterValue;
	}

	public onMenuTriggerClick(test: TestObjWeb) {
		this.selectedTest = test;

	}

	public handlePage(e?: PageEvent) {
		this.currentPage = e.pageIndex;
		this.pageSize = e.pageSize;
		this.loadTests(this.id, e.pageIndex, e.pageSize);
		return e;
	}

	public loadTests(podId: string, page: number, size: number){
		this.loading = true;
		const params = new HttpParams()
			.set('usePagination', String(true));
		this.httpService.getWithParams(environment.apiEndpoint + 'test/' + podId + '/' + page + '/' + size, params)
			.subscribe(
				data => {
					this.tests = data.tests;
					this.dataSource.data = this.tests;
					this.totalSize = data.totalSize;
					if (!this.isTestChanged){
						if (data.tests.length > 0) {
							this.alertService.success(this.translate.instant('message.data'));
						}
						else {
							this.alertService.error(this.translate.instant('message.data.notProvided'));
						}
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

	openDialogShowTest(type: string): void {

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		dialogConfig.data = {
			tests: this.selectedTest,
			type: type,
			podId: this.id
		};

		const dialogRef = this.dialog.open(TestDetailsComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.isTestChanged = true;
				this.loadTests(this.id, this.currentPage, this.pageSize);
			}
		});

	}

	public runTest(){

		this.loading = true;
		let flag = this.selectedTest.test_content.test_definition.step.command.parameter.value;
		let regexp = new RegExp('^\{.*\}');
		let boolResult = regexp.test(flag);
		if(flag){
			let brk = '';
		}else {
			this.url = environment.apiEndpoint + 'test/scheduleTest';
			this.httpService.post(this.selectedTest, this.url).subscribe(
				data => {
					this.alertService.success(this.translate.instant('message.test.schedule'));
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
				});
			this.loading = false;		
		}
	}

	public openDeleteDialog() {
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
				this.deleteTest(this.selectedTest);
			}
		});
	}

	public deleteTest(selectedTest: TestObjWeb) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'test/delete/' + selectedTest.testId).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.test.delete'));
				this.loading = false;
				this.isTestChanged = true;
				this.loadTests(this.id, this.currentPage, this.pageSize);
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
