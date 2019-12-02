import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatDialogConfig, MatDialog, MatSort, MatPaginator, PageEvent} from '@angular/material';
import { AlertService, HttpService, DataService } from '../_services';
import { TranslateService } from '@ngx-translate/core';
import { Router, ActivatedRoute } from '@angular/router';
import {environment} from '../../environments/environment';
import { SUTDetailsComponent } from './sutDetails.component';
import { SystemUnderTest } from '../_models/systemUnderTest';

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


 @Component({
     moduleId: module.id,
     templateUrl: 'orbiterSystemsUnderTestList.component.html'
 })

export class OrbiterSystemsUnderTestListComponent {

	displayedColumns = ['name', 'groupId', 'endDevice', 'ipAdress', 'action'];
	groupId: string;
	sutList: SystemUnderTest[];
	selectedSUT: SystemUnderTest;
	loading = false;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	public pageEvent: PageEvent;
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService
	) {}

	ngOnInit() {
		// TODO: Benjamin: change this function to work with contextId
		this.loadSUTList("contextId", 0, 10);
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
		this.dataSource.filter = filterValue;
	}

	public handlePage(e?: PageEvent) {
		this.currentPage = e.pageIndex;
		this.pageSize = e.pageSize;
		this.loadSUTList(this.groupId, e.pageIndex, e.pageSize);
		return e;
	}


    openDialogShowSuT(type: string): void {

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		dialogConfig.data = {
			type: type,
			groupId: this.groupId
		};

		const dialogRef = this.dialog.open(SUTDetailsComponent, dialogConfig);

	}

	public loadSUTList(groupId: string, page: number, size: number){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'sut/' + groupId + '/' + page + '/' + size)
			.subscribe(
				data => {
					this.sutList = data.sutList;
					this.totalSize = data.totalSize;
					this.dataSource.data = this.sutList;
					if (data.sutList.length > 0) {
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

	public onMenuTriggerClick(sut: SystemUnderTest) {
		this.selectedSUT = sut;
	}

}
