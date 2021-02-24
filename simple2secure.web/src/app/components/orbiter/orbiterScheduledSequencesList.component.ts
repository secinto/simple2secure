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
import { environment } from '../../../environments/environment';
import { TestStatus } from '../../_models/testStatus';
import { TestSequenceResultDetailsComponent } from '../report/testSequenceResultDetails.component';
import { TestSequenceRunDTO } from '../../_models/DTO/testSequenceRunDTO';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { HttpParams } from '@angular/common/http';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { fromEvent } from 'rxjs';
import { OrbiterComponent } from './orbiter.component';

@Component({
    moduleId: module.id,
    styleUrls: ['orbiter.css'],
    templateUrl: 'orbiterScheduledSequencesList.component.html'
})

export class OrbiterScheduledSequencesListComponent extends OrbiterComponent {

    selectedSequenceRun: TestSequenceRunDTO = new TestSequenceRunDTO();
    podId: string;
    isSequenceChanged: boolean;
    showSequenceResult = false;
    displayedColumns = ['podId', 'name', 'hostname', 'time', 'type', 'status', 'action'];
    loading = false;
    url: string;
    public pageSize = 10;
    public totalSize = 0;
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filterValue') filterValue: ElementRef;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private utils: HelperService,
        dialog: MatDialog
    ) {
        super(dialog);
    }

    ngOnInit() {
        this.loadScheduledSequences(0, 10, this.filterValue.nativeElement.value);
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
                    this.loadScheduledSequences(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
                })
            ).subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadScheduledSequences(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value))
            )
            .subscribe();

    }

    public onMenuTriggerClick(sequenceRun: TestSequenceRunDTO) {
        this.showSequenceResult = false;
        if (sequenceRun.sequenceRun.sequenceStatus == TestStatus.EXECUTED) {
            if (sequenceRun.sequenceRun.testSequenceContent != null) {
                this.showSequenceResult = true;
            }
        }
        this.selectedSequenceRun = sequenceRun;
    }

    public showSequenceContent(map: Map<string, any>) {
        this.openDialogShowTestContent(JSON.stringify(map));
    }

    public loadScheduledSequences(page: number, size: number, filter: string) {
        this.loading = true;

        const params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size))
            .set('filter', filter);

        this.httpService.getWithParams(environment.apiSequenceScheduledPagination, params)
            .subscribe(
                data => {
                    this.dataSource.data = data.sequences;
                    this.totalSize = data.totalSize;

                    if (!this.isSequenceChanged) {
                        this.alertService.showSuccessMessage(data, 'message.data', false, true);
                    }
                },
                error => {
                    this.alertService.showErrorMessage(error);
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
