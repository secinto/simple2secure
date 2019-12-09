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

import {Component, Inject} from '@angular/core';
import {Context} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {DatePipe} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {QueryCategory} from "../_models/queryCategory";

@Component({
	moduleId: module.id,
	templateUrl: 'queryCategoryAddDialog.component.html',
})

export class QueryCategoryAddDialog {
	loading = false;
	url: string;
	category = new QueryCategory();
	isNewCategory: boolean;
	windows: boolean;
	linux: boolean;
	macos: boolean;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private alertService: AlertService,
		private translate: TranslateService,
		private datePipe: DatePipe,
		private dialogRef: MatDialogRef<QueryCategoryAddDialog>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		if (data.context != null) {
			this.category = data.context;
			this.isNewCategory = false;
		}
		else {
			this.isNewCategory = true;
		}
	}

	saveCategory() {
		this.systemsValue();
		this.loading = true;

		this.url = environment.apiEndpoint + 'query/category';
		this.httpService.post(this.category, this.url).subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
				this.loading = false;
			});
	}

	systemsValue() {
		let test = 0;

		if (this.windows) {
			test = test + 1;
		}
		if(this.linux) {
			test = test + 2;
		}
		if(this.macos) {
			test = test + 4;
		}

		this.category.systemsAvailable = test;

	}

	cancel() {
		this.location.back();
	}
}
