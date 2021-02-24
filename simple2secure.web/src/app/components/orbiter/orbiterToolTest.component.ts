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
import { MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog } from '@angular/material';
import { Device } from '../../_models/device';
import { AlertService } from '../../_services/alert.service';
import { HttpService } from '../../_services/http.service';
import { environment } from '../../../environments/environment';
import { DeviceType } from '../../_models/deviceType';
import { HttpParams } from '@angular/common/http';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { fromEvent } from 'rxjs';
import { MatTabChangeEvent } from '@angular/material/tabs';

@Component({
    moduleId: module.id,
    styleUrls: ['orbiter.css'],
    templateUrl: 'orbiterToolTest.component.html'
})

export class OrbiterToolTestComponent {

    selectedPod: Device;
    displayedColumns: string[] = ['podId', 'hostname', 'group', 'status', 'action'];
    loading = false;
    slide: boolean;
    url: string;
    tabIndex: number;
    dataSource = new MatTableDataSource();
    isPublic = false;
    public pageSize = 10;
    public totalSize = 0;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filterValue') filterValue: ElementRef;


    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private router: Router,
        private route: ActivatedRoute
    ) { }

    ngOnInit() {
        this.paginator.pageSize = 10;
        this.tabIndex = 0;
        this.loadPods();
    }

    onTabClick(tabChangeEvent: MatTabChangeEvent) {
        this.tabIndex = tabChangeEvent.index;
        this.filterValue.nativeElement.value = '';
        this.loadPods();
    }

    ngAfterViewInit() {
        // This is currently only for local sorting
        this.dataSource.sort = this.sort;
        this.initPaginatorAndFilter();

    }

    public initPaginatorAndFilter() {
        fromEvent(this.filterValue.nativeElement, 'keyup')
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                tap(() => {
                    this.paginator.pageIndex = 0;
                    this.loadPods();
                })
            ).subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadPods())
            )
            .subscribe();
    }

    public onMenuTriggerClick(pod: Device) {
        this.selectedPod = pod;
        this.slide = this.selectedPod.info.publiclyAvailable;
    }

    loadPods() {
        this.dataSource.data = [];
        this.loading = true;

        const params = new HttpParams()
            .set('isPublic', String(this.tabIndex == 1))
            .set('page', String(this.paginator.pageIndex))
            .set('size', String(this.paginator.pageSize))
            .set('filter', this.filterValue.nativeElement.value);

        let apiUrl = '';

        apiUrl = environment.apiDevicesWithTestsByTypePagination.replace('{deviceType}', DeviceType.POD);

        this.httpService.getWithParams(apiUrl, params)
            .subscribe(
                data => {
                    this.dataSource.data = data.devices;
                    this.totalSize = data.totalSize;
                    this.loading = false;
                    this.alertService.showSuccessMessage(data.devices, 'message.data', false, true);
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
        this.loading = false;
    }

    updatePodVisibility(slide: boolean) {
        this.loading = true;
        this.selectedPod.info.publiclyAvailable = slide;
        this.httpService.post(this.selectedPod, environment.apiDevicesUpdateVisiblity).subscribe(
            data => {
                if (data) {
                    this.selectedPod = data;
                    this.loading = false;
                    this.loadPods();
                }
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public showPodTests() {
        this.router.navigate([this.selectedPod.info.id], { relativeTo: this.route });
    }

    public showSequences() {
        this.router.navigate(['sequences/' + this.selectedPod.info.id], { relativeTo: this.route });
    }
}
