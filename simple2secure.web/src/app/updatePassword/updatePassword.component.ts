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
import {ActivatedRoute, Params, Router} from '@angular/router';
import {AlertService, HttpService} from '../_services/index';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	styleUrls: ['updatePassword.component.css'],
	templateUrl: 'updatePassword.component.html'
})

export class UpdatePasswordComponent {
	newPassword: string;
	loading: boolean;
	token: string;
	activationToken: string;
	isMainRoute: boolean;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private activatedRoute: ActivatedRoute,
		private translate: TranslateService)
	{
		this.loading = false;
	}

	ngOnInit() {

		this.isMainRoute = false;

		if (this.route.snapshot.paramMap.get('id')) {
			this.isMainRoute = true;
		}

		if (this.isMainRoute) {
			this.activationToken = this.route.snapshot.paramMap.get('id');
		}
		else {
			this.activatedRoute.params.subscribe((params: Params) => {
				this.token = params['token'];
			});
		}

	}

	update() {
		this.loading = true;
		if (this.isMainRoute) {

			this.httpService.postUpdatePasswordFirstLogin(this.newPassword, this.activationToken)
				.subscribe(
					data => {
						this.alertService.success(this.translate.instant('message.passwordUpdate'), true);

						setTimeout((router: Router) => {
							this.router.navigate(['account/activate/' + this.activationToken]);
							this.loading = false;
						}, 2000);
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
		else {
			this.httpService.postUpdatePassword(this.newPassword, this.token)
				.subscribe(
					data => {
						this.alertService.success(this.translate.instant('message.passwordUpdate'), true);
						setTimeout((router: Router) => {
							this.router.navigate(['/login']);
							this.loading = false;
						}, 3000);
					},
					error => {
						console.log(error);
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
}
