import {Component} from '@angular/core';

import {Processor, Timeunit} from '../_models/index';

import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {Location, Time} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  templateUrl: 'networkProcessorConfigurationEdit.component.html'
})

export class NetworkProcessorConfigurationEditComponent {

  processor: Processor;
  id: string;
  private sub: any;
  splittedurl: any[];
  type: number;
  action: string;
  groupId: string;
  probeId: string;

  timeUnits = Timeunit;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private dataService: DataService,
    private translate: TranslateService,
    private router: Router,
    private route: ActivatedRoute,
    private location: Location
  ) {
    this.processor = new Processor();
  }

    extractTimeUnits(): Array<string> {
        const keys = Object.keys(this.timeUnits);
        return keys.slice();
    }

  ngOnInit() {

    this.route.queryParams.subscribe(params => {
      this.type = params['type'];
      this.action = params['action'];


      if (this.type == 3){
          this.groupId = params['groupId'];
      }
      else{
          this.probeId = params['probeId'];
      }

    });

    if (this.action === 'edit') {
      this.processor = this.dataService.get();
    }
  }

  saveProcessor() {

      if (this.action == 'new') {

          if (this.type == 3){
              this.processor.groupId = this.groupId;
          }
          else{
              this.processor.probeId = this.probeId;
          }
      }

    this.httpService.post(this.processor, environment.apiEndpoint + 'processors').subscribe(
      data => {
        this.processor = data;
        this.location.back();
      },
      error => {
          if (error.status == 0){
              this.alertService.error(this.translate.instant('server.notresponding'));
          }
          else{
              this.alertService.error(error.error.errorMessage);
          }
      });
  }

  cancel(){
      this.location.back();
  }
}
