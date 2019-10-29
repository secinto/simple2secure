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
import {PodDTO} from '../_models/DTO/podDTO';
import {ContextDTO, Test, User} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {TestObjWeb} from '../_models/testObjWeb';
import {AlertService, HttpService, DataService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TestDetailsComponent} from './testDetails.component';
import { TestSequence } from '../_models/testSequence';
import { TestSequenceDetailsComponent } from './testSequenceDetails.component';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterToolTestSequenceList.component.html'
})

export class OrbiterToolTestSequenceListComponent {

	selectedSequence: TestSequence = new TestSequence();
	podId: string;
	isSequenceChanged: boolean;
	pod: PodDTO;
	sequences: TestSequence[];
	testSequence: string[];
	context: ContextDTO;
	currentUser: any;
	displayedColumns = ['testId', 'version', 'status', 'action'];
	loading = false;
	url: string;
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
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.isSequenceChanged = false;
		this.context = JSON.parse(localStorage.getItem('context'));
		this.pod = this.dataService.getPods();
		this.loadSequences(this.pod.pod.deviceId);
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
		this.dataSource.filter = filterValue;
	}

	public onMenuTriggerClick(sequence: TestSequence) {
		this.selectedSequence = sequence;

	}

	public loadSequences(podId: string){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'sequence/' + podId)
			.subscribe(
				data => {
					this.sequences = data;
					this.dataSource.data = this.sequences;
					if (!this.isSequenceChanged){
						if (data.length > 0) {
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
			sequence: this.selectedSequence,
			type: type
		};

		const dialogRef = this.dialog.open(TestSequenceDetailsComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.isSequenceChanged = true;
				this.loadSequences(this.pod.pod.deviceId);
			}
		});

	}

	public runSequence(){

		this.loading = true;

		this.url = environment.apiEndpoint + 'sequence/scheduleSequence/' +
			this.context.context.id + '/' + this.currentUser.userID;
		this.httpService.post(this.selectedSequence, this.url).subscribe(
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
				this.deleteSequence(this.selectedSequence);
			}
		});
	}

	public deleteSequence(selectedSequence: TestSequence) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'sequence/delete/' + selectedSequence.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.sequence.delete'));
				this.loading = false;
				this.isSequenceChanged = true;
				this.loadSequences(this.pod.pod.deviceId);
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
