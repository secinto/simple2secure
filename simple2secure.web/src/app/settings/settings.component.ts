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
import {MatDialog} from '@angular/material';
import {TestMacro} from '../_models/TestMacro';
import {AlertService, DataService, HttpService} from '../_services/index';
import {ActivatedRoute, Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Settings, SettingsDTO, Timeunit} from '../_models';
import {environment} from '../../environments/environment';
import {LicensePlan} from '../_models/LicensePlan';
import {Widget} from '../_models/widget';

@Component({
	moduleId: module.id,
	styleUrls: ['settings.component.css'],
	templateUrl: 'settings.component.html',
	selector: 'settings'
})
export class SettingsComponent {

	loading = false;
	settingsObj: SettingsDTO;
	timeUnits = Timeunit;
	updated = false;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService)
	{
		this.settingsObj = new SettingsDTO();
		this.settingsObj.licensePlan = [];
		this.settingsObj.settings = new Settings();
	}

	ngOnInit() {
		this.loadSettings();
	}

	extractTimeUnits(): Array<string> {
		const keys = Object.keys(this.timeUnits);
		return keys.slice();
	}

	loadSettings() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'settings')
			.subscribe(
				data => {
					this.settingsObj = data;
					if (this.updated) {
						this.alertService.success(this.translate.instant('message.settings.update'));
					}
					else {
						this.alertService.success(this.translate.instant('message.data'));
					}

					this.updated = true;
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

	updateSettings() {
		this.loading = true;
		this.httpService.post(this.settingsObj['settings'], environment.apiEndpoint + 'settings').subscribe(
			data => {
				this.settingsObj['settings'] = data;
				this.updated = true;
				this.loadSettings();
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
			});
		this.loading = false;
	}

	addNewLicensePlan() {
		if (this.settingsObj.licensePlan) {
			this.settingsObj.licensePlan.push(new LicensePlan());
		}
		else {
			this.settingsObj.licensePlan = [];
			this.settingsObj.licensePlan.push(new LicensePlan());
		}
	}


	addNewTestMacro(){
		if(this.settingsObj.testMacroList){
			this.settingsObj.testMacroList.push(new TestMacro())
		}
		else{
			this.settingsObj.testMacroList = [];
			this.settingsObj.testMacroList.push(new TestMacro())
		}
	}

	saveLicensePlan(licensePlan: LicensePlan) {
		this.loading = true;
		this.httpService.post(licensePlan, environment.apiEndpoint + 'settings/licensePlan').subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.settings.update'));
				this.loading = false;
				this.loadSettings();
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

	deleteLicensePlan(licensePlan: LicensePlan) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'settings/licensePlan/' + licensePlan.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.user.delete'));
				this.loading = false;
				this.loadSettings();
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

	deleteTestMacro(testMacro: TestMacro) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'settings/testmacro/' + testMacro.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.user.delete'));
				this.loading = false;
				this.loadSettings();
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

	saveTestMacro(testMacro: TestMacro) {
		this.loading = true;
		this.httpService.post(testMacro, environment.apiEndpoint + 'settings/testmacro').subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.settings.update'));
				this.loading = false;
				this.loadSettings();
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

	addNewWidget(){
		if (this.settingsObj.widgetList){
			this.settingsObj.widgetList.push(new Widget());
		}
		else{
			this.settingsObj.widgetList = [];
			this.settingsObj.widgetList.push(new Widget());
		}
	}

	deleteWidget(widget: Widget) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'widget/delete/' + widget.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.user.delete'));
				this.loading = false;
				this.loadSettings();
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

	saveWidget(widget: Widget) {
		this.loading = true;
		this.httpService.post(widget, environment.apiEndpoint + 'widget/add').subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.settings.update'));
				this.loading = false;
				this.loadSettings();
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
