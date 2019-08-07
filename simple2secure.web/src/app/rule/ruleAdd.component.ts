import {Component, ViewChild} from '@angular/core';
import {FrontendRule, User} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy, Location} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  styleUrls: ['rule.component.css'],
  templateUrl: 'ruleAdd.component.html',
  selector: 'addRule'
})
export class RuleAddComponent {

    public rule: any;
    currentUser: any;
    loading = false;
    toolId: string;
    operators = ['==', '<', '<=', '>', '>=', '&&', '||', 'contains'];
    toolItems = ['subject', 'body'];
    action: string;

    constructor(
            private route: ActivatedRoute,
            private router: Router,
            private httpService: HttpService,
            private alertService: AlertService,
            private dataService: DataService,
            private url: LocationStrategy,
            private translate: TranslateService,
            private location: Location) {}

    ngOnInit() {
      this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
      this.route.queryParams.subscribe(params => {
        this.action = params['action'];
      });


      if (this.action === 'edit') {
          this.rule = this.dataService.get();
      }
      else{
          this.rule = new FrontendRule();
          this.toolId = this.route.snapshot.paramMap.get('id');
          this.rule.userId = this.currentUser.userID;
          this.rule.toolId = this.toolId;
      }


    }

    saveRule(){
      this.httpService.post(this.rule, environment.apiEndpoint + 'rule').subscribe(
      data => {
        this.rule = data;
        this.alertService.success(this.translate.instant('message.rule.add'));
        this.location.back();
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
