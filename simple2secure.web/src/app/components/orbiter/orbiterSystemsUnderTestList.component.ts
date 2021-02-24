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
import { MatTableDataSource, MatDialogConfig, MatDialog, MatSort, MatPaginator, PageEvent } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { SystemUnderTest } from '../../_models/systemUnderTest';
import { AlertService } from '../../_services/alert.service';
import { HttpService } from '../../_services/http.service';
import { SUTDetailsComponent } from './sutDetails.component';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { environment } from '../../../environments/environment';
import { HttpParams } from '@angular/common/http';
import { saveAs as importedSaveAs } from 'file-saver';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { fromEvent } from 'rxjs';
import { DeviceType } from '../../_models/deviceType';
import { ImportSutDialogComponent } from './importSutDialog.component';

@Component({
    moduleId: module.id,
    styleUrls: ['orbiter.css'],
    templateUrl: 'orbiterSystemsUnderTestList.component.html'
})

export class OrbiterSystemsUnderTestListComponent {

    displayedColumnsMonitored = ['name', 'device', 'ipAdress', 'deviceStatus'];
    displayedColumnsTargeted = ['name', 'Protocol', 'action'];
    selectedSUT: SystemUnderTest;
    loading = false;
    currentTab = 0;
    public pageSize = 10;
    public totalSize = 0;
    public totalSizeSut = 0;
    public pageEvent: PageEvent;
    dataSourceMonitored = new MatTableDataSource();
    dataSourceSut = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filterValue') filterValue: ElementRef;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private dialog: MatDialog,
        private translate: TranslateService
    ) { }

    ngOnInit() {
        this.paginator.pageSize = 10;
        this.currentTab = -1;
        this.loadSUTs(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value, this.currentTab);
    }

    onTabClick(event) {
        this.currentTab = event.tab.origin;
        this.loadSUTs(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value, this.currentTab);
    }

    ngAfterViewInit() {
        // This is currently only for local sorting
        this.dataSourceSut.sort = this.sort;
        this.dataSourceMonitored.sort = this.sort;

        fromEvent(this.filterValue.nativeElement, 'keyup')
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                tap(() => {
                    this.paginator.pageIndex = 0;
                    this.loadSUTs(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value, this.currentTab);
                })
            ).subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadSUTs(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value, this.currentTab))
            )
            .subscribe();
    }

    openDialogShowSuT(action: string): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '750px';
        if (action == 'new') {
            dialogConfig.data = {
                action: action,
            };
        } else if (action == 'edit') {
            dialogConfig.data = {
                action: action,
                sut: this.selectedSUT
            };
        }

        const dialogRef = this.dialog.open(SUTDetailsComponent, dialogConfig);
        dialogRef.afterClosed().subscribe(data => {
            this.loadSUTs(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value, this.currentTab);
        });
    }

    openDeleteSutDialog() {
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
                this.deleteSUT(this.selectedSUT);
            }
            this.loadSUTs(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value, this.currentTab);
        });
    }

    public downloadSuts() {
        this.loading = true;
        this.httpService.getFile(environment.apiDownloadSuts)
            .subscribe(
                data => {
                    importedSaveAs(data, 'suts.json');
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    public openDialogImportSuts() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '750px';

        const dialogRef = this.dialog.open(ImportSutDialogComponent, dialogConfig);
        dialogRef.afterClosed().subscribe(data => {
            this.loadSUTs(this.paginator.pageIndex, this.paginator.pageSize, this.filterValue.nativeElement.value, this.currentTab);
        });
    }

    public loadSUTs(page: number, size: number, filter: string, type: number) {
        this.loading = true;
        let apiUrl = '';


        if (this.currentTab === -1) {
            apiUrl = environment.apiSutPagination;
        } else {
            apiUrl = environment.apiDevicesWithTestsByTypePagination.replace('{deviceType}', DeviceType.PROBE);
        }

        const params = new HttpParams()
            .set('page', String(page))
            .set('size', String(size))
            .set('filter', filter);

        this.httpService.getWithParams(apiUrl, params)
            .subscribe(
                data => {
                    if (data.sutList) {
                        this.dataSourceSut = data.sutList;
                        this.alertService.showSuccessMessage(data.sutList, 'message.data', false, true);
                    } else if (data.devices) {
                        this.dataSourceMonitored = data.devices;
                        this.alertService.showSuccessMessage(data.devices, 'message.data', false, true);
                    }
                    this.totalSizeSut = data.totalSize;
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    public onMenuTriggerClick(sut: SystemUnderTest) {
        this.selectedSUT = sut;
    }

    public deleteSUT(sut) {
        this.loading = true;

        const apiUrl = environment.apiSutById.replace('{sutId}', sut.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.sut.delete');
                this.loading = false;
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }
}
