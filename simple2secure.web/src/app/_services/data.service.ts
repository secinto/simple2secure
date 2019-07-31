import {Injectable} from '@angular/core';
import {CompanyGroup, Notification} from '../_models';
import {PodDTO} from '../_models/DTO/podDTO';

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

}
