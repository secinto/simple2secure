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
import {CompanyGroup, Notification} from '../_models';
import {PodDTO} from '../_models/DTO/podDTO';
import {Widget} from '../_models/widget';

@Injectable()
export class DataService {
	data: any;

	get() {
		return JSON.parse(localStorage.getItem('data'));
	}

	set(data: any) {
		localStorage.setItem('data', JSON.stringify(data));
	}

	setGroupEditable(value: boolean) {
		localStorage.setItem('isGroupEditable', JSON.stringify(value));
	}

	isGroupEditable() {
		return JSON.parse(localStorage.getItem('isGroupEditable'));
	}

	getSelectedWidget(){
		return JSON.parse(localStorage.getItem('selectedWidget'));
	}

	addWidgetToLS(widget: Widget) {
		localStorage.setItem('selectedWidget', JSON.stringify(widget));
	}

	clearWidgets(){
		localStorage.removeItem('selectedWidget');
	}

}
