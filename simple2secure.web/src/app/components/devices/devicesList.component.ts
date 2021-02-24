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

import { AfterContentChecked, ChangeDetectorRef, Component, ElementRef, ViewChild } from '@angular/core';
import { MatDialog, MatPaginator, MatSort, MatTableDataSource, PageEvent } from '@angular/material';
import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { saveAs as importedSaveAs } from 'file-saver';
import { CompanyGroup } from '../../_models/companygroup';
import { Device } from '../../_models/device';
import { AlertService } from '../../_services/alert.service';
import { HttpService } from '../../_services/http.service';
import { UserDeviceChangeGroupComponent } from '../user/user-device-change-group.component';
import { environment } from '../../../environments/environment';
import { fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';

@Component({
    moduleId: module.id,
    templateUrl: 'devicesList.component.html'
})

export class DevicesListComponent implements AfterContentChecked {

    selectedItem: any;
    groupAdded = false;
    loading = false;
    devices: Device[];
    groupsForSelect: CompanyGroup[];
    selectedGroup: CompanyGroup;
    dataSource = new MatTableDataSource();
    @ViewChild('paginator') paginator: MatPaginator;
    @ViewChild('sort') sort: MatSort;
    @ViewChild('filterValue') filterValue: ElementRef;
    displayedColumnsDevices = ['probeId', 'hostname', 'type', 'status', 'action'];
    public totalSize = 0;

    constructor(
        private dialog: MatDialog,
        private alertService: AlertService,
        private httpService: HttpService,
        private cdr: ChangeDetectorRef) {
    }

    ngOnInit() {
        this.paginator.pageSize = 10;
        this.loadGroups();
    }

    ngAfterContentChecked() {
        this.cdr.detectChanges();
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;

        fromEvent(this.filterValue.nativeElement, 'keyup')
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                tap(() => {
                    this.paginator.pageIndex = 0;
                    this.loadDevicesBySelectedGroup(this.selectedGroup);
                })
            )
            .subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadDevicesBySelectedGroup(this.selectedGroup))
            )
            .subscribe();
    }

    loadDevicesBySelectedGroup(group: CompanyGroup) {
        this.loading = true;
        const apiUrl = environment.apiDevicesByGroupIdAndTypePagination.replace('{groupId}', group.id);

        this.httpService.getWithParams(apiUrl, this.getParams())
            .subscribe(
                data => {
                    this.devices = data.devices;
                    this.dataSource.data = this.devices;
                    this.totalSize = data.totalSize;
                    this.alertService.showSuccessMessage(data, 'message.data', false, true);
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
        this.loading = false;

    }

    loadGroups() {
        this.loading = true;
        this.httpService.get(environment.apiGroupByContext)
            .subscribe(
                data => {
                    this.groupsForSelect = data;
                    this.selectedGroup = this.groupsForSelect[0];
                    this.loadDevicesBySelectedGroup(this.selectedGroup);
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
        this.loading = false;
    }

    openDialogChangeDeviceGroup(): void {
        const dialogRef = this.dialog.open(UserDeviceChangeGroupComponent, {
            width: '350px',
            data: this.selectedItem
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.group.add');
                this.groupAdded = true;
                this.loadDevicesBySelectedGroup(this.selectedGroup);
            }
            else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    public download() {
        this.loading = true;
        const apiUrl = environment.apiDownloadGroupId.replace('{groupId}', this.selectedGroup.id);
        this.httpService.getFile(apiUrl)
            .subscribe(
                data => {
                    importedSaveAs(data, 's2s_setup.zip');
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    public onMenuTriggerClick(element) {
        this.selectedItem = element;
    }

    public onSelectBoxChange(newGroup) {
        this.selectedGroup = newGroup;
        this.loadDevicesBySelectedGroup(newGroup);
    }

    private getParams() {
        const params = new HttpParams()
            .set('page', String(this.paginator.pageIndex))
            .set('size', String(this.paginator.pageSize))
            .set('filter', this.filterValue.nativeElement.value);

        return params;
    }

}
