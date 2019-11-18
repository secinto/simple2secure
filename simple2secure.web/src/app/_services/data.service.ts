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
		// information lost on page refresh
		// return this.data

		// information stored in local storage
		return JSON.parse(localStorage.getItem('data'));
	}

	set(data: any) {

		// information lost on page refresh
		// this.data = data;

		// information stored in local storage
		localStorage.setItem('data', JSON.stringify(data));
	}

	getCurrentUser() {
		return JSON.parse(localStorage.getItem('currentUser'));
	}

	setProbe(data: any) {
		localStorage.setItem('probe', JSON.stringify(data));
	}

	setTool(data: any) {
		localStorage.setItem('tool', JSON.stringify(data));
	}

	getTool() {
		return JSON.parse(localStorage.getItem('tool'));
	}

	static getProbe() {
		return JSON.parse(localStorage.getItem('probe'));
	}

	setGroups(data: CompanyGroup[]) {
		localStorage.setItem('groups', JSON.stringify(data));
	}

	getGroups() {
		return JSON.parse(localStorage.getItem('groups'));
	}

	setGroupEditable(value: boolean) {
		localStorage.setItem('isGroupEditable', JSON.stringify(value));
	}

	isGroupEditable() {
		return JSON.parse(localStorage.getItem('isGroupEditable'));
	}

	setPods(data: PodDTO) {
		localStorage.setItem('pod', JSON.stringify(data));
	}

	getPods() {
		return JSON.parse(localStorage.getItem('pod'));
	}

	setNotifications(notifications: Notification[]) {
		localStorage.setItem('notifications', JSON.stringify(notifications));
	}

	getNotifications(){
		return JSON.parse(localStorage.getItem('notifications'));
	}

	getSelectedWidget(){
		return JSON.parse(localStorage.getItem('selectedWidget'));
	}

	addWidgetToLS(widget: Widget) {
		localStorage.setItem('selectedWidget', JSON.stringify(widget));
	}

	clearWidgets(){
		localStorage.removeItem('selectedWidgets');
	}

}
