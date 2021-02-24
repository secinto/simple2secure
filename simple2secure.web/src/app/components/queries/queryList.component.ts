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

import { Component, ViewChild, ViewChildren, QueryList } from '@angular/core';
import { environment } from '../../../environments/environment';
import { OsQueryDTO } from '../../_models/DTO/osQueryDTO';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { OsQueryCategory } from '../../_models/osQueryCategory';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { QueryEditDialogComponent } from './queryEditDialog.component';
import { QueryCategoryAddDialog } from './queryCategoryAddDialog.component';
import { OsQuery } from '../../_models/osQuery';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';

@Component({
    moduleId: module.id,
    templateUrl: 'queryList.component.html',
    styleUrls: ['query.css']
})

export class QueryListComponent {

    queries: OsQueryDTO[];
    deleted = false;
    added = false;
    loading = false;
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChildren('paginator') paginatorList: QueryList<MatPaginator>;

    displayedColumns = ['name', 'query', 'action'];

    constructor(
        private httpService: HttpService,
        private alertService: AlertService,
        private translate: TranslateService,
        private dialog: MatDialog) {
    }

    ngOnInit() {
        this.getQueries();
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
        this.dataSource.sortingDataAccessor = (item, property) => {
            if (property === 'interval') {
                return (item['analysisIntervalUnit'] + ' ' + item['analysisInterval']);
            } else {
                return item[property];
            }
        };
    }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim();
        filterValue = filterValue.toLowerCase();
        this.dataSource.filter = filterValue;
    }

    public getQueries() {
        this.loading = true;
        this.httpService.get(environment.apiQueriesAllDto)
            .subscribe(
                data => {
                    this.queries = data;
                    if (this.deleted == false && this.added == false) {
                        this.alertService.showSuccessMessage(data, 'message.data', false, true);
                    } else {
                        this.deleted = false;
                        this.added = false;
                    }
                },
                error => {
                    this.alertService.showErrorMessage(error);
                });
        this.loading = false;
    }

    onEditClick(element: OsQuery) {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            queryRun: element,
        };

        const dialogRef = this.dialog.open(QueryEditDialogComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.osquery.update');
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    onDeleteClick(element: OsQuery) {
        this.openDialog(element);
    }

    public openDialog(item: OsQuery) {

        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;

        dialogConfig.data = {
            id: 1,
            title: this.translate.instant('message.areyousure'),
            content: this.translate.instant('message.config.dialog')
        };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true) {
                this.deleteConfig(item);
            }
        });
    }

    deleteConfig(queryConfig: OsQuery) {
        this.loading = true;
        const apiUrl = environment.apiQueriesById.replace('{queryId}', queryConfig.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.osquery.delete');
                this.deleted = true;
                this.getQueries();
                this.loading = false;
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
        this.loading = false;
    }

    onAddClick(category: OsQueryCategory) {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            queryRun: null,
            queryCategory: category
        };

        const dialogRef = this.dialog.open(QueryEditDialogComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.osquery.add');
                this.added = true;
                this.getQueries();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    onCategoryAddClick() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            category: null,
        };

        const dialogRef = this.dialog.open(QueryCategoryAddDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'query.category.add.success');
                this.added = true;
                this.getQueries();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    setDataSource(indexNumber) {
        this.dataSource.paginator = this.paginatorList.toArray()[indexNumber];
        this.dataSource.data = this.queries[indexNumber].queries;
    }
}
