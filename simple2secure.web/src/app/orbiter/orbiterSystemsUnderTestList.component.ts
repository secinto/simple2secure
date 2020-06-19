import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatDialogConfig, MatDialog, MatSort, MatPaginator, PageEvent} from '@angular/material';
import { AlertService, HttpService, DataService } from '../_services';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../environments/environment';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { SUTDetailsComponent } from './sutDetails.component';
import { SystemUnderTest } from '../_models/systemUnderTest';
import { DeviceType } from '../_models/deviceType';
import { DeviceStatus } from '../_models/deviceStatus';


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
	 styleUrls: ['sut.css'],
     templateUrl: 'orbiterSystemsUnderTestList.component.html'
 })

export class OrbiterSystemsUnderTestListComponent {

	displayedColumnsMonitored = ['name', 'device', 'ipAdress', 'deviceStatus'];
	displayedColumnsTargeted = ['name', 'Protocol', 'action'];
	contextId: string;
	monitoredSystems: SystemUnderTest[];
	selectedSUT: SystemUnderTest;
	loading = false;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	public totalSizeSut = 0;
	public pageEvent: PageEvent;
	dataSourceMonitored = new MatTableDataSource();
	dataSourceSut = new MatTableDataSource();
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

    openDialogShowSuT(action: string): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		if(action == 'new'){
			dialogConfig.data = {
				action: action,
			};
		}else if( action == 'edit') {
			dialogConfig.data = {
				action: action,
				sut: this.selectedSUT
			};
		}

		const dialogRef = this.dialog.open(SUTDetailsComponent, dialogConfig);
		dialogRef.afterClosed().subscribe(data => {
			this.loadSUTList(this.currentPage, this.pageSize);
		});
	}
	
	
	openDeleteSutDialog() {
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
				this.deleteSUT(this.selectedSUT);
			}
			this.loadSUTList(this.currentPage, this.pageSize);
		});
	}
	
	
	public loadMonitoredSystemsList(page: number, size: number){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'devices/' + DeviceType.PROBE + '/' + page + '/' + size)
			.subscribe(
				data => {
					for (let device of data.devices){
						if(device.info.deviceStatus == DeviceStatus.ONLINE){
							this.dataSourceMonitored.data.push(device);
							this.dataSourceMonitored.data = this.dataSourceMonitored.data;
						}
					}
					this.totalSize = data.devices.totalSize;
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
					this.dataSourceSut.data = data.sutList;
					this.totalSizeSut = data.totalSize;
					this.loading = false;
					this.alertService.success(this.translate.instant('message.data'));
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
	
	public deleteSUT(sut){
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'sut/' + sut.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.sut.delete'));
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
}
