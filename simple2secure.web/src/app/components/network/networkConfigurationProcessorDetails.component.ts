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

import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { NetworkProcessorConfigurationEditComponent } from './networkProcessorConfigurationEdit.component';
import { HttpErrorResponse } from '@angular/common/http';
import { Processor } from '../../_models/processor';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';

@Component({
    moduleId: module.id,
    selector: 'networkConfigurationProcessor',
    templateUrl: 'networkConfigurationProcessorDetails.component.html'
})

export class NetworkConfigurationProcessorDetailsComponent {
    processors: Processor[];
    private sub: any;
    loading = false;
    type: number;
    groupId: string;
    deleted = false;
    added = false;
    selectedItem: Processor;
    displayedColumns = ['name', 'class', 'interval', 'packet', 'action'];
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    groupEditable: boolean;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private dialog: MatDialog,
        private route: ActivatedRoute,
        private dataService: DataService,
        private translate: TranslateService
    ) {
    }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.groupId = params['id'];
        });

        this.groupEditable = this.dataService.isGroupEditable();

        if (!this.groupEditable) {
            this.displayedColumns = ['name', 'class', 'interval', 'packet'];
        }

        this.loadProcessors();
    }

    loadProcessors() {
        this.loading = true;
        this.httpService.get(environment.apiProcessors)
            .subscribe(
                data => {
                    this.processors = data;
                    this.dataSource.data = this.processors;
                    if (this.deleted == false && this.added == false) {
                        this.alertService.showSuccessMessage(data, 'message.data', false, true);
                    } else {
                        this.deleted = false;
                        this.added = false;
                    }
                    this.loading = false;

                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
    }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.dataSource.filter = filterValue;
    }

    onMenuTriggerClick(item: any) {
        this.selectedItem = item;
    }

    onEditClick() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            processor: this.selectedItem,
            groupId: this.groupId
        };

        const dialogRef = this.dialog.open(NetworkProcessorConfigurationEditComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.processor.update');
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    onAddClick() {

        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            processor: null,
            groupId: this.groupId
        };
        const dialogRef = this.dialog.open(NetworkProcessorConfigurationEditComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.processor.add');
                this.added = true;
                this.loadProcessors();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    onDeleteClick() {
        this.openDialog(this.selectedItem);
    }

    public openDialog(item: any) {

        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;

        dialogConfig.data = {
            id: 1,
            title: this.translate.instant('message.areyousure'),
            content: this.translate.instant('message.processor.dialog')
        };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true) {
                this.deleteProcessor(item);
            }
        });
    }

    deleteProcessor(processor: any) {
        this.loading = true;

        const apiUrl = environment.apiProcessorsById.replace('{processorId}', processor.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.processor.delete');
                this.deleted = true;
                this.loadProcessors();
                this.loading = false;
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }
}
