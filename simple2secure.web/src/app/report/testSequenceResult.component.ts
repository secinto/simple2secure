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
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig, PageEvent} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {ContextDTO, TestResultDTO} from '../_models';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TestResultDetailsComponent} from './testResultDetails.component';
import { TestSequenceResult } from '../_models/testSequenceResult';
import { PodDTO } from '../_models/DTO/podDTO';
import { TestSequence } from '../_models/testSequence';
import {TestSequenceResultDetailsComponent} from './testSequenceResultDetails.component';

@Component({
	moduleId: module.id,
	templateUrl: 'testSequenceResult.component.html'
})

export class TestSequenceResultComponent {
	context: ContextDTO;
    testSequenceResults: TestSequenceResult[] = [];
	loading = false;
	displayedColumns = ['name', 'podId', 'timestamp'];
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
		private router: Router,
		private dataService: DataService,
		private route: ActivatedRoute,
		private dialog: MatDialog,
		private translate: TranslateService
	)
	{}

	ngOnInit() {
		this.context = JSON.parse(localStorage.getItem('context'));
        this.loadSequenceResults(0, 10);
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
		this.loadSequenceResults(e.pageIndex, e.pageSize);
		return e;
	}


    loadSequenceResults(page: number, size: number) {
        this.httpService.get(environment.apiEndpoint + 'sequence/result/' + this.context.context.id + '/' + page + '/' + size)
        .subscribe(
            data => {
                this.testSequenceResults = data.results;
                console.log(data.results);
				this.totalSize = data.totalSize;
                if (data.results.length > 0) {
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

	public openDialogShowTestSequenceResult(testSequenceResult: TestSequenceResult){
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			result: testSequenceResult,
		};

		this.dialog.open(TestSequenceResultDetailsComponent, dialogConfig);
	}
}
