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

import { Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { environment } from '../../../environments/environment';
import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { RuleAddRegexComponent } from './ruleAddRegex.component';
import { BaseComponent } from '../widgets/base.component';
import { RuleRegexDTO } from '../../_models/DTO/ruleRegexDTO';
import { RuleRegex } from '../../_models/ruleRegex';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';
import { HttpService } from '../../_services/http.service';
import { fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';

@Component({
    moduleId: module.id,
    templateUrl: './ruleRegexList.component.html'
})

export class RuleRegexListComponent extends BaseComponent {

    loading = false;
    regexDTO: RuleRegexDTO;
    regexes: RuleRegex[];
    selectedRegex: RuleRegex;
    displayedColumns = ['name', 'description', 'action'];
    dataSource = new MatTableDataSource();
    @ViewChild('paginator') paginator: MatPaginator;
    @ViewChild('sort') sort: MatSort;
    @ViewChild('filterValue') filterValue: ElementRef;
    public totalSize = 0;

    constructor(dialog: MatDialog,
        alertService: AlertService,
        translate: TranslateService,
        dataService: DataService,
        httpService: HttpService,
        route: ActivatedRoute) {
        super(dialog, alertService, translate, dataService, httpService, route);
    }

    ngOnInit() {
        this.paginator.pageSize = 10;
        this.loadRegex();

        this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
            this.loadRegex();
        });
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;

        fromEvent(this.filterValue.nativeElement, 'keyup')
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                tap(() => {
                    this.paginator.pageIndex = 0;
                    this.loadRegex();
                })
            )
            .subscribe();

        this.paginator.page
            .pipe(
                tap(() => this.loadRegex())
            )
            .subscribe();
    }

    public onMenuTriggerClick(regex) {
        this.selectedRegex = regex;
    }


    public onOpenDialogAddRegex() {
        this.openDialogAddOrEditRegex(null);
    }

    public onEditClick() {
        this.openDialogAddOrEditRegex(this.selectedRegex);
    }


    public onDeleteClick() {
        this.onDeleteDialog();
    }


    /**
     * Method to open a dialog for creating/editing a regex
     * @param regex for editing, if this param is null it will create a new one
     */
    public openDialogAddOrEditRegex(regex) {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '500px';

        if (regex) // if regex is given it will be provided the RuleAdd dialog for editing
        {
            dialogConfig.data = {
                regex: regex
            };
        }


        const dialogRef = this.dialog.open(RuleAddRegexComponent, dialogConfig);
        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.rule.regex.saved');
                this.loadRegex();
            }
            else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }


    private onDeleteDialog() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;

        dialogConfig.data = {
            id: 1,
            title: this.translate.instant('message.areyousure'),
        };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true) {
                this.deleteRegex(this.selectedRegex);
            }
        });
    }


    /**
     * Method to delete a regex from the db
     * @param regex
     */
    private deleteRegex(regex) {
        const apiUrl = environment.apiRuleRegexById.replace('{ruleRegexId}', regex.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.rule.regex.delete');
                this.loadRegex();
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }


    /**
     * Method to fetch all regex from the database and display them in the table
     */
    private loadRegex() {
        this.loading = true;

        const httpParams = new HttpParams()
            .set('page', String(this.paginator.pageIndex))
            .set('size', String(this.paginator.pageSize))
            .set('filter', this.filterValue.nativeElement.value);

        this.httpService.getWithParams(environment.apiRuleRegex, httpParams)
            .subscribe(
                data => {
                    this.regexDTO = data;
                    this.regexes = this.regexDTO.regexes;
                    this.dataSource.data = this.regexes;
                    this.totalSize = this.regexDTO.totalSize;
                    this.loading = false;
                    this.alertService.showSuccessMessage(data, 'message.rule.regex.loadeded', false, true);
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

}
