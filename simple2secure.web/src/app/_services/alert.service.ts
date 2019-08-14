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

import {Injectable} from '@angular/core';
import {Router, NavigationStart} from '@angular/router';
import {Observable, Subject} from 'rxjs';
import {Alert, AlertType} from '../_models/index';

@Injectable()
export class AlertService {
	private subject = new Subject<Alert>();
	private keepAfterRouteChange = false;

	constructor(private router: Router) {
		// clear alert messages on route change unless 'keepAfterRouteChange' flag is true
		router.events.subscribe(event => {
			if (event instanceof NavigationStart) {
				if (this.keepAfterRouteChange) {
					// only keep for a single route change
					this.keepAfterRouteChange = false;
				} else {
					// clear alert messages
					this.clear();
				}
			}
		});
	}

	getAlert(): Observable<any> {
		return this.subject.asObservable();
	}

	success(message: string, keepAfterRouteChange = false) {
		this.alert(AlertType.Success, message, keepAfterRouteChange);
	}

	error(message: string, keepAfterRouteChange = false) {
		this.alert(AlertType.Error, message, keepAfterRouteChange);
	}

	info(message: string, keepAfterRouteChange = false) {
		this.alert(AlertType.Info, message, keepAfterRouteChange);
	}

	warn(message: string, keepAfterRouteChange = false) {
		this.alert(AlertType.Warning, message, keepAfterRouteChange);
	}

	alert(type: AlertType, message: string, keepAfterRouteChange = false) {
		this.keepAfterRouteChange = keepAfterRouteChange;
		this.subject.next(<Alert>{type: type, message: message});
	}

	clear() {
		// clear alerts
		this.subject.next();
	}
}
