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

import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AlertService, HttpService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Ng4LoadingSpinnerService} from 'ng4-loading-spinner';
import {SearchResult} from '../_models/searchResult';
import {MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {TestResultDetailsComponent} from '../report/testResultDetails.component';
import {NetworkReportDetailsComponent, OsQueryReportDetailsComponent} from '../report';
import {NotificationDetailsComponent} from '../notification/notificationDetails.component';


@Component({
    moduleId: module.id,
    styleUrls: ['searchResult.component.css'],
    templateUrl: 'searchResult.component.html',
    selector: 'searchResult'
})

export class SearchResultComponent implements OnInit{

    searchString = '';
    public loading = true;
    searchResult: SearchResult[] = [];
    displayedColumnsNotifications = ['content', 'timestamp'];
    displayedColumnsTR = ['result', 'timestamp'];
    displayedColumnsOSQuery = ['result', 'timestamp'];
    displayedColumnsNetworkReport = ['result', 'timestamp'];
    dataSourceNotifications = new MatTableDataSource();
    dataSourceTR = new MatTableDataSource();
    dataSourceOSQuery = new MatTableDataSource();
    dataSourceNetworkReport = new MatTableDataSource();
    @ViewChild(MatSort) sortNotification: MatSort;
    @ViewChild(MatPaginator) paginatorNotification: MatPaginator;
    @ViewChild(MatSort) sortTR: MatSort;
    @ViewChild(MatPaginator) paginatorTR: MatPaginator;
    @ViewChild(MatSort) sortOSQuery: MatSort;
    @ViewChild(MatPaginator) paginatorOSQuery: MatPaginator;
    @ViewChild(MatSort) sortNetworkReport: MatSort;
    @ViewChild(MatPaginator) paginatorNetworkReport: MatPaginator;


    constructor(private router: Router,
                private route: ActivatedRoute,
                private httpService: HttpService,
                private alertService: AlertService,
                private translate: TranslateService,
                private dialog: MatDialog,
                private spinnerService: Ng4LoadingSpinnerService){
    }

    ngOnInit() {
        this.loading = true;
        this.spinnerService.show();
        this.searchString = this.route.snapshot.paramMap.get('searchquery');
        this.getSearchResults();
    }

    ngAfterViewInit() {
        this.dataSourceNotifications.sort = this.sortNotification;
        this.dataSourceNotifications.paginator = this.paginatorNotification;
        this.dataSourceTR.sort = this.sortTR;
        this.dataSourceTR.paginator = this.paginatorTR;
        this.dataSourceOSQuery.sort = this.sortOSQuery;
        this.dataSourceOSQuery.paginator = this.paginatorOSQuery;
        this.dataSourceNetworkReport.sort = this.sortNetworkReport;
        this.dataSourceNetworkReport.paginator = this.paginatorNetworkReport;
    }

    applyFilter(filterValue: string, index: number) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        if (index == 0){
            this.dataSourceNotifications.filter = filterValue;
        }
        else if (index == 1){
            this.dataSourceOSQuery.filter = filterValue;
        }
        else if (index == 2){
            this.dataSourceNetworkReport.filter = filterValue;
        }
        else if (index == 3){
            this.dataSourceTR.filter = filterValue;
        }

    }


    getSearchResults() {
        // Send request only if the search string is not null
        if (this.searchString){
            this.httpService.get(environment.apiEndpoint + 'search/' + this.searchString)
            .subscribe(
                data => {
                    this.searchResult = data;
                    if (this.searchResult){
                        if (this.searchResult[0]){
                            if (this.searchResult[0].object){
                                this.dataSourceNotifications.data = this.searchResult[0].object;
                            }
                        }

                        if (this.searchResult[1]){
                            if (this.searchResult[1].object){
                                this.dataSourceOSQuery.data = this.searchResult[1].object;
                            }
                        }

                        if (this.searchResult[2]){
                            if (this.searchResult[2].object){
                                this.dataSourceNetworkReport.data = this.searchResult[2].object;
                            }
                        }

                        if (this.searchResult[3]){
                            if (this.searchResult[3].object){
                                this.dataSourceTR.data = this.searchResult[3].object;
                            }
                        }

                    }
                },
                error => {
                    if (error.status == 0) {
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else {
                        this.alertService.error(error.error.errorMessage);
                    }
                });
            this.spinnerService.hide();
            this.loading = false;
        }
    }

    getSearchResultObjectLenght(searchResult: SearchResult){
        if (searchResult){
            if (searchResult.object)
            return searchResult.object.length;
        }
        else{
            return 0;
        }
    }

    isPanelDisabled(searchResult: SearchResult){
        if (searchResult){
            if (searchResult.object){
                if (searchResult.object.length > 0){
                    return false;
                }
            }
        }
        return true;
    }

    _setDataSource(index) {
        setTimeout(() => {
            switch (index) {
                case 0:
                    !this.dataSourceNotifications.paginator ? this.dataSourceNotifications.paginator = this.paginatorNotification : null;
                    !this.dataSourceNotifications.sort ? this.dataSourceNotifications.sort = this.sortNotification : null;
                    break;

                case 1:
                    !this.dataSourceOSQuery.paginator ? this.dataSourceOSQuery.paginator = this.paginatorOSQuery : null;
                    !this.dataSourceOSQuery.sort ? this.dataSourceOSQuery.sort = this.sortOSQuery : null;
                    break;

                case 2:
                    !this.dataSourceNetworkReport.paginator ? this.dataSourceNetworkReport.paginator = this.paginatorNetworkReport : null;
                    !this.dataSourceNetworkReport.sort ? this.dataSourceNetworkReport.sort = this.sortNetworkReport : null;
                    break;

                case 3:
                    !this.dataSourceTR.paginator ? this.dataSourceTR.paginator = this.paginatorTR : null;
                    !this.dataSourceTR.sort ? this.dataSourceTR.sort = this.sortTR : null;
                    break;
            }
        });
    }

    openDialogShowDetails(report: any, type: string): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        if (type == 'notification'){
            dialogConfig.data = {
                notification: report,
            };
            this.dialog.open(NotificationDetailsComponent, dialogConfig);
        }
        else if (type == 'networkReport'){
            dialogConfig.data = {
                report: report,
            };
            this.dialog.open(NetworkReportDetailsComponent, dialogConfig);
        }
        else if (type == 'osqueryReport'){
            dialogConfig.data = {
                report: report,
            };
            this.dialog.open(OsQueryReportDetailsComponent, dialogConfig);
        }
        else if (type == 'testresult'){
            dialogConfig.data = {
                result: report,
            };
            this.dialog.open(TestResultDetailsComponent, dialogConfig);
        }

    }
}
