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

@Component({
	moduleId: module.id,
	templateUrl: 'userContextAddDialog.component.html',
	selector: 'userContextAddDialog',
	providers: [DatePipe]
})

export class UserContextAddDialogComponent {
	loading = false;
	id: string;
	private sub: any;
	url: string;
	isDialogOpen: boolean;
	context = new Context();
	isNewContext: boolean;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private alertService: AlertService,
		private translate: TranslateService,
		private datePipe: DatePipe,
		private dialogRef: MatDialogRef<UserContextAddDialogComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		if (data.context != null) {
			this.context = data.context;
			this.isNewContext = false;
		}
		else {
			this.isNewContext = true;
		}
	}

	ngOnInit() {
		this.sub = this.route.params.subscribe(params => {
			this.id = params['id'];
		});
	}

	saveContext() {
		this.loading = true;

		this.url = environment.apiEndpoint + 'context/add';
		this.httpService.post(this.context, this.url).subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
				this.loading = false;
			});
	}

	cancel() {
		this.location.back();
	}
}
