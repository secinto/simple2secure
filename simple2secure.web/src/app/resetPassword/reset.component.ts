import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AlertService, HttpService} from '../_services/index';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  styleUrls: ['reset.component.css'],
  templateUrl: 'reset.component.html'
})

export class ResetComponent {
  resetEmail: string;
  loading: boolean;

    constructor(
        private router: Router,
        private httpService: HttpService,
        private alertService: AlertService,
        private translate: TranslateService) {
            this.loading = false;
    }

    reset(){
        this.loading = true;
        this.httpService.postReset(this.resetEmail)
            .subscribe(
                data => {
                    this.alertService.success(this.translate.instant('message.passwordReset'), true);
                    setTimeout((router: Router) => {
                        this.router.navigate(['/login']);
                        this.loading = false;
                    }, 3000);
                },
                error => {

                    if (error.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(error.error.errorMessage);
                    }

                    this.loading = false;
                });
    }
}
