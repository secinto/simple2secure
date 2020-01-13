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
import {AlertService, DataService, HttpService} from '../_services';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {BaseComponent} from '../components/base.component';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	templateUrl: 'devicesOverview.component.html'
})

export class DevicesOverviewComponent extends BaseComponent{

	location: string = this.route.component["name"];

	constructor(dialog: MatDialog,
				alertService: AlertService,
				translate: TranslateService,
				dataService: DataService, 
				httpService: HttpService,
				route: ActivatedRoute) {
		super(dialog, alertService, translate, dataService, httpService, route);
	}
}
