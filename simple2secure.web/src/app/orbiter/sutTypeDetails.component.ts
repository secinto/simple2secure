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
import { SUTDetailsComponent} from './sutDetails.component';
import { environment } from '../../environments/environment';
import { SUTType } from '../_models/sutType';
import { Protocol } from '../_models/protocol';


@Component({
	moduleId: module.id,
	templateUrl: 'sutTypeDetails.component.html'
})

export class SUTTypeDetailsComponent {
	
	sutTypeSelect: any[];
	selectedType: SUTType;
	sutName = '';
    type: string;
    isNewSUT = false;
    url: string;
    loading = false;

    constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
        private dialog: MatDialog,
		private dialogRef: MatDialogRef<SUTTypeDetailsComponent>,
		private translate: TranslateService,
		private router: Router,
        private route: ActivatedRoute,
        @Inject(MAT_DIALOG_DATA) data)
	{

		this.type = data.type;
		if (
			this.type == 'new'){
			this.isNewSUT = true;
			this.sutTypeSelect = Object.keys(SUTType);
		}
    }

    public next() {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		dialogConfig.data = {
			sutName: this.sutName,
			selectedType: this.selectedType
		};

		const dialogRef2 = this.dialog.open(SUTDetailsComponent, dialogConfig);
		this.dialogRef.close(true);
	}

    public close(value: boolean){
		this.dialogRef.close(value);
	}

}
