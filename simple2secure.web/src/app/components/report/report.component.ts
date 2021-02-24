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

import { AfterContentChecked, ChangeDetectorRef, Component, ElementRef, ViewChild } from '@angular/core';
import { CompanyGroup } from '../../_models/companygroup';
import { HttpParams } from '@angular/common/http';
import { ReportType } from '../../_models/reportType';
import { environment } from '../../../environments/environment';
import { Device } from '../../_models/device';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';
import { TranslateService } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { DeviceType } from '../../_models/deviceType';

@Component({
	moduleId: module.id,
	styleUrls: ['report.css'],
	templateUrl: 'report.component.html'
})

export class ReportComponent implements AfterContentChecked {

	repType = ReportType;
	devType = DeviceType;
	devices: Device[] = [];
	selection = new SelectionModel<any>(true, []);
	isDeleteButtonDisabled = false;
	numSelected = 0;
	dataSource = new MatTableDataSource();
	selectedGroups: CompanyGroup[] = [];
	selectedDevices: Device[] = [];
	groups: CompanyGroup[] = [];
	loading = false;
	public totalSize = 0;
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild('filterValue') filterValue: ElementRef;
	@ViewChild('selectgroup') selectElRefGroups;
	@ViewChild('selectdevices') selectElRefDevices;

	constructor(
		public route: ActivatedRoute,
		public router: Router,
		public httpService: HttpService,
		public alertService: AlertService,
		public dataService: DataService,
		public dialog: MatDialog,
		public translate: TranslateService,
		public cdr: ChangeDetectorRef) {
	}

	ngAfterContentChecked() {
		this.cdr.detectChanges();
	}

	loadReports(reportType: ReportType) {

		if (this.selectedDevices.length > 0) {
			this.loadReportsByDeviceIds(this.selectElRefDevices.value, reportType);
		}
		else {
			this.loadReportsByGroupIds(this.selectedGroups, reportType);
		}
	}

	getDevicesByGroupIds(groups: CompanyGroup[], reportType: ReportType, deviceType: DeviceType) {

		const params = new HttpParams()
			.set('deviceType', deviceType)
			.set('reportType', reportType);

		this.httpService.postWithParams(groups, environment.apiDevicesByGroup, params)
			.subscribe(
				data => {
					this.devices = data;
					if (this.devices && this.devices.length > 0) {
						this.selectedDevices = data;
						this.selectElRefDevices.value = data;
					}
				});
	}

	public updateGroups(reportType: ReportType, deviceType: DeviceType) {
		this.selectedGroups = this.selectElRefGroups.value;
		if (this.selectElRefGroups.value != 0) {
			this.loadReportsByGroupIds(this.selectElRefGroups.value, reportType);
			this.getDevicesByGroupIds(this.selectElRefGroups.value, reportType, deviceType);
		}
		else {
			this.dataSource.data = [];
			this.totalSize = 0;
			this.devices = [];
		}
	}

	public updateDevices(reportType: ReportType) {
		this.selectedDevices = this.selectElRefDevices.value;
		if (this.selectElRefDevices.value.length != 0) {
			this.loadReportsByDeviceIds(this.selectElRefDevices.value, reportType);
		}
	}

	public getGroups(reportType: ReportType, deviceType: DeviceType) {
		this.loading = true;
		this.httpService.get(environment.apiGroupByContext)
			.subscribe(
				data => {
					this.groups = data;
					this.selectedGroups = data;
					this.loadReportsByGroupIds(data, reportType);
					this.getDevicesByGroupIds(data, reportType, deviceType);
				},
				error => {
					this.alertService.showErrorMessage(error);
				});
		this.loading = false;
	}

	loadReportsByGroupIds(groups: CompanyGroup[], reportType: ReportType) {
		this.loading = true;
		let apiUrl = environment.apiNetworkReportsGroupsPagination;
		if (reportType == ReportType.OSQUERY) {
			apiUrl = environment.apiReportsGroupsPagination;
		}
		else if (reportType == ReportType.TEST) {
			apiUrl = environment.apiTestResultGroups;
		}
		else if (reportType == ReportType.TESTSEQUENCE) {
			apiUrl = environment.apiSequenceResultGroup;
		}

		this.httpService.postWithParams(groups, apiUrl, this.getParams())
			.subscribe(
				data => {
					this.dataSource.data = data.report;
					this.totalSize = data.totalSize;
					this.alertService.showSuccessMessage(data.report, 'message.report', false, true);
					this.loading = false;

				},
				error => {
					this.alertService.showErrorMessage(error);
					this.dataSource.data = [];
					this.totalSize = 0;
					// this.cdr.detectChanges();
					this.loading = false;
				});
	}

	private loadReportsByDeviceIds(devices: Device[], reportType: ReportType) {
		let apiUrl = environment.apiNetworkReportsDevicesPagination;
		if (reportType == ReportType.OSQUERY) {
			apiUrl = environment.apiReportsDevicesPagination;
		}
		else if (reportType == ReportType.TEST) {
			apiUrl = environment.apiTestResultDevices;
		}
		else if (reportType == ReportType.TESTSEQUENCE) {
			apiUrl = environment.apiSequenceResultDevices;
		}

		this.loading = true;

		this.httpService.postWithParams(devices, apiUrl, this.getParams())
			.subscribe(
				data => {
					this.dataSource.data = data.report;
					this.totalSize = data.totalSize;
					this.alertService.showSuccessMessage(data.report, 'message.report', false, true);
					this.loading = false;

				},
				error => {
					this.alertService.showErrorMessage(error);
					this.dataSource.data = [];
					this.totalSize = 0;
					// this.cdr.detectChanges();
					this.loading = false;
				});
	}

	deleteSelectedReports(reportType: ReportType) {
		let apiUrl = environment.apiReportsDeleteSelected;

		if (reportType == ReportType.NETWORK) {
			apiUrl = environment.apiNetworkReportsDeleteSelected;
		}
		else if (reportType == ReportType.TEST) {
			apiUrl = environment.apiTestResultDeleteSelected;
		}
		else if (reportType == ReportType.TESTSEQUENCE) {
			apiUrl = environment.apiSequenceResultDeleteSelected;
		}
		this.loading = true;
		this.httpService.post(this.selection.selected, apiUrl).subscribe(
			data => {
				this.alertService.showSuccessMessage(data, 'message.report.delete');
				this.loadReportsByGroupIds(this.selectedGroups, reportType);
				this.loading = false;
			},
			error => {
				this.alertService.showErrorMessage(error);
				this.loading = false;
			});
		this.selection.clear();
	}

	/** Whether the number of selected elements matches the total number of rows. */
	isAllSelected() {
		this.numSelected = this.selection.selected.length;
		const numRows = this.dataSource.data.length;

		this.isDeleteButtonDisabled = this.numSelected <= 0;

		return this.numSelected === numRows;
	}

	/** Selects all rows if they are not all selected; otherwise clear selection. */
	masterToggle() {
		this.isAllSelected() ?
			this.selection.clear() :
			this.dataSource.data.forEach(row => this.selection.select(row));
	}

	/** The label for the checkbox on the passed row */
	checkboxLabel(row?: any): string {
		if (!row) {
			return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
		}
		return `${this.selection.isSelected(row) ? 'deselect' : 'select'}`;
	}

	getParams() {
		const params = new HttpParams()
			.set('page', String(this.paginator.pageIndex))
			.set('size', String(this.paginator.pageSize))
			.set('filter', this.filterValue.nativeElement.value);

		return params;
	}


}
