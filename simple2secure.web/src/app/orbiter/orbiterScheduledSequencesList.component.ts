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
import {MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {TestStatus} from '../_models/testStatus';
import {AlertService, DataService, HttpService} from '../_services';
import {HelperService} from '../_services/helper.service';
import {PageEvent} from '@angular/material/paginator';
import {TestSequenceResultDetailsComponent} from '../report/testSequenceResultDetails.component';
import {TestSequenceRunDTO} from '../_models/DTO/testSequenceRunDTO';

@Component({
	moduleId: module.id,
	styleUrls: ['orbiter.css'],
	templateUrl: 'orbiterScheduledSequencesList.component.html'
})

export class OrbiterScheduledSequencesListComponent {

	selectedSequenceRun: TestSequenceRunDTO = new TestSequenceRunDTO();
	podId: string;
	isTestChanged: boolean;
	showTestResult = false;
	sequenceRuns: TestSequenceRunDTO[] = [];
	displayedColumns = ['name', 'time', 'type', 'status', 'action'];
	loading = false;
	url: string;
	public pageEvent: PageEvent;
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService,
		private utils: HelperService
	) {
	}

	ngOnInit() {
		this.loadScheduledSequences(0 , 10);
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
		this.loadScheduledSequences(e.pageIndex, e.pageSize);
		return e;
	}

	public onMenuTriggerClick(sequenceRun: TestSequenceRunDTO) {
		this.showTestResult = false;
		if (sequenceRun.sequenceRun.sequenceStatus == TestStatus.EXECUTED){
			if (sequenceRun.sequenceRun.sequenceContent != null){
				this.showTestResult = true;
			}
		}
		this.selectedSequenceRun = sequenceRun;
	}

	public loadScheduledSequences(page: number, size: number){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'sequence/scheduledSequence/' + page + '/' + size)
			.subscribe(
				data => {
					this.sequenceRuns = data.sequences;
					this.dataSource.data = this.sequenceRuns;
					this.totalSize = data.totalSize;

					if (!this.isTestChanged){
						if (data.sequences.length > 0) {
							this.alertService.success(this.translate.instant('message.data'));
						}
						else {
							this.alertService.error(this.translate.instant('message.data.notProvided'));
						}
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


	public openDialogShowSequenceResult(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			result: this.selectedSequenceRun.sequenceResult
		};

		this.dialog.open(TestSequenceResultDetailsComponent, dialogConfig);

	}

}
