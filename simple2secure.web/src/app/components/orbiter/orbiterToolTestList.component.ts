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

import { Component, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog, PageEvent } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { TestDetailsComponent } from './testDetails.component';
import { HttpParams } from '@angular/common/http';
import { SystemUnderTest } from '../../_models/systemUnderTest';
import { AlertService } from '../../_services/alert.service';
import { HttpService } from '../../_services/http.service';
import { environment } from '../../../environments/environment';
import { TestWebDTO } from '../../_models/DTO/TestWebDTO';
import { TestSUTDataInput } from '../../_models/DTO/testSUTDataInput';
import { SutInputDataDialogComponent } from './sutInputDataDialog.component';
import { OrbiterComponent } from './orbiter.component';
import { InputDataDialogComponent } from './inputDataDialog.component';
import { saveAs as importedSaveAs } from 'file-saver';
import { fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';

@Component({
    moduleId: module.id,
    styleUrls: ['orbiter.css'],
    templateUrl: 'orbiterToolTestList.component.html'
})

export class OrbiterToolTestListComponent extends OrbiterComponent {

    selectedTest: TestWebDTO = new TestWebDTO();
    isTestChanged: boolean;
    displayedColumns = ['testId', 'version', 'status', 'action'];
    loading = false;
    url: string;
    deviceId: string;
    sutList: SystemUnderTest[] = [];
    isUserAdmin = false;
    testTransferObj: TestSUTDataInput = new TestSUTDataInput();
    public pageEvent: PageEvent;
    public pageSize = 10;
    public currentPage = 0;
    public totalSize = 0;
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filterValue') filterValue: ElementRef;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        dialog: MatDialog,
        private translate: TranslateService,
        private route: ActivatedRoute
    ) {
        super(dialog);
    }

    ngOnInit() {
        this.isTestChanged = false;
        this.deviceId = this.route.snapshot.paramMap.get('id');
        this.paginator.pageSize = 10;
        this.loadTests(this.deviceId, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
        this.getIsUserAdmin(this.deviceId);
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;

        fromEvent(this.filterValue.nativeElement, 'keyup')
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                tap(() => {
                    this.paginator.pageIndex = 0;
                    this.loadTests(this.deviceId, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
                })
            ).subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadTests(this.deviceId, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value))
            )
            .subscribe();
    }

    public onMenuTriggerClick(test: TestWebDTO) {
        this.selectedTest = test;
    }

    public loadTests(podId: string, page: number, size: number, filter: string) {
        this.loading = true;
        const params = new HttpParams()
            .set('usePagination', String(true))
            .set('page', String(page))
            .set('size', String(size))
            .set('filter', filter);

        const apiUrl = environment.apiTestByDeviceIdPagination.replace('{deviceId}', podId);
        this.httpService.getWithParams(apiUrl, params)
            .subscribe(
                data => {
                    this.dataSource.data = data.tests;
                    this.totalSize = data.totalSize;
                    if (!this.isTestChanged) {
                        this.alertService.showSuccessMessage(data.tests, 'message.data', false, true);
                    }

                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    public getIsUserAdmin(podId: string) {
        const params = new HttpParams()
            .set('deviceId', podId);

        const apiUrl = environment.apiUserIsDeviceAdmin.replace('{deviceId}', podId);
        this.httpService.get(apiUrl)
            .subscribe(
                data => {
                    this.isUserAdmin = data;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
    }

    openDialogShowTest(type: string): void {

        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '750px';
        dialogConfig.data = {
            tests: this.selectedTest,
            type: type,
            podId: this.deviceId
        };

        const dialogRef = this.dialog.open(TestDetailsComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true) {
                this.isTestChanged = true;
                this.loadTests(this.deviceId, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
            }
        });

    }

    public prepareTest() {

        const flag = this.parameterArrayToString(this.selectedTest);

        let regexp = new RegExp('USE_SUT_METADATA\{.*\}');
        const isSutTest = regexp.test(flag);

        regexp = new RegExp('\{USE_INPUT_DATA\}');
        const isInputData = regexp.test(flag);

        if (isSutTest || isInputData) {
            this.openSutInputDataDialog(isSutTest, isInputData);
        }
        else {
            this.runTest();
        }
    }

    public runTest() {
        this.testTransferObj.test = this.selectedTest.test;
        this.httpService.post(this.testTransferObj, environment.apiTestScheduleTest).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.test.schedule');
            },
            error => {
                this.alertService.showErrorMessage(error);
            });

        this.loading = false;
    }

    public async openDeleteDialog() {
        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;

        dialogConfig.data = {
            id: 1,
            title: this.translate.instant('message.areyousure'),
            content: this.translate.instant('message.test.dialog')
        };


        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        await dialogRef.afterClosed().subscribe(data => {
            if (data === true) {
                this.deleteTest(this.selectedTest);
            }
        });
    }

    public openSutInputDataDialog(isSutTest: boolean, isInputData: boolean) {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '750px';
        dialogConfig.data = {
            sutList: this.selectedTest.suts,
            inputDataList: this.selectedTest.inputData,
            selectedTest: this.selectedTest.test,
            isSutTest: isSutTest,
            isInputData: isInputData
        };

        const dialogRef = this.dialog.open(SutInputDataDialogComponent, dialogConfig);
        dialogRef.afterClosed().subscribe(data => {
            this.testTransferObj.sut = null;
            this.testTransferObj.inputData = null;
            // If data is null print some error message
            if (data) {

                if (data.sut || data.inputData) {
                    if (data.sut) {
                        this.testTransferObj.sut = data.sut;
                    }

                    if (data.inputData) {
                        this.testTransferObj.inputData = data.inputData;
                    }
                    this.runTest();
                }
            }
        });

    }

    public deleteTest(selectedTest: TestWebDTO) {
        this.loading = true;

        const apiUrl = environment.apiTestDeleteById.replace('{testId}', selectedTest.test.testId);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.test.delete');
                this.loading = false;
                this.isTestChanged = true;
                this.loadTests(this.deviceId, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public cloneTest() {
        this.loading = true;
        const apiUrl = environment.apiTestClone.replace('{testId}', this.selectedTest.test.testId);
        this.httpService.get(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.test.clone');
                this.loading = false;
                this.isTestChanged = true;
                this.loadTests(this.deviceId, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public downloadServices() {
        this.loading = true;
        const apiUrl = environment.apiDownloadServices.replace('{deviceId}', this.deviceId);
        this.httpService.getFile(apiUrl)
            .subscribe(
                data => {
                    importedSaveAs(data, 'services.json');
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    public openDialogShowInputData() {

        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '750px';
        dialogConfig.data = {
            inputDataList: this.selectedTest.inputData,
            selectedTest: this.selectedTest.test,
        };

        const dialogRef = this.dialog.open(InputDataDialogComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            this.loadTests(this.deviceId, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
        });
    }
}
