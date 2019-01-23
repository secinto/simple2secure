import {Component} from '@angular/core';
import {MatDialog} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Settings, SettingsDTO, Timeunit} from '../_models';
import {environment} from '../../environments/environment';
import {LicensePlan} from '../_models/LicensePlan';

@Component({
	moduleId: module.id,
	styleUrls: ['settings.component.css'],
	templateUrl: 'settings.component.html',
	selector: 'settings'
})
export class SettingsComponent {

	loading = false;
	currentUser: any;
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
}
