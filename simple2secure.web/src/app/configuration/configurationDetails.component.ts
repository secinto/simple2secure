import {Component} from '@angular/core';

import {Config} from '../_models/index';
import {AlertService, HttpService} from '../_services/index';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';


@Component({
  moduleId: module.id,
  templateUrl: 'configurationDetails.component.html',
  styleUrls: ['configuration.component.css'],
  selector: 'configurationDetails'
})

export class ConfigurationDetailsComponent {
  config: Config;
  loading: boolean;

  constructor(
    private route: ActivatedRoute,
    private httpService: HttpService,
    private alertService: AlertService,
    private location: Location,
    private translate: TranslateService) {
        this.config = new Config();
        this.loading = false;
  }

  ngOnInit() {
    this.loadConfiguration();
  }

  private loadConfiguration() {
    this.loading = true;
    this.httpService.get(environment.apiEndpoint + 'config/')
      .subscribe(
      data => {
        this.config = data;
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

  saveConfig() {
    this.loading = true;

    this.httpService.post(this.config, environment.apiEndpoint + 'config').subscribe(
      data => {
        this.config = data;
        this.alertService.success(this.translate.instant('message.configuration.save'));
        this.loading = false;
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
