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

import {Component} from '@angular/core';
import {Config} from '../_models/index';
import {AlertService, HttpService} from '../_services/index';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';


@Component({
	moduleId: module.id,
	templateUrl: 'configurationDetails.component.html',
	styleUrls: ['configuration.component.css'],
	selector: 'configurationDetails'
})

export class ConfigurationDetailsComponent {
	config: Config;
	loading: boolean;

	constructor(
		private route: ActivatedRoute,
		private httpService: HttpService,
		private alertService: AlertService,
		private location: Location,
		private translate: TranslateService)
	{
		this.config = new Config();
		this.loading = false;
	}

	ngOnInit() {
		this.loadConfiguration();
	}

	private loadConfiguration() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'config/')
			.subscribe(
				data => {
					this.config = data;
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

	saveConfig() {
		this.loading = true;

		this.httpService.post(this.config, environment.apiEndpoint + 'config').subscribe(
			data => {
				this.config = data;
				this.alertService.success(this.translate.instant('message.configuration.save'));
				this.loading = false;
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
