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
import {UserRegistration, UserRegistrationType} from '../_models/index';
import {Router} from '@angular/router';
import {AlertService, HttpService} from '../_services/index';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	styleUrls: ['register.component.css'],
	templateUrl: 'register.component.html'
})

export class RegisterComponent {
	user: UserRegistration;
	loading: boolean;

	constructor(
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private translate: TranslateService)
	{
		this.user = new UserRegistration();
		this.loading = false;
	}

	register() {
		this.loading = true;
		this.user.registrationType = UserRegistrationType.STANDARD;
		this.httpService.postRegister(this.user)
			.subscribe(
				data => {
					this.alertService.success(this.translate.instant('message.registration'), true);
					this.loading = false;
					setTimeout((router: Router) => {
						this.router.navigate(['/login']);
					}, 3000);
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
