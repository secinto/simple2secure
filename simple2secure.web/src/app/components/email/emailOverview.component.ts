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

import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { environment } from '../../../environments/environment';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { TranslateService } from '@ngx-translate/core';
import { EmailAccountAddComponent } from './emailAccountAdd.component';
import { EmailInboxComponent } from './emailInbox.component';
import { HttpService } from '../../_services/http.service';
import { EmailConfiguration } from '../../_models/emailconfig';
import { AlertService } from '../../_services/alert.service';
import { EmailConfigurationDTO } from '../../_models/DTO/emailConfigurationDTO';
import { fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';

@Component({
    moduleId: module.id,
    styleUrls: ['email.component.css'],
    templateUrl: 'emailOverview.component.html',
    selector: 'emailOverview'
})
export class EmailOverviewComponent {

    config: EmailConfigurationDTO[];
    loading = false;
    selectedConfig: EmailConfigurationDTO;
    deleted = false;
    isConfigUpdated = false;
    isConfigAdded = false;

    public totalSize = 0;

    displayedColumns = ['email', 'id', 'incomingPort', 'action'];
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filterValue') filterValue: ElementRef;

    constructor(
        private httpService: HttpService,
        private alertService: AlertService,
        private dialog: MatDialog,
        private translate: TranslateService) {
    }

    ngOnInit() {
        this.paginator.pageSize = 10;
        this.loadAllConfigurations();

    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;

        fromEvent(this.filterValue.nativeElement, 'keyup')
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                tap(() => {
                    this.paginator.pageIndex = 0;
                    this.loadAllConfigurations();
                })
            )
            .subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadAllConfigurations())
            )
            .subscribe();
    }

    private loadAllConfigurations() {

        this.loading = true;
        this.httpService.get(environment.apiEmail)
            .subscribe(
                data => {
                    this.config = data;
                    this.dataSource.data = this.config;

                    if (!this.deleted && !this.isConfigUpdated && !this.isConfigAdded) {
                        this.alertService.showSuccessMessage(data, 'message.emailConfig', false, true);
                        this.loading = false;
                    }
                    this.deleted = false;
                    this.isConfigUpdated = false;
                    this.isConfigAdded = false;
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    public onMenuTriggerClick(config: EmailConfigurationDTO) {
        this.selectedConfig = config;
    }

    openDialogViewInbox(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '750px';

        dialogConfig.data = {
            emails: this.selectedConfig.emails
        };
        this.dialog.open(EmailInboxComponent, dialogConfig);

    }

    openDialogAddConfig(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '500px';

        dialogConfig.data = {
            config: new EmailConfiguration(),
        };

        const dialogRef = this.dialog.open(EmailAccountAddComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.email');
                this.isConfigAdded = true;
                this.loadAllConfigurations();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    openDialogEditConfig(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '500px';
        dialogConfig.data = {
            config: this.selectedConfig.configuration,
        };
        const dialogRef = this.dialog.open(EmailAccountAddComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.emailConfig.update');
                this.isConfigUpdated = true;
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    public openDialogDeleteConfig() {
        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;

        dialogConfig.data = {
            id: 1,
            title: this.translate.instant('message.areyousure'),
            content: this.translate.instant('message.emailConfig.dialog')
        };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true) {
                this.deleteConfig(this.selectedConfig.configuration);
            }
        });
    }

    public deleteConfig(config: EmailConfiguration) {
        this.loading = true;
        const apiUrl = environment.apiEmailConfigId.replace('{emailConfigId}', config.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.emailConfig.delete');
                this.deleted = true;
                this.loadAllConfigurations();
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }
}
