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
import {CompanyGroup} from '../_models/index';
import {MatDialog, MatPaginator, MatSort, MatTableDataSource, PageEvent} from '@angular/material';
import {UserDeviceChangeGroupComponent} from '../user';
import {HttpErrorResponse} from '@angular/common/http';
import {AlertService, HttpService} from '../_services';
import {TranslateService} from '@ngx-translate/core';
import {Device} from '../_models';
import {environment} from '../../environments/environment';
import {saveAs as importedSaveAs} from 'file-saver';

@Component({
	moduleId: module.id,
	templateUrl: 'devicesList.component.html'
})

export class DevicesListComponent {

	selectedItem: any;
	groupAdded = false;
	loading = false;
	devices: Device[];
	groupsForSelect: CompanyGroup[];
	selectedGroup: CompanyGroup;
	dataSource = new MatTableDataSource();
	@ViewChild('paginator') paginator: MatPaginator;
	@ViewChild('sort') sort: MatSort;
	displayedColumnsDevices = ['probeId', 'group', 'hostname', 'type', 'status', 'action'];
	public pageEvent: PageEvent;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private dialog: MatDialog,
		private alertService: AlertService,
		private translate: TranslateService,
		private httpService: HttpService)
	{
	}

	ngOnInit(){
		this.loadGroups();
		this.loadDevices(0, 10);
		/*
		 * The filterPredicate is necessary because we want to filter over nested objects
		*/
		this.dataSource.filterPredicate = (device: Device, filter: string) => {
		  let valid = false;

		  const transformedFilter = filter.trim().toLowerCase();

		  Object.keys(device).map(key => {
			if (
			  key === 'info' &&
			  (
				device.info.name.toLowerCase().includes(transformedFilter)
				|| device.info.type.toLowerCase().includes(transformedFilter)
				|| device.info.deviceStatus.toLowerCase().includes(transformedFilter)
			  )
			) {
			  valid = true;
			} else {
			  if (('' + device[key]).toLowerCase().includes(transformedFilter)) {
				valid = true;
			  }
			}
		  });

		  return valid;
		};
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
	}

	public handlePage(e?: PageEvent) {
		this.currentPage = e.pageIndex;
		this.pageSize = e.pageSize;
		this.loadDevices(e.pageIndex, e.pageSize);
		return e;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
		this.dataSource.filter = filterValue;
	}

	loadDevices(page: number, size: number) {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'devices/' + page + '/' + size)
			.subscribe(
				data => {
					this.devices = data.devices;
					this.dataSource.data = this.devices;
					this.totalSize = data.totalSize;
					if (data.devices.length > 0) {
						this.alertService.success(this.translate.instant('message.data'));
					}
					else {
						this.alertService.error(this.translate.instant('message.data.notProvided'));
					}

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
	
	loadGroups() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'group/context')
			.subscribe(
				data => {
					this.groupsForSelect = data;
					this.selectedGroup = this.groupsForSelect[0];
					if (data.length > 0) {
						this.alertService.success(this.translate.instant('message.data'));
					}
					else {
						this.alertService.error(this.translate.instant('message.data.notProvided'));
					}
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

	openDialogChangeDeviceGroup(): void {
		const dialogRef = this.dialog.open(UserDeviceChangeGroupComponent, {
			width: '350px',
			data: this.selectedItem
		});

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.group.add'));
				this.groupAdded = true;
				this.loadDevices(this.currentPage, this.pageSize);
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

	public download() {
		this.loading = true;
		this.httpService.getFile(environment.apiEndpoint + 'download/' + this.selectedGroup.id)
			.subscribe(
				data => {
					importedSaveAs(data, 's2s_setup.zip');
					this.loading = false;
				},
				error => {
					this.alertService.error(error.errorMessage);
					this.loading = false;
				});
	}
}
