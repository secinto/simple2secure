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
import { LDCSystemUnderTest } from '../_models/LDCSystemUnderTest';
import { SDCSystemUnderTest } from '../_models/SDCSystemUnderTest';
import { environment } from '../../environments/environment';
import { SUTType } from '../_models/sutType';
import { Protocol } from '../_models/protocol';



@Component({
	moduleId: module.id,
	templateUrl: 'sutDetails.component.html'
})
export class SUTDetailsComponent {
	
	sutTypeSelect = Object.keys(SUTType);
	selectedType: SUTType;
	id: string;
	contextId: string;
	uri = '';
	sutName = '';
	ipAddress = '';
	ldcport = '';
	sdcport = '';
	protocolSelect = Object.keys(Protocol);
	selectedProtocol: Protocol;
	ldcSUT: LDCSystemUnderTest = new LDCSystemUnderTest();
	sdcSUT: SDCSystemUnderTest = new SDCSystemUnderTest(); 
    action: string;
    isNewSUT = false;
    url: string;
    loading = false;
	invalidIpOrPort = false;

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
			this.id = data.sut.id;
			this.contextId = data.sut.contextId;
			if(data.type == 'LDCSUT'){
				this.selectedType = SUTType.LDCSUT;
				this.sutName = data.sut.name;
				this.ipAddress = data.sut.ipAddress;
				this.ldcport = data.sut.port;
				this.selectedProtocol = data.sut.protocol;
			}else if (data.type == 'SDCSUT') {
				this.selectedType = SUTType.SDCSUT;
				this.sutName = data.sut.name;
				this.sdcport = data.sut.port;
			}else {
				this.selectedType = SUTType.UNKNOWN;
			}
		}else {
			this.isNewSUT = true;
		}
    }

    public save() {
		if(this.selectedType == SUTType.LDCSUT){
			if(!this.isValidIp(this.ipAddress) || !this.isValidPort(this.ldcport)){
				this.invalidIpOrPort = true;
			}else {
				this.ldcSUT.name = this.sutName;
				this.ldcSUT.ipAddress = this.ipAddress;
				this.ldcSUT.port = this.ldcport;
				this.ldcSUT.protocol = this.selectedProtocol;
				this.ldcSUT.uri = this.selectedProtocol.toLowerCase() + '://' + this.ipAddress + ':' + this.ldcport;
				this.invalidIpOrPort = false;
			}
			if(!this.invalidIpOrPort){
				this.loading = true;
				this.url = environment.apiEndpoint + 'sut/addLDC';
				this.httpService.post(this.ldcSUT, this.url).subscribe(
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
		}else if(this.selectedType == SUTType.SDCSUT){
			this.sdcSUT.name = this.sutName;
			this.sdcSUT.port = this.sdcport;
			this.sdcSUT.protocol = this.selectedProtocol;
			this.sdcSUT.uri = this.selectedProtocol.toLowerCase() + '://' + this.sdcport;
			this.loading = true;
			this.url = environment.apiEndpoint + 'sut/addSDC';
			this.httpService.post(this.sdcSUT, this.url).subscribe(
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
	}
	
	public update() {
		if(this.selectedType == SUTType.LDCSUT){
			if(!this.isValidIp(this.ipAddress) || !this.isValidPort(this.ldcport)){
				this.invalidIpOrPort = true;
			}else {
				this.ldcSUT.id = this.id;
				this.ldcSUT.contextId = this.contextId;
				this.ldcSUT.name = this.sutName;
				this.ldcSUT.ipAddress = this.ipAddress;
				this.ldcSUT.port = this.ldcport;
				this.ldcSUT.protocol = this.selectedProtocol;
				this.ldcSUT.uri = this.selectedProtocol.toLowerCase() + '://' + this.ipAddress + ':' + this.ldcport;
				this.invalidIpOrPort = false;
			}
			if(!this.invalidIpOrPort){
				this.loading = true;
				this.url = environment.apiEndpoint + 'sut/updateLDC';
				this.httpService.post(this.ldcSUT, this.url).subscribe(
					data => {
						this.alertService.success(this.translate.instant('message.sut.update'));
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
		}else if(this.selectedType == SUTType.SDCSUT){
			this.sdcSUT.id = this.id;
			this.sdcSUT.contextId = this.contextId;
			this.sdcSUT.name = this.sutName;
			this.sdcSUT.port = this.sdcport;
			this.sdcSUT.protocol = this.selectedProtocol;
			this.sdcSUT.uri = this.selectedProtocol.toLowerCase() + '://' + this.sdcport; 
			this.loading = true;
			this.url = environment.apiEndpoint + 'sut/updateSDC';
			this.httpService.post(this.sdcSUT, this.url).subscribe(
				data => {
					this.alertService.success(this.translate.instant('message.sut.update'));
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
	}
	
    public close(value: boolean){
		this.dialogRef.close(value);
	}
	
	public isValidIp = value => (/^(?:(?:^|\.)(?:2(?:5[0-5]|[0-4]\d)|1?\d?\d)){4}$/.test(value) ? true : false);
	public isValidPort = value => (/^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$/.test(value) ? true : false);
}