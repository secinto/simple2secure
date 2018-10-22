import {Component} from '@angular/core';

import {User} from '../_models/index';

import {Router} from '@angular/router';
import {AlertService, HttpService} from '../_services/index';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  styleUrls: ['register.component.css'],
  templateUrl: 'register.component.html'
})

export class RegisterComponent {
  user: User;
  loading: boolean;

  constructor(
    private router: Router,
    private httpService: HttpService,
    private alertService: AlertService,
    private translate: TranslateService) {
    this.user = new User();
    this.loading = false;
  }

  register() {
    this.loading = true;
    this.httpService.postRegister(this.user, 'standard')
      .subscribe(
      data => {
        this.alertService.success(this.translate.instant('message.registration'), true);
        this.loading = false;
        setTimeout((router: Router) => {
            this.router.navigate(['/login']);
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
