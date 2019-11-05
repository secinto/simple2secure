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
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {Timeunit, Test} from '../_models';
import {TestObjWeb} from '../_models/testObjWeb';
import {AlertService, DataService, HttpService} from '../_services/index';
import { PodDTO } from '../_models/DTO/podDTO';
import { TestSequence } from '../_models/testSequence';
import {CdkDragDrop, moveItemInArray, transferArrayItem, CdkDropList, DragRef} from '@angular/cdk/drag-drop';
import { CarouselComponent } from 'ngx-carousel-lib';
import { some } from 'highcharts/highcharts.src';


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
    pod: PodDTO;
    testNamesCurrent: string[] = [];
    @ViewChild('carousel') topCarousel: CarouselComponent;

    constructor(
        private dataService: DataService,
        private alertService: AlertService,
        private dialogRef: MatDialogRef<TestSequenceDetailsComponent>,
        private dialog: MatDialog,
        private httpService: HttpService,
        private translate: TranslateService,
        @Inject(MAT_DIALOG_DATA) data) {

        this.type = data.type;
        if (this.type == 'new') {
            this.isNewTest = true;
        }
        else if (this.type == 'edit') {
            this.isNewTest = false;
            this.sequence = data.sequence;
            this.sequence.sequenceContent = this.sequence.sequenceContent;
        } else {

        }
    }

    ngOnInit() {
        this.pod = this.dataService.getPods();
        this.tests = this.pod.test;
        this.testNamesCurrent = this.getTestNamesFromObject(this.tests);
        this.topCarousel.perspective = 800;
        this.loadTests(this.pod.pod.deviceId);
    }

    ngAfterViewInit(){
        this.getTestForSequenceToShow(this.sequence);
    }

    ngDoCheck() {
        this.topCarousel.update();
    }


    getTestNamesFromObject(tests: TestObjWeb[]) {
        const testNames: string[] = [];
        for (const testObj of tests) {
            testNames.push(testObj.name);
        }
        return testNames;
    }

    getTestForSequenceToShow(sequence: TestSequence) {
        const someContent = sequence.sequenceContent;
        // console.log(this.tests);
        if (Array.isArray(someContent) && someContent.length > 0) {
            for (let i = 0; i < someContent.length; i++) {
                for (const test of this.tests) {
                    if (test.name === someContent[i]) {
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

    public loadTests(podId: string) {
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'test/' + podId)
            .subscribe(
                data => {
                    this.tests = data;
                    if (!this.isTestChanged) {
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


    public updateSaveSequence() {
        this.loading = true;

        if (!this.sequence.podId) {
            this.sequence.podId = this.pod.pod.deviceId;
        }

        if (!(this.sequence.sequenceContent.length == 0)) {
            this.sequence.sequenceContent = this.sequence.sequenceContent;
        }

        this.url = environment.apiEndpoint + 'sequence/add';
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
