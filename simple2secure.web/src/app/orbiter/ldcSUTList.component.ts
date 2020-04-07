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
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef, MatDialogConfig, MatTableDataSource } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { Router, ActivatedRoute } from '@angular/router';
import { LDCSystemUnderTest } from '../_models/LDCSystemUnderTest';
import { environment } from '../../environments/environment';
import { Protocol } from '../_models/protocol';
import { TestObjWeb } from '../_models/testObjWeb';
import { SutDTO } from '../_models/DTO/sutDTO';


@Component({
	moduleId: module.id,
	templateUrl: 'ldcSUTList.component.html'
})

export class LDCSUTListComponent {
	
	dataSource = new MatTableDataSource();
	displayedColumns = ['name'];
	selectedTest: TestObjWeb = new TestObjWeb();
	loading = false;
	type = "new";
	url: string;
	sutTransferObj: SutDTO = new SutDTO();
	
	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
        private dialog: MatDialog,
		private dialogRef: MatDialogRef<LDCSUTListComponent>,
		private translate: TranslateService,
		private router: Router,
        private route: ActivatedRoute,
        @Inject(MAT_DIALOG_DATA) data)
	{
		this.dataSource = data.ldcSUTs;
		this.selectedTest = data.selectedTest;
    }
	
	public scheduleSutTest(sut){
		let brk = this.selectedTest;
		this.url = environment.apiEndpoint + 'test/scheduleLDCSUTTest';
		this.sutTransferObj.sutId = sut.id;
		this.sutTransferObj.testId = this.selectedTest.testId;
		this.httpService.post(this.sutTransferObj, this.url).subscribe(
			data => {
				this.loading = true;
				if (this.type === 'new') {
					this.alertService.success(this.translate.instant('message.sut.create'));
				}
				else {
					this.alertService.success(this.translate.instant('message.sut.update'));
				}
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