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
import {Router, ActivatedRoute} from '@angular/router';
import {AlertService, HttpService} from '../_services/index';
import {environment} from '../../environments/environment';
import {saveAs as importedSaveAs} from 'file-saver';

@Component({
	moduleId: module.id,
	styleUrls: ['activation.component.css'],
	templateUrl: 'activation.component.html'
})

export class ActivationComponent {
	loading: boolean;
	activationToken: string;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private alertService: AlertService,
		private httpService: HttpService)
	{
		this.loading = false;
	}

	ngOnInit() {
		this.activationToken = this.route.snapshot.paramMap.get('id');
	}

	public download() {
		this.loading = true;
		this.httpService.getFile(environment.apiEndpoint + 'download')
			.subscribe(
				data => {
					importedSaveAs(data, 's2s_setup.exe');
					this.loading = false;
				},
				error => {
					this.alertService.error(error.errorMessage);
					this.loading = false;
				});
	}
}
