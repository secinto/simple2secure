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
import {CompanyGroup, OsQuery} from "../_models";
import {TranslateService} from "@ngx-translate/core";
import {HttpParams} from "@angular/common/http";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {CdkDragDrop, transferArrayItem} from "@angular/cdk/drag-drop";
import {MappedQueryEditDialog} from "./mappedQueryEditDialog.component";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";

@Component({
	moduleId: module.id,
	templateUrl: 'queryAssign.component.html',
	styleUrls: ['query.css'],
})

export class QueryAssignComponent {

	groups: CompanyGroup[] = [];
	selectedGroup: CompanyGroup;
	lastSelectedGroup: CompanyGroup;
	loading = false;
	displayedMappedColumns: string[] = ['query', 'action'];
	displayedUnmappedColumns: string[] = ['query'];
	dataSourceUnmappedQueries = new MatTableDataSource();
	dataSourceMappedQueries = new MatTableDataSource();
	@ViewChild('paginatorMapped') paginatorMapped: MatPaginator;
	@ViewChild('paginatorUnmapped') paginatorUnmapped: MatPaginator;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private translate: TranslateService,
		private dialog: MatDialog)
	{}

	ngOnInit() {
		this.getGroups();
	}

	ngAfterViewInit() {
		this.dataSourceUnmappedQueries.paginator = this.paginatorUnmapped;
		this.dataSourceMappedQueries.paginator = this.paginatorMapped;
	}

	ngOnDestroy() {
		this.updateTheMappings(this.lastSelectedGroup);
	}

	public getGroups() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'group/context')
			.subscribe(
				data => {
					this.groups = data;
					this.selectDefaultGroup(data);
					this.getMappedQueries(this.selectedGroup);
					this.getUnmappedQueries(this.selectedGroup);
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

	public getUnmappedQueries(group: CompanyGroup) {
		this.loading = true;
		this.lastSelectedGroup = group;
		this.httpService.get(environment.apiEndpoint + 'query/unmapped/' + group.id)
			.subscribe(
				data => {
					this.dataSourceUnmappedQueries.data = data;
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

	public getMappedQueries(group: CompanyGroup){
		this.loading = true;
		const params = new HttpParams()
			.set('select_all', String(true));
		this.httpService.getWithParams(environment.apiEndpoint + 'query/group/' + group.id, params)
			.subscribe(
				data => {
					this.dataSourceMappedQueries.data = data;
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

	public changeGroup(group: CompanyGroup){
		if(group != this.lastSelectedGroup){
			this.updateTheMappings(this.lastSelectedGroup);
			this.selectedGroup = group;
			this.getMappedQueries(group);
			this.getUnmappedQueries(group);
		}
	}

	public updateTheMappings(group: CompanyGroup){
		this.loading = true;
		this.httpService.post(this.dataSourceMappedQueries.data, environment.apiEndpoint + 'query/mapping/' + group.id)
			.subscribe(
				data => {
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

	public selectDefaultGroup(groups: CompanyGroup[]){
		this.selectedGroup = groups[0];
	}

	drop(event: CdkDragDrop<OsQuery[]>) {
		if (event.previousContainer != event.container) {
			if(!this.checkIfArrayContainsObject(event.container.data, event.previousContainer.data[event.previousIndex])){
				transferArrayItem(event.previousContainer.data,
					event.container.data,
					event.previousIndex,
					event.currentIndex);

				this.dataSourceMappedQueries.connect().next(event.container.data);
				this.dataSourceUnmappedQueries.data = event.previousContainer.data;
				this.alertService.success(this.translate.instant('query.moving.success'));
			}
			else{
				this.alertService.error(this.translate.instant('query.moving.error'));
			}
		}
	}

	removeMappedQuery(item: OsQuery){
		const index = this.dataSourceMappedQueries.data.indexOf(item);
		if(index > -1){
			this.dataSourceMappedQueries.data = this.dataSourceMappedQueries.data.filter(query => query.id != item.id);
			this.alertService.success(this.translate.instant('query.remove.success'));
		}
		else{
			this.alertService.error(this.translate.instant('query.remove.error'));
		}
	}

	editMappedQuery(item: OsQuery){
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			queryRun: item,
		};

		const dialogRef = this.dialog.open(MappedQueryEditDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {

			const index = this.dataSourceMappedQueries.data.indexOf(item);
			if(index > -1){
				this.dataSourceMappedQueries.data[index] = result;
			}
			this.alertService.success(this.translate.instant('query.update.success'));
		});
	}

	checkIfArrayContainsObject(mappedQueries: OsQuery[], movedQuery: OsQuery){
		console.log("MOVED QUERY" + movedQuery);
		const index = mappedQueries.findIndex(query=> query.name === movedQuery.name);

		if(index > -1){
			return true;
		}
		else{
			return false;
		}
	}
}
