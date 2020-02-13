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
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { Router, ActivatedRoute } from '@angular/router';
import { SystemUnderTest } from '../_models/systemUnderTest';
import { environment } from '../../environments/environment';
import { DeviceType } from '../_models/deviceType';


@Component({
	moduleId: module.id,
	templateUrl: 'sutDetails.component.html'
})

export class SUTDetailsComponent {

    sut: SystemUnderTest;
	deviceTypeSelect: any[];
    type: string;
	selectedType: any;
    isNewSUT = false;
    url: string;
    loading = false;

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

		this.type = data.type;
		if (
			this.type == 'new'){
			this.isNewSUT = true;
            this.sut = new SystemUnderTest();
            this.sut.contextId = data.contextId;
			this.deviceTypeSelect = Object.keys(DeviceType);
		}
    }


    public updateSaveSUT() {
		this.loading = true;
		switch(this.selectedType) { 
		   case "PROBE": { 
			  this.sut.endDeviceType = DeviceType.PROBE; 
			  break; 
		   } 
		   case "POD": { 
			  this.sut.endDeviceType = DeviceType.POD; 
			  break; 
		   } 
		   case "SUT": { 
			  this.sut.endDeviceType = DeviceType.SUT; 
			  break; 
		   } 
		   default: { 
			  this.sut.endDeviceType = DeviceType.UNKNOWN; 
			  break; 
		   } 
		} 
		this.url = environment.apiEndpoint + 'sut/add';
		this.httpService.post(this.sut, this.url).subscribe(
			data => {
				if (this.type === 'new') {
					this.alertService.success(this.translate.instant('message.sut.create'));
				}
				else {
					this.alertService.success(this.translate.instant('message.sut.update'));
				}
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

    public close(value: boolean){
		this.dialogRef.close(value);
	}

}
