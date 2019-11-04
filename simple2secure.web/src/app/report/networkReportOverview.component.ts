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
import {NgxSpinnerService} from 'ngx-spinner';
import {HttpService, AlertService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {Modal} from 'ngx-modialog/plugins/bootstrap';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {ContextDTO, NetworkReport, NetworkReportDTO} from '../_models/index';
import {NetworkReportDetailsComponent} from './networkReportDetails.component';

@Component({
	moduleId: module.id,
	styleUrls: ['network.result.css'],
	templateUrl: 'networkReportOverview.component.html'
})

export class NetworkReportOverviewComponent {
	currentUser: any;
	reportDTO: NetworkReportDTO;
	context: ContextDTO;
	selectedReport: any;
	loading = false;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;

	displayedColumns = ['probe', 'hostname', 'processorName', 'startTime', 'action'];
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

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
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadAllReports(0, 10);
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
		//this.dataSource.paginator = this.paginator;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
		this.dataSource.filter = filterValue;
	}

	public handlePage(e: any) {
		this.loadAllReports(e.pageIndex, e.pageSize);
		this.totalSize = this.reportDTO.totalSize;
		this.paginator.length = this.totalSize;
		this.paginator.pageSize = this.pageSize;
		this.paginator.pageIndex = this.currentPage;
		//this.dataSource.paginator = this.paginator;
	}

	private loadAllReports(page: number, size: number) {
		this.loading = true;
		this.currentPage = page;
		this.pageSize = size;
		this.httpService.get(environment.apiEndpoint + 'reports/network/' + this.context.context.id + '/' + page + '/' + size)
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
		this.loading = false;
	}

	public deleteReport(report: any) {
		this.httpService.delete(environment.apiEndpoint + 'reports/network/' + report.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.report.delete'));
				this.loadAllReports(this.currentPage, this.pageSize);
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

	openDialogShowReportDetails(report: any): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			report: report,
		};

		this.dialog.open(NetworkReportDetailsComponent, dialogConfig);

	}
}
