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
import {Alert, AlertType} from '../_models/index';
import {AlertService} from '../_services/index';

@Component({
	moduleId: module.id,
	styleUrls: ['alert.component.css'],
	selector: 'alert',
	templateUrl: 'alert.component.html'
})

export class AlertComponent {
	alerts: Alert[] = [];

	constructor(private alertService: AlertService) { }

	ngOnInit() {
		this.alertService.getAlert().subscribe((alert: Alert) => {
			if (!alert) {
				// clear alerts when an empty alert is received
				this.alerts = [];
				return;
			}

			// add alert to array
			this.alerts.length = 0;
			this.alerts.push(alert);
			// setTimeout(() => this.removeAlert(alert), 3000);
		});
	}

	removeAlert(alert: Alert) {
		this.alerts = this.alerts.filter(x => x !== alert);
	}

	cssClass(alert: Alert) {
		if (!alert) {
			return;
		}

		// return css class based on alert type
		switch (alert.type) {
			case AlertType.Success:
				return 'alert alert-success';
			case AlertType.Error:
				return 'alert alert-danger';
			case AlertType.Info:
				return 'alert alert-info';
			case AlertType.Warning:
				return 'alert alert-warning';
		}
	}
}
