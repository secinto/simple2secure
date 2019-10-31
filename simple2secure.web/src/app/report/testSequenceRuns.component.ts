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
import {AlertService, DataService, HttpService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import { TestSequenceResult } from '../_models/testSequenceResult';
import { PodDTO } from '../_models/DTO/podDTO';
import { TestSequenceResultDetailsComponent } from './testSequenceResultDetails.component';

@Component({
	moduleId: module.id,
	templateUrl: 'testSequenceRuns.component.html'
})

export class TestSequenceRunsComponent {

    currentUser: any;
    testSequenceResults: TestSequenceResult[] = [];
    seq_id: string;
	selectedRun: any[];
	loading = false;
	pod: PodDTO;
	displayedColumns = ['name', 'license', 'group', 'action'];
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private router: Router,
		private dataService: DataService,
		private route: ActivatedRoute,
		private dialog: MatDialog,
		private translate: TranslateService
	)
	{}

	ngOnInit() {
        this.pod = JSON.parse(localStorage.getItem('pod'));
        let path = this.route.snapshot.url;
        this.seq_id = path[1].toString();
        this.loadSequenceRunResults();
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

	loadSequenceRunResults() {
		this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'sequence/sequencerunresults/' + this.seq_id)
			.subscribe(
				data => {
                    this.testSequenceResults = data;
					if (data.length > 0) {
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



	openDialogShowTestResult(): void {
		const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '700px';
        dialogConfig.height = '800px'
		dialogConfig.data = {
			seqResult: this.selectedRun,
		};

		this.dialog.open(TestSequenceResultDetailsComponent, dialogConfig);

	}

	public onMenuTriggerClick(selectedRun: any[]) {
		this.selectedRun = selectedRun;
	}
}
