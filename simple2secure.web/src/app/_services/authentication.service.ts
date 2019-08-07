import {Injectable} from '@angular/core';

import {JwtHelper} from 'angular2-jwt';

@Injectable()
export class AuthenticationService {

	jwtHelper: JwtHelper = new JwtHelper();

	constructor() { }

	logout() {
		// remove user from local storage to log user out
		localStorage.removeItem('token');
		localStorage.removeItem('currentUser');
		localStorage.removeItem('context');
	}

	public isAuthenticated(): boolean {
		const context = localStorage.getItem('context');
		const token = localStorage.getItem('token');
		if (token && context) {
			return !this.jwtHelper.isTokenExpired(token);
		}
		else {
			return false;
		}

	}
}
