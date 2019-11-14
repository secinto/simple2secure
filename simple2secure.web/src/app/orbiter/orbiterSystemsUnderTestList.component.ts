import { Component } from "@angular/core";
import { MatDialogConfig, MatDialog } from '@angular/material';
import { AlertService, HttpService, DataService } from '../_services';
import { TranslateService } from '@ngx-translate/core';
import { Router, ActivatedRoute } from '@angular/router';
import { SUTDetailsComponent } from './sutDetails.component';

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


 @Component({
     moduleId: module.id,
     templateUrl: 'orbiterSystemsUnderTestList.component.html'
 })

export class OrbiterSystemsUnderTestListComponent {

	displayedColumns = ['name', 'groupId', 'endDevice', 'version', 'action'];
	groupId: string;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService,
		private router: Router,
		private route: ActivatedRoute
	) {}

	ngOnInit() {
		let groups = JSON.parse(localStorage.getItem('groups'));
		this.groupId = groups[0].id;
	}


    openDialogShowSuT(type: string): void {

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		dialogConfig.data = {
			type: type,
			groupId: this.groupId
		};

		const dialogRef = this.dialog.open(SUTDetailsComponent, dialogConfig);

        /*
		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.isSequenceChanged = true;
				this.loadSequences(this.id, this.currentPage, this.pageSize);
			}
		});*/

	}

}
