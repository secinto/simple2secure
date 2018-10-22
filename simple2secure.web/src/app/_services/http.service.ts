import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse} from '@angular/common/http';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {User} from '../_models';
import {Response} from '@angular/http';
import {catchError} from 'rxjs/operators';

@Injectable()
export class HttpService {
  constructor(protected httpClient: HttpClient,
              private translate: TranslateService) { }

    currentLang: string;

    public get(url: string): Observable<any> {
        this.currentLang = this.translate.currentLang;

        if (!this.currentLang){
            this.currentLang = this.translate.defaultLang;
        }

        const headers = new HttpHeaders().set('Authorization', localStorage.
        getItem('token')).set('Accept-Language', this.currentLang);
        return this.httpClient.get<any>(url, {headers});
    }

    public post(item: any, url: string): Observable<any> {
        this.currentLang = this.translate.currentLang;

        if (!this.currentLang){
            this.currentLang = this.translate.defaultLang;
        }

        const headers = new HttpHeaders().set('Authorization', localStorage.
        getItem('token')).set('Accept-Language', this.currentLang);
        return this.httpClient.post<any>(url, item, {headers});
    }

    public delete(url: string): Observable<any> {
        this.currentLang = this.translate.currentLang;

        if (!this.currentLang){
            this.currentLang = this.translate.defaultLang;
        }

        const headers = new HttpHeaders().set('Authorization', localStorage.
        getItem('token')).set('Accept-Language', this.currentLang);
        return this.httpClient.delete<any>(url, {headers});
    }

    public getFile(url: string): Observable<Blob> {
        this.currentLang = this.translate.currentLang;

        if (!this.currentLang){
            this.currentLang = this.translate.defaultLang;
        }

        const headers = new HttpHeaders().set('Authorization', localStorage.
        getItem('token')).set('Accept-Language', this.currentLang);
        return this.httpClient.get<Blob>(url, {responseType : 'blob' as 'json', headers}).pipe(catchError(this.parseErrorBlob));
    }

    public postLogin(username: string, password: string): Observable<HttpResponse<any>> {
        return this.httpClient.post<any>(environment.apiEndpoint + 'login',
            JSON.stringify({ username: username, password: password }), { observe: 'response' });
    }

    public postRegister(user: User, type: String): Observable<HttpResponse<any>> {
        return this.httpClient.post<any>(environment.apiEndpoint + 'register/' +
            type, user, { observe: 'response' });
    }

    public postReset(email: String): Observable<HttpResponse<any>> {
        this.currentLang = this.translate.currentLang;

        if (!this.currentLang){
            this.currentLang = this.translate.defaultLang;
        }

        const headers = new HttpHeaders().set('Accept-Language', this.currentLang);
        return this.httpClient.post<any>(environment.apiEndpoint + 'users/sendResetPasswordEmail', email,
            { observe: 'response', headers });
    }

    public postUpdatePassword(password: String, token: String): Observable<HttpResponse<any>> {
        this.currentLang = this.translate.currentLang;

        if (!this.currentLang){
            this.currentLang = this.translate.defaultLang;
        }
        const headers = new HttpHeaders().set('Accept-Language', this.currentLang);
        return this.httpClient.post<any>(environment.apiEndpoint + 'users/updatePassword/' + token, password,
            { observe: 'response', headers });
    }

    public postUpdatePasswordFirstLogin(password: String, authenticationToken: String): Observable<HttpResponse<any>> {
        this.currentLang = this.translate.currentLang;

        if (!this.currentLang){
        this.currentLang = this.translate.defaultLang;
        }
        const headers = new HttpHeaders().set('Accept-Language', this.currentLang);

        return this.httpClient.post<any>(environment.apiEndpoint + 'users/activate/updatePassword/' +
            authenticationToken, password, { observe: 'response', headers });
    }

    parseErrorBlob(err: HttpErrorResponse): Observable<any> {
        const reader: FileReader = new FileReader();

        const obs = Observable.create((observer: any) => {
            reader.onloadend = (e) => {
                observer.error(JSON.parse(reader.result));
                observer.complete();
            };
        });
        reader.readAsText(err.error);
        return obs;
    }
}
