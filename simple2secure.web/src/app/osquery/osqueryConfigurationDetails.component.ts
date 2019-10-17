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
import {AlertService, DataService, HttpService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {HttpErrorResponse} from '@angular/common/http';
import {QueryRun} from '../_models';
import {OsqueryConfigurationEditComponent} from './osqueryConfigurationEdit.component';
import {UserGroupComponent} from '../user';

@Component({
	moduleId: module.id,
	selector: 'osQueryConfigDetails',
	styleUrls: ['osquery.css'],
	templateUrl: 'osqueryConfigurationDetails.component.html'
})

export class OsqueryConfigurationDetailsComponent {
	displayedColumns = ['name', 'query', 'runAlways', 'interval', 'active', 'action'];
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	currentUser: any;
	queries: any[];
	selectedItem: QueryRun;
	loading = false;
	type: number;
	deleted = false;
	added = false;
	private sub: any;
	groupId: string;
	probeId: string;
	groupEditable: boolean;


	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private router: Router,
		private dialog: MatDialog,
		private dataService: DataService,
		private route: ActivatedRoute,
		private translate: TranslateService
	)
	{}

	ngOnInit() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));

		this.sub = this.route.params.subscribe(params => {
			this.groupId = params['id'];
		});

		this.groupEditable = this.dataService.isGroupEditable();

		if (!this.groupEditable) {
			this.displayedColumns = ['name', 'query', 'runAlways', 'interval', 'active'];
		}

		this.loadQueries();
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim();
		filterValue = filterValue.toLowerCase();
		this.dataSource.filter = filterValue;
	}

	loadQueries() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'config/query/group/' + this.groupId + '/true')
			.subscribe(
				data => {
					this.queries = data;
					this.dataSource.data = this.queries;
					if (data.length > 0) {
						if (this.deleted == false && this.added == false) {
							this.alertService.success(this.translate.instant('message.data'));
						}
						else {
							this.deleted = false;
							this.added = false;
						}
					}
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
	}

	onMenuTriggerClick(item: any) {
		this.selectedItem = item;
	}

	onEditClick() {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			queryRun: this.selectedItem,
			groupId: this.groupId
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

	onAddClick() {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			queryRun: null,
			groupId: this.groupId
		};

		const dialogRef = this.dialog.open(OsqueryConfigurationEditComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.osquery.add'));
				this.added = true;
				this.loadQueries();
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
			content: this.translate.instant('message.config.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteConfig(item);
			}
		});
	}

	deleteConfig(queryConfig: any) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'config/query/' + queryConfig.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.osquery.delete'));
				this.deleted = true;
				this.loadQueries();
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
	}
}
