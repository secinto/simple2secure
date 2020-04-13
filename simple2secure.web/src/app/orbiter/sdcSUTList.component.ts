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

import { Component, Inject } from '@angular/core';
import { AlertService, HttpService, DataService } from '../_services';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef, MatDialogConfig, MatTableDataSource, MatSort, MatPaginator, PageEvent } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { Router, ActivatedRoute } from '@angular/router';
import { SDCSystemUnderTest } from '../_models/SDCSystemUnderTest';
import { environment } from '../../environments/environment';
import { TestObjWeb } from '../_models/testObjWeb';
import { SutDTO } from '../_models/DTO/sutDTO';


@Component({
	moduleId: module.id,
	templateUrl: 'sdcSUTList.component.html'
})

export class SDCSUTListComponent {
	
	dataSource = new MatTableDataSource();
	displayedColumns = ['name', 'port', 'Apply'];
	selectedTest: TestObjWeb = new TestObjWeb();
	loading = false;
	url: string;
	sutTransferObj: SutDTO = new SutDTO();
	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	public pageEvent: PageEvent;
	
	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
        private dialog: MatDialog,
		private dialogRef: MatDialogRef<SDCSUTListComponent>,
		private translate: TranslateService,
		private router: Router,
        private route: ActivatedRoute,
        @Inject(MAT_DIALOG_DATA) data)
	{
		this.dataSource = data.sdcSUTs;
		this.selectedTest = data.selectedTest;
    }
	
	public handlePage(e?: PageEvent) {
		return e;
	}
	
	public scheduleSutTest(sut){
		this.url = environment.apiEndpoint + 'test/scheduleLDCSUTTest';
		this.sutTransferObj.sutId = sut.id;
		this.sutTransferObj.testId = this.selectedTest.testId;
		this.httpService.post(this.sutTransferObj, this.url).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.sut.scheduled.on.test'));
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
		this.dialogRef.close(true);
	}
	
}