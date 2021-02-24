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

import { TestRun } from '../../_models/testRun';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'testContentDialog.component.html'
})

export class TestContentDialogComponent {
	testRun: TestRun = new TestRun();
	testContent: string;
	testContentRaw: string;
	loading = false;
	result: string;

	constructor(
		@Inject(MAT_DIALOG_DATA) data) {
		this.testRun = data.testRun;
		this.testContentRaw = data.testContent;
	}

	getTestContent() {
		this.testContent = JSON.parse(this.testContentRaw);
		return this.testContent;
	}

}
