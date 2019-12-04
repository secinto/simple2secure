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
import {Notification, QueryRun} from "../_models";
import {QueryDTO} from "../_models/DTO/queryDTO";
import {TranslateService} from "@ngx-translate/core";
import {OsqueryConfigurationEditComponent} from "../osquery";
import {HttpErrorResponse} from "@angular/common/http";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {QueryCategory} from "../_models/queryCategory";

@Component({
	moduleId: module.id,
	templateUrl: 'queryList.component.html'
})

export class QueryListComponent {

	queries: QueryDTO[];
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
		this.httpService.get(environment.apiEndpoint + 'query/all')
			.subscribe(
				data => {
					this.queries = data;
					console.log(data);
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

	onAddClick(category: QueryCategory){
		console.log(category);
	}

	onEditClick(element: QueryRun) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			queryRun: element,
		};

		const dialogRef = this.dialog.open(OsqueryConfigurationEditComponent, dialogConfig);

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
}
