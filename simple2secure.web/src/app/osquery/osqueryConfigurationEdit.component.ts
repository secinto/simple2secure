import {Component} from '@angular/core';
import {Location} from '@angular/common';
import {QueryRun, Timeunit} from '../_models/index';

import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  templateUrl: 'osqueryConfigurationEdit.component.html'
})

export class OsqueryConfigurationEditComponent {

  queryRun: QueryRun;
  id: string;
  private sub: any;
  splittedurl: any[];
  type: number;
  action: string;
  probeId: string;
  groupId: string;

  timeUnits = Timeunit;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private dataService: DataService,
    private router: Router,
    private translate: TranslateService,
    private route: ActivatedRoute,
    private location: Location
  ) {
    this.queryRun = new QueryRun();
  }

  ngOnInit() {

    this.route.queryParams.subscribe(params => {
      this.type = params['type'];
      this.action = params['action'];

      if (this.type == 1){
          this.groupId = params['groupId'];
      }
      else{
          this.probeId = params['probeId'];
      }

    });

    if (this.action === 'edit') {
      this.queryRun = this.dataService.get();
    }
  }

  extractTimeUnits(): Array<string> {
      const keys = Object.keys(this.timeUnits);
      return keys.slice();
  }

  saveQueryRun() {

    if (this.action == 'new') {
      if (this.type == 1) {
        if (!this.queryRun.groupId) {
          this.queryRun.groupId = this.groupId;
        }
      }
      else{
          this.queryRun.probeId = this.probeId;
        }
      }

    this.httpService.post(this.queryRun, environment.apiEndpoint + 'config/query').subscribe(
      data => {
        this.queryRun = data;
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
