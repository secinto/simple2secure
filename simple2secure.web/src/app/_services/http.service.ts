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
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse} from '@angular/common/http';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {Context, User, UserRegistration} from '../_models';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationService} from './authentication.service';
import { Location } from '@angular/common';

@Injectable()
export class HttpService {

	returnUrl: string;

	constructor(private route: ActivatedRoute,
	            protected httpClient: HttpClient,
	            private translate: TranslateService,
	            private router: Router,
	            private authenticationService: AuthenticationService,
	            private location: Location)
	{
		this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
	}

	currentLang: string;

	public get(url: string): Observable<any> {
		const headers = this.getHeaders(true);

		return this.httpClient.get<any>(url, {headers});
	}

	public getWithParams(url: string, params: HttpParams): Observable<any> {
		const headers = this.getHeaders(true);

		return this.httpClient.get<any>(url, {headers, params});
	}

	public post(item: any, url: string): Observable<any> {
		const headers = this.getHeaders(true);

		return this.httpClient.post<any>(url, item, {headers});
	}

	public delete(url: string): Observable<any> {
		const headers = this.getHeaders(true);

		return this.httpClient.delete<any>(url, {headers});
	}

	public getFile(url: string): Observable<Blob> {
		const headers = this.getHeaders(true);

		return this.httpClient.get<Blob>(url, {responseType: 'blob' as 'json', headers}).pipe();
	}

	public postLogin(username: string, password: string): Observable<HttpResponse<any>> {
		const headers = this.getHeaders();
		return this.httpClient.post<any>(environment.apiEndpoint + 'login',
			JSON.stringify({username: username, password: password}), {observe: 'response', headers});
	}

	public postRegister(user: UserRegistration): Observable<HttpResponse<any>> {
		const headers = this.getHeaders();
		return this.httpClient.post<any>(environment.apiEndpoint + 'user/register', user, {observe: 'response', headers});
	}

	public postReset(email: String): Observable<HttpResponse<any>> {
		return this.postEmail(environment.apiEndpoint + 'user/sendResetPasswordEmail', email);
	}

	public postResend(email: String): Observable<HttpResponse<any>> {
		return this.postEmail(environment.apiEndpoint + 'user/resendActivation', email);
	}

	private postEmail(url: string, email: String): Observable<HttpResponse<any>> {
		const headers = this.getHeaders();
		return this.httpClient.post<any>(url, email,
			{observe: 'response', headers});
	}

	public postUpdatePassword(password: String, token: String): Observable<HttpResponse<any>> {
		const headers = this.getHeaders();
		return this.httpClient.post<any>(environment.apiEndpoint + 'user/updatePassword/' + token, password,
			{observe: 'response', headers});
	}

	public postUpdatePasswordFirstLogin(password: String, authenticationToken: String): Observable<HttpResponse<any>> {
		const headers = this.getHeaders();
		return this.httpClient.post<any>(environment.apiEndpoint + 'user/activate/updatePassword/' +
			authenticationToken, password, {observe: 'response', headers});
	}

	public updateContext(context: Context) {

		this.post(context, environment.apiEndpoint + 'context').subscribe(
			() => {
				// Navigate to the home route
				this.router.navigate([this.returnUrl]);
			},
			() => {
				this.authenticationService.logout();
			});
	}

	public processInvitation(url: string): Observable<any> {
		const headers = this.getHeaders();
		return this.httpClient.get<any>(url, {headers});
	}

	private getHeaders(withAuth: boolean = false): HttpHeaders {
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}

		if (withAuth) {
			const token = sessionStorage.getItem('auth_token');
			return new HttpHeaders().set('Authorization', token)
				.set('Accept-Language', this.currentLang)
				.set('Access-Control-Allow-Origin', '*')
				.set('Access-Control-Allow-Credentials', 'true');
		}
		else {
			return new HttpHeaders().set('Accept-Language', this.currentLang)
				.set('Access-Control-Allow-Origin', '*')
				.set('Access-Control-Allow-Credentials', 'true');
		}
	}

}
