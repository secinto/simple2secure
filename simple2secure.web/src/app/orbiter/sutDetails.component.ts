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
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef, MatDialogConfig } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { Router, ActivatedRoute } from '@angular/router';
import { SystemUnderTest } from '../_models/systemUnderTest';
import { environment } from '../../environments/environment';
import { Protocol } from '../_models/protocol';



@Component({
	moduleId: module.id,
	styleUrls: ['sut.css'],
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
	invalidIpOrPort = false;
	public metadataArr: any[];

    constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
        private dialog: MatDialog,
		private dialogRef: MatDialogRef<SUTDetailsComponent>,
		private translate: TranslateService,
		private router: Router,
        private route: ActivatedRoute,
        @Inject(MAT_DIALOG_DATA) data)
	{

		this.action = data.action;
		if (this.action ==	'edit'){
			this.sut = data.sut;
			this.selectedProtocol = data.sut.protocol;
			this.populateArrFromSutMetaData(this.sut);
		}else {
			this.isNewSUT = true;
		}
    }

    public save() {
		this.loading = true;
		this.sut = this.populateSutMetaDataFromArr(this.sut);
		this.sut.protocol = this.selectedProtocol;
		this.url = environment.apiEndpoint + 'sut/addSut';
		this.httpService.post(this.sut, this.url).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.sut.create'));
				this.close(true);
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
	
	public update() {
		this.sut = this.populateSutMetaDataFromArr(this.sut);
		this.loading = true;
		this.url = environment.apiEndpoint + 'sut/updateSut';
		this.httpService.post(this.sut, this.url).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.sut.create'));
				this.close(true);
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
	
	public addMetaData(){
		if(this.metadataArr){
			this.metadataArr.push({key: '', value: ''});
		}else {
			this.metadataArr = [{key: '', value: ''}];
		}
	}
	
	public removeMetaData(index) {
		this.metadataArr.splice(index,1)
    }
	
    public close(value: boolean){
		this.dialogRef.close(value);
	}
	
	public populateSutMetaDataFromArr(sut){
		sut.metadata = {};
		for(let pair of this.metadataArr) {
			sut.metadata[pair.key] = pair.value;
		}
		return sut;
	}
	
	public populateArrFromSutMetaData(sut){
		if(!this.metadataArr){
			this.metadataArr = [];
			for(let mdKey in sut.metadata){
				this.metadataArr.push({key: mdKey, value: sut.metadata[mdKey]});
			}
		}
	}
	
	public isValidIp = value => (/^(?:(?:^|\.)(?:2(?:5[0-5]|[0-4]\d)|1?\d?\d)){4}$/.test(value) ? true : false);
	public isValidPort = value => (/^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$/.test(value) ? true : false);
}