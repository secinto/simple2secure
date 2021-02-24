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

import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { OsQueryReportDetailsComponent } from './osqueryReportDetails.component';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { fromEvent } from 'rxjs';
import { ReportType } from '../../_models/reportType';
import { ReportComponent } from './report.component';
import { DeviceType } from '../../_models/deviceType';

@Component({
    moduleId: module.id,
    styleUrls: ['report.css'],
    templateUrl: 'osqueryReportOverview.component.html'
})

export class OsQueryReportOverviewComponent extends ReportComponent {
    displayedColumns = ['select', 'probe', 'hostname', 'query', 'timestamp'];

    constructor(
        route: ActivatedRoute,
        router: Router,
        httpService: HttpService,
        alertService: AlertService,
        dataService: DataService,
        dialog: MatDialog,
        translate: TranslateService,
        cdr: ChangeDetectorRef) {
        super(route, router, httpService, alertService, dataService, dialog, translate, cdr);
    }

    ngOnInit() {
        this.paginator.pageSize = 10;
        this.getGroups(ReportType.OSQUERY, DeviceType.PROBE);
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
                    this.loadReports(ReportType.OSQUERY);
                })
            )
            .subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadReports(ReportType.OSQUERY))
            )
            .subscribe();
    }

    openDialogShowReportDetails(report: any): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            report: report,
        };

        this.dialog.open(OsQueryReportDetailsComponent, dialogConfig);
    }
}
