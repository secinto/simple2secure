import { Component } from "@angular/core";
import { MatDialogConfig } from '@angular/material';

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


    openDialogShowSuT(type: string): void {

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		dialogConfig.data = {
			//sequence: this.selectedSequence,
			//type: type,
			//deviceId: this.id
		};

		//const dialogRef = this.dialog.open(TestSequenceDetailsComponent, dialogConfig);

        /*
		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.isSequenceChanged = true;
				this.loadSequences(this.id, this.currentPage, this.pageSize);
			}
		});*/

	}

}