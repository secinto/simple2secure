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

import { Component } from '@angular/core';
import { TestMacro } from '../../_models/TestMacro';
import { environment } from '../../../environments/environment';
import { LicensePlan } from '../../_models/LicensePlan';
import { Widget } from '../../_models/widget';
import { SettingsDTO } from '../../_models/DTO/settingsDTO';
import { Timeunit } from '../../_models/timeunit';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { Settings } from '../../_models/settings';

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
        private httpService: HttpService,
        private alertService: AlertService) {
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
        this.httpService.get(environment.apiSettings)
            .subscribe(
                data => {
                    this.settingsObj = data;
                    if (this.updated) {
                        this.alertService.showSuccessMessage(data, 'message.settings.update', false, true);
                    } else {
                        this.alertService.showSuccessMessage(data, 'message.data', false, true);
                    }

                    this.updated = true;
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    updateSettings() {
        this.loading = true;
        this.httpService.post(this.settingsObj['settings'], environment.apiSettings).subscribe(
            data => {
                this.settingsObj['settings'] = data;
                this.updated = true;
                this.loadSettings();
            },
            error => {
                this.alertService.showErrorMessage(error);
            });
        this.loading = false;
    }

    addNewLicensePlan() {
        if (this.settingsObj.licensePlan) {
            this.settingsObj.licensePlan.push(new LicensePlan());
        } else {
            this.settingsObj.licensePlan = [];
            this.settingsObj.licensePlan.push(new LicensePlan());
        }
    }


    addNewTestMacro() {
        if (this.settingsObj.testMacroList) {
            this.settingsObj.testMacroList.push(new TestMacro());
        } else {
            this.settingsObj.testMacroList = [];
            this.settingsObj.testMacroList.push(new TestMacro());
        }
    }

    saveLicensePlan(licensePlan: LicensePlan) {
        this.loading = true;
        this.httpService.post(licensePlan, environment.apiSettingsLicensePlan).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.settings.update', false, true);
                this.loading = false;
                this.loadSettings();
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    deleteLicensePlan(licensePlan: LicensePlan) {
        this.loading = true;
        const apiUrl = environment.apiSettingsLicensePlanById.replace('{licensePlanId}', licensePlan.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'licensePlan.delete');
                this.loading = false;
                this.loadSettings();
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    deleteTestMacro(testMacro: TestMacro) {
        this.loading = true;
        const apiUrl = environment.apiSettingsTestMacroById.replace('{testMacroId}', testMacro.id);

        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'testmacro.delete');
                this.loading = false;
                this.loadSettings();
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    saveTestMacro(testMacro: TestMacro) {
        this.loading = true;
        this.httpService.post(testMacro, environment.apiSettingsTestMacro).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.settings.update');
                this.loading = false;
                this.loadSettings();
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    addNewWidget() {
        if (this.settingsObj.widgetList) {
            this.settingsObj.widgetList.push(new Widget());
        } else {
            this.settingsObj.widgetList = [];
            this.settingsObj.widgetList.push(new Widget());
        }
    }

    deleteWidget(widget: Widget) {
        this.loading = true;
        const apiUrl = environment.apiWidgetDeleteById.replace('{widgetId}', widget.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'widget.deleted');
                this.loading = false;
                this.loadSettings();
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    saveWidget(widget: Widget) {
        this.loading = true;
        this.httpService.post(widget, environment.apiWidgetAdd).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.settings.update');
                this.loading = false;
                this.loadSettings();
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }
}
