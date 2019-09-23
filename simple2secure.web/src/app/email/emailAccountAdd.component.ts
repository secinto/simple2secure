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
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ContextDTO, EmailConfiguration, User} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	styleUrls: ['email.component.css'],
	templateUrl: 'emailAccountAdd.component.html',
	selector: 'emailAccountAdd'
})
export class EmailAccountAddComponent {

	public config: EmailConfiguration;
	loading = false;
	context: ContextDTO;
	isConfigAdded = false;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private dataService: DataService,
		private url: LocationStrategy,
		private translate: TranslateService,
		private dialogRef: MatDialogRef<EmailAccountAddComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{

		this.context = JSON.parse(localStorage.getItem('context'));
		this.config = data.config;

		if (this.config.id) {
			this.isConfigAdded = false;
		}
		else {
			this.isConfigAdded = true;
			this.config.contextId = this.context.context.id;
		}
	}

	saveConfig() {
		this.httpService.post(this.config, environment.apiEndpoint + 'email').subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
			});
		this.loading = false;
	}
}
