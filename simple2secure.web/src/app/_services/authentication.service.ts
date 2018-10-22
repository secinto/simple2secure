import { Injectable } from '@angular/core';
import 'rxjs/add/operator/map';
import { JwtHelper } from 'angular2-jwt';
import {environment} from '../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable()
export class AuthenticationService {

    jwtHelper: JwtHelper = new JwtHelper();

    constructor(private httpClient: HttpClient) { }

    logout() {
        // remove user from local storage to log user out
        localStorage.removeItem('token');
        localStorage.removeItem('currentUser');
    }

    public isAuthenticated(): boolean {
        const token = localStorage.getItem('token');
        if (token){
            return !this.jwtHelper.isTokenExpired(token);
        }
        else{
            return false;
        }

    }
}
