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

import { Component } from '@angular/core';
import { TestWebDTO } from '../../_models/DTO/TestWebDTO';
import { Parameter } from '../../_models/parameter';
import { TestContentDialogComponent } from './testContentDialog.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { TestContent } from '../../_models/testContent';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiter.component.html'
})

export class OrbiterComponent {

	constructor(public dialog: MatDialog) {
	}

	public parameterArrayToString(selectedTest: TestWebDTO) {
		let paramValue = '';
		for (let i = 0; i < selectedTest.test.test_content.test_definition.step.command.parameter.length; i++) {
			paramValue += selectedTest.test.test_content.test_definition.step.command.parameter[i].value;
		}
		return paramValue;
	}

	public parameterArrayToStringFromParamArray(parameter: Parameter[]) {
		let paramValue = '';
		for (let i = 0; i < parameter.length; i++) {
			paramValue += parameter[i].value;
		}
		return paramValue;
	}

	public openDialogShowTestContent(testContent: string) {

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			testContent: testContent
		};

		this.dialog.open(TestContentDialogComponent, dialogConfig);
	}

	public openDialogShowTestContentFromTestContentObj(testContent: TestContent) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			testContent: JSON.stringify(testContent)
		};

		this.dialog.open(TestContentDialogComponent, dialogConfig);
	}
}
