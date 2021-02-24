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
import { Protocol } from '../../_models/protocol';
import { SystemUnderTest } from '../../_models/systemUnderTest';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { environment } from '../../../environments/environment';



@Component({
    moduleId: module.id,
    styleUrls: ['orbiter.css'],
    templateUrl: 'sutDetails.component.html'
})
export class SUTDetailsComponent {

    protocolSelect = Object.keys(Protocol);
    selectedProtocol: Protocol;
    sut: SystemUnderTest = new SystemUnderTest();
    action: string;
    isNewSUT = false;
    url: string;
    loading = false;
    public metadataArr: any[];

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private dialogRef: MatDialogRef<SUTDetailsComponent>,
        @Inject(MAT_DIALOG_DATA) data) {

        this.action = data.action;
        if (this.action == 'edit') {
            this.sut = data.sut;
            this.selectedProtocol = data.sut.protocol;
            this.populateArrFromSutMetaData(this.sut);
        } else {
            this.isNewSUT = true;
        }
    }

    public save() {
        this.loading = true;
        this.sut = this.populateSutMetaDataFromArr(this.sut);
        this.sut.protocol = this.selectedProtocol;
        this.httpService.post(this.sut, environment.apiSutAdd).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.sut.create');
                this.close(true);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public update() {
        this.sut = this.populateSutMetaDataFromArr(this.sut);
        this.loading = true;
        this.httpService.post(this.sut, environment.apiSutUpdate).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.sut.create');
                this.close(true);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public addMetaData() {
        if (this.metadataArr) {
            this.metadataArr.push({ key: '', value: '' });
        } else {
            this.metadataArr = [{ key: '', value: '' }];
        }
    }

    public removeMetaData(index) {
        this.metadataArr.splice(index, 1);
    }

    public close(value: boolean) {
        this.dialogRef.close(value);
    }

    public populateSutMetaDataFromArr(sut) {
        sut.metadata = {};
        for (const pair of this.metadataArr) {
            sut.metadata[pair.key] = pair.value;
        }
        return sut;
    }

    public populateArrFromSutMetaData(sut) {
        if (!this.metadataArr) {
            this.metadataArr = [];
            for (const mdKey in sut.metadata) {
                this.metadataArr.push({ key: mdKey, value: sut.metadata[mdKey] });
            }
        }
    }
}
