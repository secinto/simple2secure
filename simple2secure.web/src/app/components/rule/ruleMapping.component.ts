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

import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { environment } from '../../../environments/environment';
import { RuleFactType } from '../../_models/ruleFactType';
import { Device } from '../../_models/device';
import { DeviceType } from '../../_models/deviceType';
import { EmailConfiguration } from '../../_models/emailconfig';
import { RuleMappingDTO } from '../../_models/DTO/ruleMappingDTO';
import { HttpParams } from '@angular/common/http';

@Component({
    moduleId: module.id,
    styleUrls: ['rule.component.css'],
    templateUrl: 'ruleMapping.component.html',
})

export class RuleMappingComponent {

    ruleId: string;
    ruleFactType: RuleFactType;
    ruleFactTypeEnum = RuleFactType;
    ruleMappingDTO: RuleMappingDTO;
    emailConfigurations: EmailConfiguration[];
    devices: Device[];
    chosenItems = new Array();
    loading = true;

    constructor(
        private httpService: HttpService,
        private alertService: AlertService,
        private dialogRef: MatDialogRef<RuleMappingComponent>,
        @Inject(MAT_DIALOG_DATA) data) {

        this.ruleMappingDTO = data.ruleMappingDTO;
        this.ruleFactType = this.ruleMappingDTO.ruleFactType;
        this.ruleId = this.ruleMappingDTO.ruleId;
        this.getMappingObjects(this.ruleFactType);
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
    }

    private getMappingObjects(ruleFactType: RuleFactType) {
        this.loading = true;
        let apiUrl;
        let params;

        switch (ruleFactType) {
            case RuleFactType.EMAIL: {
                apiUrl = environment.apiEmail;
                break;
            }
            case RuleFactType.OSQUERYREPORT:
            case RuleFactType.NETWORKREPORT: {
                const page = 1;
                const size = 100;
                params = new HttpParams()
                    .set('active', String(true))
                    .set('page', String(page))
                    .set('size', String(size))
                    .set('filter', '');

                apiUrl = environment.apiDevicesByTypePagination.replace('{deviceType}', DeviceType.PROBE);
                break;
            }
            case RuleFactType.TESTSEQUENCERESULT:
            case RuleFactType.TESTRESULT: {
                const page = 1;
                const size = 100;
                params = new HttpParams()
                    .set('active', String(true))
                    .set('page', String(page))
                    .set('size', String(size))
                    .set('filter', '');
                apiUrl = environment.apiDevicesByTypePagination.replace('{deviceType}', DeviceType.POD);
                break;
            }
        }

        this.httpService.get(apiUrl)
            .subscribe(
                data => {
                    if (data) {

                        switch (ruleFactType) {
                            case RuleFactType.EMAIL: {
                                this.emailConfigurations = new Array();

                                this.alertService.showSuccessMessage(data, 'message.data', false, true);

                                if (data.length <= 0) {
                                    this.alertService.showErrorMessage(null, false, 'message.data.notProvided');
                                    this.loading = false;
                                    return;
                                }

                                data.forEach(dto => {
                                    this.emailConfigurations.push(dto.configuration);
                                    const found = this.ruleMappingDTO.mappedObjectIds.indexOf(dto.configuration.id) > -1;
                                    if (found == true) {
                                        this.chosenItems.push(true);
                                    }
                                    else {
                                        this.chosenItems.push(false);
                                    }
                                });
                                break;
                            }
                            case RuleFactType.OSQUERYREPORT:
                            case RuleFactType.NETWORKREPORT:
                            case RuleFactType.TESTSEQUENCERESULT:
                            case RuleFactType.TESTRESULT: {

                                if (data.devices.length <= 0) {
                                    this.alertService.showErrorMessage(null, false, 'message.data.notProvided');
                                    this.loading = false;
                                    return;
                                }

                                this.devices = data.devices;
                                this.devices.forEach(device => {
                                    const found = this.ruleMappingDTO.mappedObjectIds.indexOf(device.info.id) > -1;
                                    if (found == true) {
                                        this.chosenItems.push(true);
                                    }
                                    else {
                                        this.chosenItems.push(false);
                                    }
                                });
                                break;
                            }
                        }
                        this.loading = false;
                    }
                    else {
                        this.loading = false;
                        this.alertService.showErrorMessage(null, false, 'message.data.notProvided');
                    }
                },
                error => {
                    this.loading = false;
                    this.alertService.showErrorMessage(error);
                });
    }

    public saveMapping() {

        const chosenIds = new Array();

        switch (this.ruleFactType) {
            case RuleFactType.EMAIL: {
                for (let index = 0; index < this.emailConfigurations.length; index++) {
                    if (this.chosenItems[index] == true) {
                        chosenIds.push(this.emailConfigurations[index].id);
                    }
                }
                break;
            }
            case RuleFactType.OSQUERYREPORT:
            case RuleFactType.NETWORKREPORT:
            case RuleFactType.TESTSEQUENCERESULT:
            case RuleFactType.TESTRESULT: {
                for (let index = 0; index < this.devices.length; index++) {
                    if (this.chosenItems[index] == true) {
                        chosenIds.push(this.devices[index].info.id);
                    }
                }
                break;
            }
        }

        this.ruleMappingDTO.mappedObjectIds = chosenIds;

        this.httpService.post(this.ruleMappingDTO, environment.apiRuleMapping).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.dialogRef.close(error);
            });
    }

}
