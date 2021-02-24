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
import { MAT_DIALOG_DATA } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { AlertService } from '../../_services/alert.service';
import { HttpService } from '../../_services/http.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from '../../../environments/environment';
import { FileSystemFileEntry, NgxFileDropEntry } from 'ngx-file-drop';
import { SystemUnderTest } from '../../_models/systemUnderTest';


@Component({
	moduleId: module.id,
	templateUrl: './importSutDialog.component.html',
	styleUrls: ['orbiter.css'],
})

export class ImportSutDialogComponent {

	public files: NgxFileDropEntry[] = [];
	public importedSuts: SystemUnderTest[] = [];
	public fileUploaded: boolean;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private translate: TranslateService,
		private snackBar: MatSnackBar) {
		this.fileUploaded = false;
	}

	public importSuts() {

		this.httpService.post(this.importedSuts, environment.apiSutImport).subscribe(
			data => {
				this.alertService.showSuccessMessage(data, 'message.test.update');
				// this.close(true);
			},
			error => {
				this.alertService.showErrorMessage(error);
				// this.close(true);
			});
	}

	public dropped(files: NgxFileDropEntry[]) {
		this.files = files;
		for (const droppedFile of files) {

			// Is it a file?
			if (droppedFile.fileEntry.isFile && this.isFileAllowed(droppedFile.fileEntry.name)) {
				const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
				fileEntry.file((file: File) => {
					const reader = new FileReader();

					reader.onload = (e) => {
						const text = reader.result.toString().trim();
						this.importedSuts = JSON.parse(text);
						this.fileUploaded = true;
					};

					reader.readAsText(file);
				});
			} else {
				this.openSnackbar(this.translate.instant('message.error.inputFileUpload', { dataType: '.json, .txt' }), '');
			}
		}
	}

	isFileAllowed(fileName: string) {
		let isFileAllowed = false;
		const allowedFiles = ['.txt', '.json'];
		const regex = /(?:\.([^.]+))?$/;
		const extension = regex.exec(fileName);
		if (undefined !== extension && null !== extension) {
			for (const ext of allowedFiles) {
				if (ext === extension[0]) {
					isFileAllowed = true;
				}
			}
		}
		return isFileAllowed;
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

	public fileOver(event) {
	}

	public fileLeave(event) {
	}
}
