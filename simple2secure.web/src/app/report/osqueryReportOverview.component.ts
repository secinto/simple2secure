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
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {HttpService, AlertService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {Modal} from 'ngx-modialog/plugins/bootstrap';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {OsQueryReportDetailsComponent} from './osqueryReportDetails.component';
import {OsQueryReportDTO} from '../_models/DTO/osQueryReportDTO';
import {PageEvent} from '@angular/material/paginator';
import {SelectionModel} from '@angular/cdk/collections';
import {CompanyGroup, Device} from "../_models";
import {HttpParams} from "@angular/common/http";

@Component({
	moduleId: module.id,
	styleUrls: ['query.result.css'],
	templateUrl: 'osqueryReportOverview.component.html'
})

export class OsQueryReportOverviewComponent {
	public reportDTO: OsQueryReportDTO;
	groups: CompanyGroup[] = [];
	selectedGroups: CompanyGroup[] = [];
	selectedDevices: Device[] = [];
	devices: Device[] = [];
	selectedReport: any;
	loading = false;
	displayedColumns = ['select', 'probe', 'hostname', 'query', 'timestamp'];
	public pageEvent: PageEvent;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;
	@ViewChild('selectgroup') selectElRefGroups;
	@ViewChild('selectdevices') selectElRefDevices;
	selection = new SelectionModel<any>(true, []);
	showDeleteButton = false;
	numSelected = 0;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private dataService: DataService,
		public modal: Modal,
		private dialog: MatDialog,
		private translate: TranslateService)
	{}

	ngOnInit() {
		this.getGroups();
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
		console.log(this.devices);
		this.currentPage = e.pageIndex;
		this.pageSize = e.pageSize;
		if(this.selectedDevices.length > 0){
			this.loadReportsByDeviceIds(this.selectElRefDevices.value, e.pageIndex, e.pageSize);
		}
		else{
			this.loadReportsByGroupIds(this.selectedGroups, e.pageIndex, e.pageSize);
		}
		return e;
	}

	public updateGroups(){
		this.selectedGroups = this.selectElRefGroups.value;
		if(!this.selectElRefGroups.value.isEmpty){
			this.loadReportsByGroupIds(this.selectElRefGroups.value, 0, 10);
			this.getProbesByGroupIds(this.selectElRefGroups.value);
			this.selectElRefGroups.close();
			this.selectElRefDevices.open();
		}
	}

	public updateDevices(){
		this.selectedDevices = this.selectElRefDevices.value;
		if(!this.selectElRefDevices.value.isEmpty){
			this.loadReportsByDeviceIds(this.selectElRefDevices.value, 0, 10);
			this.selectElRefDevices.close();
		}
	}

	public getGroups() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'group/context')
			.subscribe(
				data => {
					this.groups = data;
					this.selectedGroups = data;
					this.loadReportsByGroupIds(data, 0, 10);
					this.getProbesByGroupIds(data);
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

	getProbesByGroupIds(groups: CompanyGroup[]) {
		this.httpService.post(groups,environment.apiEndpoint + 'device/group')
			.subscribe(
				data => {
					this.devices = data;
				});
	}

	loadReportsByGroupIds(groups: CompanyGroup[], page: number, size: number){
		this.loading = true;
		this.httpService.post(groups,environment.apiEndpoint + 'reports/groups/' + page + '/' + size)
			.subscribe(
				data => {
					this.reportDTO = data;
					this.dataSource.data = this.reportDTO.report;
					this.totalSize = data.totalSize;
					if (data.report.length > 0) {
						this.alertService.success(this.translate.instant('message.report'));
					}
					else {
						this.alertService.error(this.translate.instant('message.report.notProvided'));
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

	private loadReportsByDeviceIds(devices: Device[], page: number, size: number){
		this.loading = true;
		this.httpService.post(devices,environment.apiEndpoint + 'reports/devices/' + page + '/' + size)
			.subscribe(
				data => {
					this.reportDTO = data;
					this.dataSource.data = this.reportDTO.report;
					this.totalSize = data.totalSize;
					if (data.report.length > 0) {
						this.alertService.success(this.translate.instant('message.report'));
					}
					else {
						this.alertService.error(this.translate.instant('message.report.notProvided'));
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

	public onDeleteClick(report: any) {
		this.selectedReport = report;
		this.openDialog(this.selectedReport);
	}

	public openDialog(report: any) {
		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.report.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteReport(this.selectedReport);
			}
		});
	}

	public deleteReport(report: any) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'reports/' + report.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.report.delete'));
				this.loadReportsByGroupIds(this.selectedGroups, this.currentPage, this.pageSize);
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

	openDialogShowReportDetails(report: any): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			report: report,
		};

		this.dialog.open(OsQueryReportDetailsComponent, dialogConfig);

	}

	/** Whether the number of selected elements matches the total number of rows. */
	isAllSelected() {
		this.numSelected = this.selection.selected.length;
		const numRows = this.dataSource.data.length;

		if (this.numSelected > 0){
			this.showDeleteButton = true;
		}
		else{
			this.showDeleteButton = false;
		}

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

	deleteSelectedReports(){
		this.loading = true;
		this.httpService.post(this.selection.selected, environment.apiEndpoint + 'reports/delete/selected').subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.report.delete'));
				this.loadReportsByGroupIds(this.selectedGroups, this.currentPage, this.pageSize);
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
		this.selection.clear();
	}
}
