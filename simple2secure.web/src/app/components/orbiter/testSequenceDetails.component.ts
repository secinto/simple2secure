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

import { Component, Inject, ViewChild } from '@angular/core';
import {
    MAT_DIALOG_DATA,
    MatDialog,
    MatDialogRef,
    MatPaginator,
    MatSort,
    MatTableDataSource,
    PageEvent
} from '@angular/material';
import { environment } from '../../../environments/environment';
import { PodDTO } from '../../_models/DTO/podDTO';
import { TestSequence } from '../../_models/testSequence';
import { HttpParams } from '@angular/common/http';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { TestWebDTO } from '../../_models/DTO/TestWebDTO';
import { SystemUnderTest } from '../../_models/systemUnderTest';
import { TestSUTDataInput } from '../../_models/DTO/testSUTDataInput';
import { TestInputData } from '../../_models/testInputData';
import { OrbiterComponent } from './orbiter.component';

@Component({
    moduleId: module.id,
    templateUrl: 'testSequenceDetails.component.html',
    styleUrls: ['orbiter.css'],
})

export class TestSequenceDetailsComponent extends OrbiterComponent {
    loading = false;
    sequence = new TestSequence();
    sequenceToShow: TestWebDTO[] = [];

    type: string;
    isTestChanged = false;
    isNewTest = false;
    url: string;
    id: string;
    pod: PodDTO;
    public pageEvent: PageEvent;
    public pageSize = 4;
    public currentPage = 0;
    public totalSize = 0;
    dataSource = new MatTableDataSource();
    displayedColumns = ['test', 'action'];
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor(
        private alertService: AlertService,
        private dialogRef: MatDialogRef<TestSequenceDetailsComponent>,
        dialog: MatDialog,
        private httpService: HttpService,
        @Inject(MAT_DIALOG_DATA) data) {

        super(dialog);

        this.type = data.type;
        this.id = data.deviceId;
        if (this.type == 'new') {
            this.isNewTest = true;
        } else if (this.type == 'edit') {
            this.isNewTest = false;
            this.sequence = data.sequence.sequence;
            this.sequenceToShow = data.sequence.tests;
        }
    }

    ngOnInit() {
        this.loadTests(this.id, 0, 5);
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }

    public handlePage(e?: PageEvent) {
        this.currentPage = e.pageIndex;
        this.pageSize = e.pageSize;
        this.loadTests(this.id, e.pageIndex, e.pageSize);
        return e;
    }

    addTestToSequence(item: TestWebDTO) {
        const sutInputData = new TestSUTDataInput();
        sutInputData.test = item.test;

        if (this.sequence.tests) {
            this.sequence.tests.push(sutInputData);
            this.sequenceToShow.push(item);
        } else {
            this.sequence.tests = [];
            this.sequence.tests.push(sutInputData);
            this.sequenceToShow.push(item);
        }
    }

    removeTestFromSequence(index) {
        if (index > -1) {
            this.sequence.tests.splice(index, 1);
            this.sequenceToShow.splice(index, 1);
        }
    }

    getTestById(id: string) {

        for (let i = 0; i < this.sequence.tests.length; i++) {

            if (this.sequence.tests[i].test.testId == id) {
                return this.sequence.tests[i];
            }
        }

        return null;
    }

    /**
     * Adds Sut to the TestSequence object
     * @param value
     * @param testId
     */
    onSutChange(value: SystemUnderTest, testId: string) {

        const testSutDataInput = this.getTestById(testId);

        testSutDataInput.sut = value;
    }

    compareObjects(o1: any, o2: any): boolean {
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.id === o2.id;
    }

    /**
     * Adds Data Input to the TestSequence object
     * @param value
     * @param testId
     */
    onDataInputChange(value: TestInputData, testId: string) {

        const testSutDataInput = this.getTestById(testId);

        testSutDataInput.inputData = value;
    }

    checkInputData(test: TestWebDTO) {
        const flag = this.parameterArrayToString(test);
        const regexp = new RegExp('\{USE_INPUT_DATA\}');
        return regexp.test(flag);
    }

    checkSut(test: TestWebDTO) {
        const flag = this.parameterArrayToString(test);
        const regexp = new RegExp('USE_SUT_METADATA\{.*\}');
        return regexp.test(flag);
    }

    getMetadata(sut: SystemUnderTest) {
        return JSON.stringify(sut.metadata);
    }

    public loadTests(podId: string, page: number, size: number) {
        this.loading = true;
        const params = new HttpParams()
            .set('usePagination', String(true))
            .set('page', String(page))
            .set('size', String(size))
            .set('filter', '');

        const apiUrl = environment.apiTestByDeviceIdPagination.replace('{deviceId}', podId);

        this.httpService.getWithParams(apiUrl, params)
            .subscribe(
                data => {
                    this.dataSource.data = data.tests;
                    this.totalSize = data.totalSize;
                    if (!this.isTestChanged) {
                        this.alertService.showSuccessMessage(data, 'message.data', false, true);
                    }
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }


    public updateSaveSequence() {
        this.loading = true;

        if (!this.sequence.podId) {
            this.sequence.podId = this.id;
        }

        this.httpService.post(this.sequence, environment.apiSequence).subscribe(
            data => {
                if (this.type === 'new') {
                    this.alertService.showSuccessMessage(data, 'message.sequence.create');
                } else {
                    this.alertService.showSuccessMessage(data, 'message.sequence.update');
                }
                this.close(true);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            }
        );
    }

    public close(value: boolean) {
        this.dialogRef.close(value);
    }

}
