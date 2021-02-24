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

import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpParams } from '@angular/common/http';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { CdkDragDrop, copyArrayItem } from '@angular/cdk/drag-drop';
import { MappedQueryEditDialog } from './mappedQueryEditDialog.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { OsQuery } from '../../_models/osQuery';
import { HttpService } from '../../_services/http.service';
import { CompanyGroup } from '../../_models/companygroup';
import { AlertService } from '../../_services/alert.service';

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

		private httpService: HttpService,
		private alertService: AlertService,
		private dialog: MatDialog,
		private changeDetectorRefs: ChangeDetectorRef) { }

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
		this.httpService.get(environment.apiGroupByContext)
			.subscribe(
				data => {
					this.groups = data;
					this.selectDefaultGroup(data);
					this.getMappedQueries(this.selectedGroup);
					this.getUnmappedQueries(this.selectedGroup);
				},
				error => {
					this.alertService.showErrorMessage(error);
				});
		this.loading = false;
	}

	public getUnmappedQueries(group: CompanyGroup) {
		this.loading = true;
		this.lastSelectedGroup = group;
		const apiUrl = environment.apiQueriesUnmappedByGroup.replace('{groupId}', group.id);
		this.httpService.get(apiUrl)
			.subscribe(
				data => {
					this.dataSourceUnmappedQueries.data = data;
				},
				error => {
					this.alertService.showErrorMessage(error);
				});
		this.loading = false;
	}

	public getMappedQueries(group: CompanyGroup) {
		this.loading = true;
		const params = new HttpParams()
			.set('select_all', String(true));
		const apiUrl = environment.apiQueriesGroup.replace('{groupId}', group.id);
		this.httpService.getWithParams(apiUrl, params)
			.subscribe(
				data => {
					this.dataSourceMappedQueries.data = data;
				},
				error => {
					this.alertService.showErrorMessage(error);
				});
		this.loading = false;
	}

	public changeGroup(group: CompanyGroup) {
		if (group != this.lastSelectedGroup) {
			this.updateTheMappings(this.lastSelectedGroup);
			this.selectedGroup = group;
			this.getMappedQueries(group);
			this.getUnmappedQueries(group);
		}
	}

	public updateTheMappings(group: CompanyGroup) {
		this.loading = true;
		const apiUrl = environment.apiQueriesMapGroup.replace('{groupId}', group.id);
		this.httpService.post(this.dataSourceMappedQueries.data, apiUrl)
			.subscribe(
				data => {
				},
				error => {
					this.alertService.showErrorMessage(error);
				});
		this.loading = false;
	}

	public selectDefaultGroup(groups: CompanyGroup[]) {
		this.selectedGroup = groups[0];
	}

	drop(event: CdkDragDrop<OsQuery[]>) {
		if (event.previousContainer != event.container) {
			if (!this.checkIfArrayContainsObject(event.container.data, event.previousContainer.data[event.previousIndex])) {

				let dataIndex = event.previousIndex;
				if (this.dataSourceUnmappedQueries.paginator.pageIndex > 0) {
					dataIndex = (this.dataSourceUnmappedQueries.paginator.pageIndex * this.dataSourceUnmappedQueries.paginator.pageSize) + event.previousIndex;
				}

				copyArrayItem(event.previousContainer.data,
					event.container.data,
					dataIndex,
					this.dataSourceMappedQueries.paginator.length);

				this.dataSourceMappedQueries.paginator.length = this.dataSourceMappedQueries.paginator.length + 1;
				this.dataSourceUnmappedQueries.data = this.dataSourceUnmappedQueries.data.filter(query => query['id'] != event.previousContainer.data[dataIndex].id);
				this.dataSourceUnmappedQueries.paginator.length = this.dataSourceUnmappedQueries.paginator.length - 1;

				this.changeDetectorRefs.detectChanges();
				this.alertService.showSuccessMessage(null, 'query.moving.success');
			}
			else {
				this.alertService.showErrorMessage(null, false, 'group.moving.error');
			}
		}
	}

	removeMappedQuery(item: OsQuery) {
		const index = this.dataSourceMappedQueries.data.indexOf(item);

		if (index > -1) {
			this.dataSourceUnmappedQueries.data.push(this.dataSourceMappedQueries.data.find(query => query['id'] == item.id));
			this.dataSourceMappedQueries.data = this.dataSourceMappedQueries.data.filter(query => query['id'] != item.id);
			this.dataSourceMappedQueries.paginator.length = this.dataSourceMappedQueries.paginator.length - 1;
			this.dataSourceUnmappedQueries.paginator.length = this.dataSourceUnmappedQueries.paginator.length + 1;

			this.changeDetectorRefs.detectChanges();
			this.alertService.showSuccessMessage(null, 'query.remove.success');
		}
		else {
			this.alertService.showErrorMessage(null, false, 'query.remove.error');
		}
	}

	editMappedQuery(item: OsQuery) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			queryRun: item,
		};

		const dialogRef = this.dialog.open(MappedQueryEditDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {

			const index = this.dataSourceMappedQueries.data.indexOf(item);
			if (index > -1) {
				this.dataSourceMappedQueries.data[index] = result;
			}
			this.alertService.showSuccessMessage(null, 'query.update.success');
		});
	}

	checkIfArrayContainsObject(mappedQueries: OsQuery[], movedQuery: OsQuery) {
		const index = mappedQueries.findIndex(query => query.name === movedQuery.name);

		if (index > -1) {
			return true;
		}
		else {
			return false;
		}
	}
}
