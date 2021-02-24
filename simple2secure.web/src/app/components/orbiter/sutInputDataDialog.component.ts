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
import { SystemUnderTest } from '../../_models/systemUnderTest';
import { SelectionModel } from '@angular/cdk/collections';
import { TestInputData } from '../../_models/testInputData';
import { MatDialogConfig } from '@angular/material/dialog';
import { AddInputDataDialogComponent } from './addInputDataDialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';


@Component({
	moduleId: module.id,
	templateUrl: './sutInputDataDialog.component.html',
	styleUrls: ['orbiter.css'],
})

export class SutInputDataDialogComponent {

	dataSourceSut = new MatTableDataSource<SystemUnderTest>();
	dataSourceInputData = new MatTableDataSource<TestInputData>();
	selectionSut = new SelectionModel<SystemUnderTest>(false, []);
	selectionInputData = new SelectionModel<TestInputData>(false, []);
	displayedColumnsSut = ['name', 'protocol', 'select'];
	displayedColumnsInputData = ['name', 'select'];
	selectedTest: TestObjWeb = new TestObjWeb();
	testinputData: TestInputData[] = [];
	loading = false;
	url: string;
	isSutTest: boolean;
	isInputData: boolean;
	hasInput = false;

	public pageSize = 10;
	public currentPage = 0;
	public totalSizeSut = 0;
	public totalSizeInputData = 0;
	public pageEvent: PageEvent;

	constructor(
		private dialog: MatDialog,
		private dialogRef: MatDialogRef<SutInputDataDialogComponent>,
		private translate: TranslateService,
		private changeDetectorRefs: ChangeDetectorRef,
		private snackBar: MatSnackBar,
		@Inject(MAT_DIALOG_DATA) data) {
		this.dialogRef.disableClose = true;

		if (!data.sutList) {
			this.dataSourceSut.data = [];
		}
		else {
			this.dataSourceSut.data = data.sutList;
			this.totalSizeSut = data.sutList.length;
		}

		if (!data.inputDataList) {
			this.dataSourceInputData.data = [];
			this.testinputData = [];
		}
		else {
			this.testinputData = data.inputDataList;
			this.dataSourceInputData.data = data.inputDataList;
			this.totalSizeInputData = data.inputDataList.length;
		}
		this.selectedTest = data.selectedTest;
		this.isSutTest = data.isSutTest;
		this.isInputData = data.isInputData;
	}

	public handlePage(e?: PageEvent) {
		return e;
	}

	public close() {
		let errorMessage = '';
		if (this.isSutTest && this.isInputData) {
			if (this.selectionSut.hasValue() && this.selectionInputData.hasValue()) {
				this.hasInput = true;
			}
			else {
				if (this.selectionSut.isEmpty() && this.selectionInputData.isEmpty()) {
					errorMessage = this.translate.instant('message.sutAndInputDataNotSelected');
				}
				else if (this.selectionSut.isEmpty()) {
					errorMessage = this.translate.instant('message.sutNotSelected');
				}
				else if (this.selectionInputData.isEmpty()) {
					errorMessage = this.translate.instant('message.inputDataNotSelected');
				}
			}
		}
		else if (this.isSutTest) {
			if (this.selectionSut.hasValue()) {
				this.hasInput = true;
			}
			else {
				errorMessage = this.translate.instant('message.sutNotSelected');
			}
		}
		else if (this.isInputData) {
			if (this.selectionInputData.hasValue()) {
				this.hasInput = true;
			}
			else {
				errorMessage = this.translate.instant('message.inputDataNotSelected');
			}
		}

		if (this.hasInput) {
			this.dialogRef.close({ 'sut': this.selectionSut.selected[0], 'inputData': this.selectionInputData.selected[0] });
		}
		else {
			this.openSnackbar(errorMessage, '');
		}
	}

	public convertSUTMetadata(sut: SystemUnderTest) {
		return JSON.stringify(sut.metadata);
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

	public openDialogAddInputData() {
		const dialogConfig = new MatDialogConfig();

		dialogConfig.data = {
			testId: this.selectedTest.testId,
			type: 'new'
		};

		dialogConfig.minWidth = '750px';

		const dialogRef = this.dialog.open(AddInputDataDialogComponent, dialogConfig);
		dialogRef.afterClosed().subscribe(data => {
			// If data is null print some error message
			if (data) {
				this.testinputData.push(data);
				this.dataSourceInputData.data = this.testinputData;
				this.changeDetectorRefs.detectChanges();
			}
		});
	}
}
