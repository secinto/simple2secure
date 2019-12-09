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

import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from "../../environments/environment";
import {AlertService, HttpService} from "../_services";
import {QueryRun} from "../_models";
import {QueryDTO} from "../_models/DTO/queryDTO";
import {TranslateService} from "@ngx-translate/core";
import {HttpErrorResponse} from "@angular/common/http";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {QueryCategory} from "../_models/queryCategory";
import {ConfirmationDialog} from "../dialog/confirmation-dialog";
import {QueryEditDialogComponent} from "./queryEditDialog.component";
import {QueryCategoryAddDialog} from "./queryCategoryAddDialog.component";

@Component({
	moduleId: module.id,
	templateUrl: 'queryList.component.html',
	styleUrls: ['query.css']
})

export class QueryListComponent {

	queries: QueryDTO[];
	deleted = false;
	added = false;
	loading = false;
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	displayedColumns = ['name', 'query', 'action'];

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private translate: TranslateService,
		private dialog: MatDialog)
	{}

	ngOnInit() {
		this.getQueries();
	}

	ngAfterViewInit(){
		this.dataSource.sort = this.sort;
		this.dataSource.sortingDataAccessor = (item, property) => {
			if (property === 'interval') {
				return (item['analysisIntervalUnit'] + ' ' + item['analysisInterval']);
			} else {
				return item[property];
			}
		}

		this.dataSource.paginator = this.paginator;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim();
		filterValue = filterValue.toLowerCase();
		this.dataSource.filter = filterValue;
	}

	public getQueries() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'query/allDto')
			.subscribe(
				data => {
					this.queries = data;
					if (this.deleted == false && this.added == false) {
						this.alertService.success(this.translate.instant('message.data'));
					}
					else {
						this.deleted = false;
						this.added = false;
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
		this.loading = false;
	}

	onEditClick(element: QueryRun) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			queryRun: element,
		};

		const dialogRef = this.dialog.open(QueryEditDialogComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.osquery.update'));
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
				}
			}
		});
	}

	onDeleteClick(element: QueryRun) {
		this.openDialog(element);
	}

	public openDialog(item: QueryRun) {

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

	deleteConfig(queryConfig: QueryRun) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'query/' + queryConfig.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.osquery.delete'));
				this.deleted = true;
				this.getQueries();
				this.loading = false;
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
				this.loading = false;
			});
		this.loading = false;
	}

	onAddClick(category: QueryCategory) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			queryRun: null,
			queryCategory: category
		};

		const dialogRef = this.dialog.open(QueryEditDialogComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.osquery.add'));
				this.added = true;
				this.getQueries();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
				}
			}
		});
	}

	onCategoryAddClick(){
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			category: null,
		};

		const dialogRef = this.dialog.open(QueryCategoryAddDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('query.category.add.success'));
				this.added = true;
				this.getQueries();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
				}
			}
		});
	}
}
