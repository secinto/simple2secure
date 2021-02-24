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

import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef, MatTableDataSource, PageEvent } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { TestObjWeb } from '../../_models/testObjWeb';
import { AlertService } from '../../_services/alert.service';
import { HttpService } from '../../_services/http.service';
import { TestInputData } from '../../_models/testInputData';
import { MatDialogConfig } from '@angular/material/dialog';
import { AddInputDataDialogComponent } from './addInputDataDialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SutInputDataDialogComponent } from './sutInputDataDialog.component';
import { environment } from '../../../environments/environment';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';


@Component({
	moduleId: module.id,
	templateUrl: './inputDataDialog.component.html',
	styleUrls: ['orbiter.css'],
})

export class InputDataDialogComponent {

	dataSource = new MatTableDataSource<TestInputData>();
	displayedColumns = ['name', 'action'];
	selectedTest: TestObjWeb = new TestObjWeb();
	loading = false;
	url: string;
	selectedInputData: TestInputData = new TestInputData();
	testinputData: TestInputData[] = [];

	public pageSize = 10;
	public currentPage = 0;
	public totalSize = 0;
	public pageEvent: PageEvent;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dialog: MatDialog,
		private dialogRef: MatDialogRef<SutInputDataDialogComponent>,
		private translate: TranslateService,
		private changeDetectorRefs: ChangeDetectorRef,
		private snackBar: MatSnackBar,
		@Inject(MAT_DIALOG_DATA) data) {
		this.dialogRef.disableClose = true;

		if (!data.inputDataList) {
			this.dataSource.data = [];
		}
		else {
			this.testinputData = data.inputDataList;
			this.dataSource.data = data.inputDataList;
			this.totalSize = data.inputDataList.length;
		}
		this.selectedTest = data.selectedTest;
	}

	/**
	 * Opens a snackbar and displays message and action
	 * @param message which should be displayed
	 * @param action which should be performed when clicked
	 */
	private openSnackbar(message: string, action: any) {
		this.snackBar.open(message, action, {
			duration: 2000
		});
	}

	public handlePage(e?: PageEvent) {
		this.currentPage = e.pageIndex;
		this.pageSize = e.pageSize;
		return e;
	}

	public onMenuTriggerClick(inputData: TestInputData) {
		this.selectedInputData = inputData;
	}

	public async openDeleteDialog() {

		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.inputdata.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		await dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteInputData();
			}
		});
	}

	public deleteInputData() {
		this.loading = true;
		const apiUrl = environment.apiInputDataDelete.replace('{inputDataId}', this.selectedInputData.id);
		this.httpService.delete(apiUrl).subscribe(
			data => {
				this.alertService.showSuccessMessage(data, 'message.inputdata.delete');
				const index = this.getInputDataIndex(this.selectedInputData);

				if (index > -1) {
					this.testinputData.splice(index, 1);
				}

				this.dataSource.data = this.testinputData;
				this.changeDetectorRefs.detectChanges();
			},
			error => {
				this.alertService.showErrorMessage(error);
				this.loading = false;
			});
	}

	public getInputDataIndex(inputData: TestInputData) {
		for (let i = 0; i < this.testinputData.length; i++) {
			if (this.testinputData[i].id == inputData.id) {
				return i;
			}
		}
		return -1;
	}

	public openDialogAddEditInputData(type: string) {

		const dialogConfig = new MatDialogConfig();

		dialogConfig.data = {
			testId: this.selectedTest.testId,
			selectedInputData: this.selectedInputData,
			type: type
		};

		dialogConfig.minWidth = '750px';

		const dialogRef = this.dialog.open(AddInputDataDialogComponent, dialogConfig);
		dialogRef.afterClosed().subscribe(data => {
			// If data is null print some error message
			if (data) {
				const index = this.getInputDataIndex(data);

				if (index != -1) {
					this.testinputData.splice(index, 1);
				}
				this.testinputData.push(data);
				this.dataSource.data = this.testinputData;
				this.changeDetectorRefs.detectChanges();
			}
		});

	}
}
