import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatDialogConfig, MatDialog, MatSort, MatPaginator, PageEvent} from '@angular/material';
import { AlertService, HttpService, DataService } from '../_services';
import { TranslateService } from '@ngx-translate/core';
import { Router, ActivatedRoute } from '@angular/router';
import {environment} from '../../environments/environment';
import { SUTDetailsComponent } from './sutDetails.component';
import { SystemUnderTest } from '../_models/SystemUnderTest';
import { DeviceType } from '../_models/DeviceType';

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
	 styleUrls: ['sutList.css'],
     templateUrl: 'orbiterSystemsUnderTestList.component.html'
 })

export class OrbiterSystemsUnderTestListComponent {

	displayedColumnsMonitored = ['name', 'groupId', 'endDevice', 'ipAdress', 'deviceStatus'];
	displayedColumnsTargeted = ['name', 'groupId', 'endDevice', 'ipAdress', 'action'];
	groupId: string;
	monitoredSUT: SystemUnderTest[];
	otherSUT: SystemUnderTest[];
	selectedSUT: SystemUnderTest;
	loading = false;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	public pageEvent: PageEvent;
	dataSourceMonitored = new MatTableDataSource();
	dataSourceOther = new MatTableDataSource();
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
		const groups = JSON.parse(localStorage.getItem('groups'));
		this.groupId = groups[0].id;
		this.loadMonitoredSUTList(this.groupId, 0, 10);
		this.loadOtherSUTList(this.groupId, 0, 10);
	}

	ngAfterViewInit() {
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
	}

	public handlePage(e?: PageEvent) {
		this.currentPage = e.pageIndex;
		this.pageSize = e.pageSize;
		this.loadMonitoredSUTList(this.groupId, e.pageIndex, e.pageSize);
		this.loadOtherSUTList(this.groupId, e.pageIndex, e.pageSize);
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

	public loadMonitoredSUTList(groupId: string, page: number, size: number){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'sut/' + groupId + '/' + DeviceType.PROBE + '/' + page + '/' + size)
			.subscribe(
				data => {
					this.monitoredSUT = data.sutList;
					this.dataSourceMonitored = data.sutList;
					this.totalSize = data.totalSize;
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
	
	public loadOtherSUTList(groupId: string, page: number, size: number){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'sut/' + groupId + '/' + DeviceType.WWW + '/' + page + '/' + size)
			.subscribe(
				data => {
					this.otherSUT = data.sutList;
					this.dataSourceOther = data.sutList;
					this.totalSize = data.totalSize;
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
