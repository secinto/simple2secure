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

import { HelperService } from '../../_services/helper.service';
import { Component, ViewChild, ElementRef } from '@angular/core';
import { MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '../../../environments/environment';
import { TestRunDTO } from '../../_models/DTO/testRunDTO';
import { TestStatus } from '../../_models/testStatus';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { TestResultDetailsComponent } from '../report/testResultDetails.component';
import { PageEvent } from '@angular/material/paginator';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { HttpParams } from '@angular/common/http';
import { OrbiterComponent } from './orbiter.component';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { fromEvent } from 'rxjs';

@Component({
    moduleId: module.id,
    styleUrls: ['orbiter.css'],
    templateUrl: 'orbiterToolTestScheduledList.component.html'
})

export class OrbiterToolTestScheduledListComponent extends OrbiterComponent {

    selectedTestRun: TestRunDTO = new TestRunDTO();
    podId: string;
    isTestChanged: boolean;
    showTestResult = false;
    tests: TestRunDTO[];
    displayedColumns = ['podId', 'name', 'hostname', 'time', 'type', 'status', 'action'];
    loading = false;
    url: string;
    public pageEvent: PageEvent;
    public pageSize = 10;
    public totalSize = 0;
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filterValue') filterValue: ElementRef;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        dialog: MatDialog,
        private utils: HelperService,
        private translate: TranslateService) {
        super(dialog);
    }

    ngOnInit() {
        this.loadScheduledTests(0, 10, this.filterValue.nativeElement.value);
    }

    ngAfterViewInit() {
        // This is currently only for local sorting
        this.dataSource.sort = this.sort;

        fromEvent(this.filterValue.nativeElement, 'keyup')
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                tap(() => {
                    this.paginator.pageIndex = 0;
                    this.loadScheduledTests(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
                })
            ).subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadScheduledTests(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value))
            )
            .subscribe();

    }

    public onMenuTriggerClick(test: TestRunDTO) {
        this.showTestResult = false;
        if (test.testRun.testStatus == TestStatus.EXECUTED) {
            if (test.testResult != null) {
                this.showTestResult = true;
            }
        }
        this.selectedTestRun = test;
    }

    public loadScheduledTests(page: number, size: number, filter: string) {
        this.loading = true;
        const params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size))
            .set('filter', filter);

        this.httpService.getWithParams(environment.apiTestScheduledTestsPagination, params)
            .subscribe(
                data => {
                    this.dataSource.data = data.tests;
                    this.totalSize = data.totalSize;
                    if (!this.isTestChanged) {
                        this.alertService.showSuccessMessage(data, 'message.data', false, true);
                    }
                },
                error => {
                    this.alertService.showErrorMessage(error);
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
                this.deleteTestRun(this.selectedTestRun);
            }
        });
    }

    public deleteTestRun(testRun: TestRunDTO) {
        this.loading = true;
        const apiUrl = environment.apiTestTestRunDelete.replace('{testRunId}', testRun.testRun.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.test.delete');
                this.loading = false;
                this.isTestChanged = true;
                this.loadScheduledTests(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public openDialogShowTestResult(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            result: this.selectedTestRun.testResult.result
        };

        this.dialog.open(TestResultDetailsComponent, dialogConfig);

    }
}
