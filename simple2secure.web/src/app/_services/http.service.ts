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

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from './authentication.service';
import { DataService } from './data.service';
import { Context } from '../_models/context';
import { UserRegistration } from '../_models/userRegistration';
import { MatDialog } from '@angular/material/dialog';
import { TokenObject } from '../_models/tokenObject';

@Injectable()
export class HttpService {

    returnUrl: string;

    constructor(private route: ActivatedRoute,
        protected httpClient: HttpClient,
        private translate: TranslateService,
        private router: Router,
        private dataService: DataService,
        private authService: AuthenticationService,
        private dialogRef: MatDialog) {
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }

    currentLang: string;
    tokenObj = new TokenObject();

    public get(url: string): Observable<any> {
        const headers = this.getHeaders(true);

        return this.httpClient.get<any>(url, { headers });
    }

    public getWithoutAuth(url: string): Observable<any> {
        const headers = this.getHeaders(false);

        return this.httpClient.get<any>(url, { headers });
    }

    public getWithParams(url: string, params: HttpParams): Observable<any> {
        const headers = this.getHeaders(true);

        return this.httpClient.get<any>(url, { headers, params });
    }

    public post(item: any, url: string, withAuth: boolean = true): Observable<any> {
        const headers = this.getHeaders(withAuth);

        return this.httpClient.post<any>(url, item, { headers });
    }

    public postWithParams(item: any, url: string, params: HttpParams): Observable<any> {
        const headers = this.getHeaders(true);

        return this.httpClient.post<any>(url, item, { headers, params });
    }

    public delete(url: string): Observable<any> {
        const headers = this.getHeaders(true);

        return this.httpClient.delete<any>(url, { headers });
    }

    public getFile(url: string): Observable<Blob> {
        const headers = this.getHeaders(true);

        return this.httpClient.get<Blob>(url, { responseType: 'blob' as 'json', headers }).pipe();
    }

    public postLogin(item: any, url: string): Observable<any> {
        const headers = this.getHeaders(false);
        return this.httpClient.post<any>(url,
            item, { headers });
    }

    public postRegister(user: UserRegistration): Observable<HttpResponse<any>> {
        const headers = this.getHeaders();
        return this.httpClient.post<any>(environment.apiUserRegister, user, {
            observe: 'response',
            headers
        });
    }

    public postReset(user: UserRegistration): Observable<any> {
        const headers = this.getHeaders();
        return this.httpClient.post<any>(environment.apiUserForgotPassword, user, { headers });
    }

    public postResend(email: String): Observable<HttpResponse<any>> {
        return this.postEmail(environment.apiUserResendActivation, email);
    }

    private postEmail(url: string, email: String): Observable<HttpResponse<any>> {
        const headers = this.getHeaders();
        return this.httpClient.post<any>(url, email,
            { observe: 'response', headers });
    }

    public updateContext(context: Context) {

        this.post(context, environment.apiContext).subscribe(
            () => {
                // Navigate to the home route
                this.router.navigate([this.returnUrl]);
            },
            () => {
                this.logout();
            });
    }

    public processInvitation(url: string): Observable<any> {
        const headers = this.getHeaders();
        return this.httpClient.get<any>(url, { headers });
    }

    private getHeaders(withAuth: boolean = false): HttpHeaders {
        this.currentLang = this.translate.currentLang;

        if (!this.currentLang) {
            this.currentLang = this.translate.defaultLang;
        }

        if (withAuth) {
            const token = this.dataService.getAuthToken();

            return new HttpHeaders().append('Authorization', 'Bearer ' + token)
                .set('Accept-Language', this.currentLang)
                .set('Access-Control-Allow-Origin', '*')
                .set('Access-Control-Allow-Credentials', 'true');
        } else {
            return new HttpHeaders().set('Accept-Language', this.currentLang)
                .set('Access-Control-Allow-Origin', '*')
                .set('Access-Control-Allow-Credentials', 'true');
        }
    }

    logout() {

        const token = this.dataService.getAuthToken();
        this.tokenObj.token = token;

        // logout from the portal


        this.post(this.tokenObj, environment.apiUserLogout, false)
            .subscribe(
                data => {
                },
                error => {
                });

        // clear the sessionStorage
        this.dataService.clearSessionStorage();
        // close all open dialogs
        this.dialogRef.closeAll();
        this.router.navigate(['login']);
        this.authService.isLoggedIn = false;

    }

}
