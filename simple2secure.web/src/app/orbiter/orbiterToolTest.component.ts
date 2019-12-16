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
import {Device} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {PageEvent} from '@angular/material/paginator';

@Component({
	moduleId: module.id,
	styleUrls: ['orbiter.css'],
	templateUrl: 'orbiterToolTest.component.html'
})

export class OrbiterToolTestComponent {

	selectedPod: Device;
	pods: Device[];
	displayedColumns: string[] = ['podId', 'hostname', 'group', 'status', 'action'];
	loading = false;
	dataSource = new MatTableDataSource();
	public pageEvent: PageEvent;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
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
		this.loadPods(0, 10);
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
		this.loadPods(e.pageIndex, e.pageSize);
		return e;
	}

	public onMenuTriggerClick(pod: Device) {
		this.selectedPod = pod;
	}

	loadPods(page: number, size: number) {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'device/pods/' + page + '/' + size)
			.subscribe(
				data => {
					this.pods = data.devices;
					this.dataSource.data = this.pods;
					this.totalSize = data.totalSize;
					if (data.devices.length > 0) {
						this.alertService.success(this.translate.instant('message.data'));
					}
					else {
						this.alertService.error(this.translate.instant('message.data.notProvided'));
					}
				},
				error => {
					console.log(error);
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
				});

		this.loading = false;
	}

	public showPodTests() {
		this.router.navigate([this.selectedPod.deviceId], {relativeTo: this.route});
	}

	public showSequences() {
		this.router.navigate(['sequences/' + this.selectedPod.deviceId], {relativeTo: this.route});
	}
}
