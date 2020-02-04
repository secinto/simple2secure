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

import {Component, Inject, ElementRef, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog, PageEvent} from '@angular/material';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {TestObjWeb} from '../_models/testObjWeb';
import {AlertService, DataService, HttpService} from '../_services/index';
import { PodDTO } from '../_models/DTO/podDTO';
import { TestSequence } from '../_models/testSequence';
import { CarouselComponent } from 'ngx-carousel-lib';
import {ActivatedRoute} from '@angular/router';
import {HttpParams} from "@angular/common/http";


@Component({
	moduleId: module.id,
	templateUrl: 'testSequenceDetails.component.html',
	styleUrls: ['orbiter.css'],
})

export class TestSequenceDetailsComponent {
    loading = false;
    sequence = new TestSequence();
    sequenceToShow: TestObjWeb[] = [];
    tests: TestObjWeb[] = [];
    type: string;
    isTestChanged = false;
    isNewTest = false;
    url: string;
    id: string;
    pod: PodDTO;
    public pageEvent: PageEvent;
    public pageSize = 5;
    public currentPage = 0;
    public totalSize = 0;
    testNamesCurrent: string[] = [];
    dataSource = new MatTableDataSource();
    displayedColumns = ['test', 'action'];
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('carousel') topCarousel: CarouselComponent;

    constructor(
        private dataService: DataService,
        private alertService: AlertService,
        private dialogRef: MatDialogRef<TestSequenceDetailsComponent>,
        private dialog: MatDialog,
        private httpService: HttpService,
        private translate: TranslateService,
        private route: ActivatedRoute,
        @Inject(MAT_DIALOG_DATA) data) {

        this.type = data.type;
        this.id = data.deviceId;
        if (this.type == 'new') {
            this.isNewTest = true;
        }
        else if (this.type == 'edit') {
            this.isNewTest = false;
            this.sequence = data.sequence;
        }
    }

    ngOnInit() {
        this.loadTests(this.id, 0, 5);
        this.topCarousel.perspective = 800;
    }

    ngDoCheck() {
        this.topCarousel.update();
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


    getTestNamesFromObject(tests: TestObjWeb[]) {
        const testNames: string[] = [];
        for (const testObj of tests) {
            testNames.push(testObj.name);
        }
        return testNames;
    }

    getTestForSequenceToShow(sequence: TestSequence) {
        const origSeqContent = sequence.sequenceContent;
        if (Array.isArray(origSeqContent) && origSeqContent.length > 0 && origSeqContent.length != this.sequenceToShow.length) {
            for (let i = 0; i < origSeqContent.length; i++) {
                for (const test of this.tests) {
                    if (test.name === origSeqContent[i]) {
                        this.sequenceToShow.push(test);
                    }
                }
            }
        }
    }

    prev() {
        this.topCarousel.slidePrev();
        this.topCarousel.update();
    }
    next() {
        this.topCarousel.slideNext();
        this.topCarousel.update();
    }

    addTestToSequence(item: TestObjWeb){
        if (this.sequence.sequenceContent){
            this.sequence.sequenceContent.push(item.name);
            this.sequenceToShow.push(item);
            this.topCarousel.update();
            this.topCarousel.slideNext();
        }else {
            this.sequence.sequenceContent = [];
            this.sequence.sequenceContent.push(item.name);
            this.sequenceToShow.push(item);
            this.topCarousel.update();
        }
    }

    removeTestFromSequence(item: TestObjWeb){
        const index = this.sequence.sequenceContent.indexOf(item.name, 0);
        const showIndex = this.sequenceToShow.indexOf(item, 0);
        if (showIndex > -1) {
            this.sequence.sequenceContent.splice(showIndex, 1);
            this.sequenceToShow.splice(showIndex, 1);
            this.topCarousel.slidePrev();
            this.topCarousel.update();
        }
    }

    public loadTests(podId: string, page: number, size: number){
        this.loading = true;
        const params = new HttpParams()
			.set('usePagination', String(true));
		this.httpService.getWithParams(environment.apiEndpoint + 'test/' + podId + '/' + page + '/' + size, params)
            .subscribe(
                data => {
                    this.tests = data.tests;
                    this.dataSource.data = this.tests;
                    this.totalSize = data.totalSize;
                    this.testNamesCurrent = this.getTestNamesFromObject(this.tests);
                    this.getTestForSequenceToShow(this.sequence);
                    if (!this.isTestChanged) {
                        if (data.tests.length > 0) {
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


    public updateSaveSequence() {
        this.loading = true;

        if (!this.sequence.podId) {
            this.sequence.podId = this.id;
        }

        this.url = environment.apiEndpoint + 'sequence';
        this.httpService.post(this.sequence, this.url).subscribe(
            data => {
                if (this.type === 'new') {
                    this.alertService.success(this.translate.instant('message.sequence.create'));
                }
                else {
                    this.alertService.success(this.translate.instant('message.sequence.update'));
                }
                this.close(true);
            },
            error => {
                if (error.status == 0) {
                    this.alertService.error(this.translate.instant('server.notresponding'));
                }
                else {
                    this.alertService.error(error.error.errorMessage);
                }
                this.loading = false;
            }
            );
    }

    public close(value: boolean) {
        this.dialogRef.close(value);
    }

}
