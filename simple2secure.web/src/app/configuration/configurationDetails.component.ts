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
  styleUrls: ['configurationDetails.component.css'],
  selector: 'configurationDetails'
})

export class ConfigurationDetailsComponent {
  config: Config;
  loading: boolean;
  id: string;
  private sub: any;

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
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
    });

    this.loadConfiguration();
  }

  private loadConfiguration() {
    this.loading = true;
    this.httpService.get(environment.apiEndpoint + 'configs/' + this.id)
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
      this.cancel();
  }

  public addNewAPI() {
    if (this.config['apis'] == null) {
      this.config['apis'] = [{id: '', name: '', url: ''}];
    }
    else {
      this.config['apis'].push({id: '', name: '', url: ''});
    }
  }

  cancel(){
      this.location.back();
  }
}
