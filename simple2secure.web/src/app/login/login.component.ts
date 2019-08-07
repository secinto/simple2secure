import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import {AlertService, AuthenticationService, HttpService} from '../_services/index';
import {TranslateCompiler, TranslateService} from '@ngx-translate/core';
import {JwtHelper} from 'angular2-jwt';

@Component({
    moduleId: module.id,
	styleUrls: ['login.component.css'],
    templateUrl: 'login.component.html'
})

export class LoginComponent implements OnInit {
    model: any = {};
    loading = false;
    returnUrl: string;
    hide: boolean;
    jwtHelper: JwtHelper = new JwtHelper();

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private translate: TranslateService,
        private authenticationService: AuthenticationService,
        private httpService: HttpService,
        private alertService: AlertService) { }

    ngOnInit() {

        this.hide = true;

        // reset login status
        this.authenticationService.logout();

        // get return url from route parameters or default to '/'
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }

    login() {
        this.loading = true;
        this.httpService.postLogin(this.model.username, this.model.password)
            .subscribe(
                response => {
                    const decodedToken = this.jwtHelper.decodeToken(response.headers.get('Authorization'));
                    const user_uuid = decodedToken.userID;
                    const user_role = decodedToken.userRole;
                    localStorage.setItem('token', response.headers.get('Authorization'));
                    localStorage.setItem('currentUser', JSON.stringify({ firstName: this.model.username, token: response.headers.get('Authorization'), userID: user_uuid, userRole: user_role }));
                    this.router.navigate([this.returnUrl]);
                },
                error => {
                    if (error.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                        this.loading = false;
                    }
                    else{
                        this.alertService.error(error.error.errorMessage);
                    }
                    this.loading = false;

                });
    }

    showPassword(){
        if (this.hide){
            this.hide = false;
        }
        else{
            this.hide = true;
        }
    }

    success(message: string) {
        this.alertService.success(message);
    }

    error(message: string) {
        this.alertService.error(message);
    }

    info(message: string) {
        this.alertService.info(message);
    }

    warn(message: string) {
        this.alertService.warn(message);
    }

    clear() {
        this.alertService.clear();
    }
}
