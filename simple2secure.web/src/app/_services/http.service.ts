import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse} from '@angular/common/http';
import {TranslateService} from '@ngx-translate/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {Context, User, UserRegistration} from '../_models';
import {catchError} from 'rxjs/operators';
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
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}

		const headers = new HttpHeaders().set('Authorization', localStorage.getItem('token'))
			.set('Accept-Language', this.currentLang)
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.get<any>(url, {headers});
	}

	public post(item: any, url: string): Observable<any> {
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}
		const headers = new HttpHeaders().set('Authorization', localStorage.getItem('token'))
			.set('Accept-Language', this.currentLang)
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.post<any>(url, item, {headers});
	}

	public delete(url: string): Observable<any> {
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}

		const headers = new HttpHeaders().set('Authorization', localStorage.getItem('token'))
			.set('Accept-Language', this.currentLang)
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.delete<any>(url, {headers});
	}

	public getFile(url: string): Observable<Blob> {
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}

		const headers = new HttpHeaders().set('Authorization', localStorage.getItem('token'))
			.set('Accept-Language', this.currentLang)
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.get<Blob>(url, {responseType: 'blob' as 'json', headers}).pipe();
	}

	public postLogin(username: string, password: string): Observable<HttpResponse<any>> {
		const headers = new HttpHeaders()
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.post<any>(environment.apiEndpoint + 'login',
			JSON.stringify({username: username, password: password}), {observe: 'response', headers});
	}

	public postRegister(user: UserRegistration): Observable<HttpResponse<any>> {
		const headers = new HttpHeaders()
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.post<any>(environment.apiEndpoint + 'user/register', user, {observe: 'response', headers});
	}

	public postReset(email: String): Observable<HttpResponse<any>> {
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}

		const headers = new HttpHeaders().set('Accept-Language', this.currentLang)
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.post<any>(environment.apiEndpoint + 'user/sendResetPasswordEmail', email,
			{observe: 'response', headers});
	}

	public postUpdatePassword(password: String, token: String): Observable<HttpResponse<any>> {
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}
		const headers = new HttpHeaders().set('Accept-Language', this.currentLang)
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.post<any>(environment.apiEndpoint + 'user/updatePassword/' + token, password,
			{observe: 'response', headers});
	}

	public postUpdatePasswordFirstLogin(password: String, authenticationToken: String): Observable<HttpResponse<any>> {
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}
		const headers = new HttpHeaders().set('Accept-Language', this.currentLang)
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');

		return this.httpClient.post<any>(environment.apiEndpoint + 'user/activate/updatePassword/' +
			authenticationToken, password, {observe: 'response', headers});
	}

	/*parseErrorBlob(err: HttpErrorResponse): Observable<any> {
		const reader: FileReader = new FileReader();

		const obs = Observable.create((observer: any) => {
			reader.onloadend = (e) => {
				observer.error(JSON.parse(reader.result));
				observer.complete();
			};
		});
		reader.readAsText(err.error);
		return obs;
	}*/

	public updateContext(context: Context, userId: string) {

		this.post(context, environment.apiEndpoint + 'context/' + userId).subscribe(
			() => {
				// Navigate to the home route
				this.router.navigate([this.returnUrl]);
			},
			() => {
				this.authenticationService.logout();
			});
	}

	public processInvitation(url: string): Observable<any> {
		this.currentLang = this.translate.currentLang;

		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}

		const headers = new HttpHeaders().set('Accept-Language', this.currentLang)
			.set('Access-Control-Allow-Origin', 'https://localhost:9000')
			.set('Access-Control-Allow-Credentials', 'true');
		return this.httpClient.get<any>(url, {headers});
	}


}
