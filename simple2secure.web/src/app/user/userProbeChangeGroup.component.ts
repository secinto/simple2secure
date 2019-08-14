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
import {CompanyGroup, Probe, ContextDTO} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'userProbeChangeGroup.component.html',
	selector: 'UserProbeChangeGroupComponent',
})

export class UserProbeChangeGroupComponent {
	loading = false;
	id: string;
	url: string;
	currentUser: any;
	groups: CompanyGroup[];
	probe: Probe;
	selectedGroup: CompanyGroup;
	context: ContextDTO;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private alertService: AlertService,
		private translate: TranslateService,
		private dialogRef: MatDialogRef<UserProbeChangeGroupComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.probe = data;
		this.selectedGroup = this.probe.group;
	}

	ngOnInit() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadGroups();

	}

	private loadGroups() {
		this.httpService.get(environment.apiEndpoint + 'group/context/' + this.context.context.id)
			.subscribe(
				data => {
					this.extractGroups(data);
				},
				error => {

					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
				});
	}

	public changeGroup() {

		this.loading = true;

		this.url = environment.apiEndpoint + 'probe/changeGroup/' + this.probe.probeId;
		this.httpService.post(this.selectedGroup, this.url).subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
				this.loading = false;
			});
	}

	extractGroups(groups: CompanyGroup[]) {
		this.groups = [];
		for (let i = 0; i < groups.length; i++) {
			if (groups[i].id == this.selectedGroup.id) {
			}
			else {
				this.groups.push(groups[i]);
			}
		}
	}
}
