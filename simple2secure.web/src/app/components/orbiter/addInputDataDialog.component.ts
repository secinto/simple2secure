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
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { TestInputData } from '../../_models/testInputData';
import { environment } from '../../../environments/environment';
import { FileSystemFileEntry, NgxFileDropEntry } from 'ngx-file-drop';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
    moduleId: module.id,
    templateUrl: 'addInputDataDialog.component.html'
})

export class AddInputDataDialogComponent {

    currentInputData = new TestInputData();

    public files: NgxFileDropEntry[] = [];

    constructor(
        private alertService: AlertService,
        private dialogRef: MatDialogRef<AddInputDataDialogComponent>,
        private httpService: HttpService,
        private translate: TranslateService,
        private snackBar: MatSnackBar,
        @Inject(MAT_DIALOG_DATA) data) {
        if (data.type == 'new') {
            this.currentInputData = new TestInputData();
            this.currentInputData.testId = data.testId;
        }
        else {
            this.currentInputData = data.selectedInputData;
        }


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
                        this.currentInputData.data = text;
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

    public save() {
        this.httpService.post(this.currentInputData, environment.apiInputData).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.inputDataAdded');
                this.dialogRef.close(data);
            },
            error => {
                this.alertService.showErrorMessage(error);
            });
    }
}
