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
import { ActivatedRoute } from '@angular/router';
import { MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { environment } from '../../../environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { TestSequence } from '../../_models/testSequence';
import { TestSequenceDetailsComponent } from './testSequenceDetails.component';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { HttpParams } from '@angular/common/http';
import { TestSequenceDTO } from '../../_models/DTO/testSequenceDTO';
import { fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';

@Component({
    moduleId: module.id,
    templateUrl: 'orbiterToolTestSequenceList.component.html'
})

export class OrbiterToolTestSequenceListComponent {

    selectedSequence: TestSequenceDTO = new TestSequenceDTO();
    podId: string;
    isSequenceChanged: boolean;
    displayedColumns = ['testId', 'status', 'action'];
    loading = false;
    url: string;
    id: string;
    public pageSize = 10;
    public totalSize = 0;
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filterValue') filterValue: ElementRef;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private dialog: MatDialog,
        private translate: TranslateService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.isSequenceChanged = false;
        this.id = this.route.snapshot.paramMap.get('id');
        this.paginator.pageSize = 10;
        this.loadSequences(this.id, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);

    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;

        fromEvent(this.filterValue.nativeElement, 'keyup')
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                tap(() => {
                    this.paginator.pageIndex = 0;
                    this.loadSequences(this.id, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
                })
            ).subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadSequences(this.id, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value))
            )
            .subscribe();
    }

    public onMenuTriggerClick(sequence: TestSequenceDTO) {
        this.selectedSequence = sequence;

    }

    public loadSequences(podId: string, page: number, size: number, filter: string) {
        this.loading = true;

        const params = new HttpParams()
            .set('filter', filter)
            .set('page', String(page))
            .set('size', String(size));

        const apiUrl = environment.apiSequenceByDeviceIdPagination.replace('{deviceId}', podId);

        this.httpService.getWithParams(apiUrl, params)
            .subscribe(
                data => {
                    this.dataSource.data = data.sequences;
                    this.totalSize = data.totalSize;
                    if (!this.isSequenceChanged) {
                        this.alertService.showSuccessMessage(data, 'message.data', false, true);
                    }

                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    openDialogShowTestSequence(type: string): void {

        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '750px';
        dialogConfig.data = {
            sequence: this.selectedSequence,
            type: type,
            deviceId: this.id
        };

        const dialogRef = this.dialog.open(TestSequenceDetailsComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true) {
                this.isSequenceChanged = true;
            }
            this.loadSequences(this.id, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
        });

    }

    public runSequence() {

        this.loading = true;
        this.httpService.post(this.selectedSequence.sequence, environment.apiSequenceSchedule).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.testsequence.schedule');
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
                this.deleteSequence(this.selectedSequence.sequence);
            }
        });
    }

    public deleteSequence(selectedSequence: TestSequence) {
        this.loading = true;
        const apiUrl = environment.apiSequenceBySequenceId.replace('{sequenceId}', selectedSequence.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.sequence.delete');
                this.loading = false;
                this.isSequenceChanged = true;
                this.loadSequences(this.id, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public cloneSequence() {
        this.loading = true;
        const apiUrl = environment.apiSequenceClone.replace('{sequenceId}', this.selectedSequence.sequence.id);
        this.httpService.get(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.sequence.clone');
                this.loading = false;
                this.isSequenceChanged = true;
                this.loadSequences(this.id, this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }
}
