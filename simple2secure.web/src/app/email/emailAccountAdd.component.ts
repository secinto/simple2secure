import {Component, ViewChild} from '@angular/core';
import {Context, ContextDTO, EmailConfiguration, User} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
@Component({
  moduleId: module.id,
  styleUrls: ['email.component.css'],
  templateUrl: 'emailAccountAdd.component.html',
  selector: 'emailAccountAdd'
})
export class EmailAccountAddComponent {

    public config: EmailConfiguration;
    loading = false;
    context: ContextDTO;

    constructor(
            private route: ActivatedRoute,
            private router: Router,
            private httpService: HttpService,
            private alertService: AlertService,
            private dataService: DataService,
            private url: LocationStrategy,
            private translate: TranslateService) {}

    ngOnInit() {

      this.context = JSON.parse(localStorage.getItem('context'));
      if (this.router.url.includes('edit')){
          this.config = this.dataService.get();
      }
      else{
          this.config = new EmailConfiguration();
          this.config.contextId = this.context.context.id;
      }
    }
    saveConfig(){
      this.httpService.post(this.config, environment.apiEndpoint + 'email').subscribe(
      data => {
        this.config = data;
        this.alertService.success(this.translate.instant('message.email'));
        this.router.navigate(['email']);
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
