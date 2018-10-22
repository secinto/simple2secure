import {Component} from '@angular/core';

import {Step, Processor} from '../_models/index';

import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  templateUrl: 'networkStepConfigurationEdit.component.html'
})

export class NetworkStepConfigurationEditComponent {

  step: Step;
  id: string;
  type: number;
  action: string;
  groupId: string;
  probeId: string;
  processors: Processor[];


  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private dataService: DataService,
    private router: Router,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private location: Location
  ) {
    this.step = new Step();
  }

  ngOnInit() {

    this.route.queryParams.subscribe(params => {
      this.type = params['type'];

      if (this.type == 3){
          this.groupId = params['groupId'];
          this.getProcessorsByGroupId();
      }
      else{
          this.probeId = params['probeId'];
          this.getProcessorsByProbeId();
      }

      this.action = params['action'];
    });

    if (this.action === 'edit') {
      this.step = this.dataService.get();
    }
  }

  saveStep() {

    if (this.action == 'new') {

        if (this.type == 3){
            this.step.groupId = this.groupId;
        }
        else{
            this.step.probeId = this.probeId;
        }
    }

    this.httpService.post(this.step, environment.apiEndpoint + 'steps').subscribe(
      data => {
        this.step = data;
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

  getProcessorsByGroupId(){
      this.httpService.get(environment.apiEndpoint + 'processors/group/' + this.groupId)
          .subscribe(
              data => {
                  this.processors = data;

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

  getProcessorsByProbeId(){
      this.httpService.get(environment.apiEndpoint + 'processors/' + this.probeId)
          .subscribe(
              data => {
                  this.processors = data;
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
