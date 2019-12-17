import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatDialogConfig, MatDialog, MatSort, MatPaginator, PageEvent} from '@angular/material';
import { AlertService, HttpService, DataService } from '../_services';
import { TranslateService } from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import { SUTDetailsComponent } from './sutDetails.component';
import { SystemUnderTest } from '../_models/systemUnderTest';
import { DeviceType } from '../_models/deviceType';

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

	displayedColumnsMonitored = ['name', 'device', 'ipAdress', 'deviceStatus'];
	displayedColumnsTargeted = ['name', 'device', 'ipAdress', 'action'];
	contextId: string;
	monitoredSystems: SystemUnderTest[];
	systemsUnderTest: SystemUnderTest[];
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
		// TODO: Benjamin: change this function to work with contextId
		this.loadMonitoredSystemsList(0, 10);
		this.loadSUTList(0, 10);
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
		this.loadMonitoredSystemsList(e.pageIndex, e.pageSize);
		this.loadSUTList(e.pageIndex, e.pageSize);
		return e;
	}


    openDialogShowSuT(type: string): void {

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		dialogConfig.data = {
			type: type,
		};

		const dialogRef = this.dialog.open(SUTDetailsComponent, dialogConfig);

	}

	public loadMonitoredSystemsList(page: number, size: number){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'devices/' + DeviceType.PROBE + '/' + page + '/' + size)
			.subscribe(
				data => {
					this.monitoredSystems = data.devices;
					this.dataSourceMonitored = data.devices;
					this.totalSize = data.totalSize;
					console.log(this.monitoredSystems);
					if (data.devices.length > 0) {
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
	public loadSUTList(page: number, size: number){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'sut/' + page + '/' + size)
			.subscribe(
				data => {
					this.systemsUnderTest = data.sutList;
					this.dataSourceOther = data.sutList;
					this.totalSize = data.totalSize;
					console.log(this.systemsUnderTest);
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
