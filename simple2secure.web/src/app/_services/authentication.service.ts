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
import {JwtHelper} from 'angular2-jwt';

@Injectable()
export class AuthenticationService {

	jwtHelper: JwtHelper = new JwtHelper();

	constructor() { }

	logout() {
		// remove user from local storage to log user out
		localStorage.removeItem('currentUser');
		localStorage.removeItem('isGroupEditable');
	}

	public isAuthenticated(): boolean {
		const currentUser = JSON.parse(localStorage.getItem('currentUser'));
		if (currentUser) {
			if(currentUser.token){
				return !this.jwtHelper.isTokenExpired(currentUser.token);
			}
		}
		return false;
	}
}
