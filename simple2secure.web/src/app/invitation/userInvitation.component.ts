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
import {ActivatedRoute, Router} from '@angular/router';
import {AlertService, HttpService} from '../_services/index';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';

@Component({
	moduleId: module.id,
	styleUrls: ['userInvitation.component.css'],
	templateUrl: 'userInvitation.component.html'
})

export class UserInvitationComponent {
	loading: boolean;
	invitationToken: string;
	url: string;

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
		this.invitationToken = this.route.snapshot.paramMap.get('id');
	}


	public performContextAction(value: boolean) {
		this.loading = true;

		this.url = environment.apiEndpoint + 'user/invite/process/' + this.invitationToken + '/' + value;
		this.httpService.processInvitation(this.url).subscribe(
			data => {
				this.alertService.success(this.translate.instant('invitation.accept'), true);
				setTimeout((router: Router) => {
					this.router.navigate(['/']);
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
				setTimeout((router: Router) => {
					this.router.navigate(['/']);
					this.loading = false;
				}, 1000);
			});
	}
}
