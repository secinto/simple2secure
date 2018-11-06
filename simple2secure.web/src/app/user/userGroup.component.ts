import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CompanyGroup, UserRole} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {MatDialog} from '@angular/material';

@Component({
  moduleId: module.id,
  templateUrl: 'userGroup.component.html',
  selector: 'UserGroupComponent',
})

export class UserGroupComponent implements OnInit{

  group = new CompanyGroup();
  loading = false;
  id: string;
  private sub: any;
  url: string;
  currentUser: any;
  groupEditable: boolean;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private httpService: HttpService,
    private dataService: DataService,
    private location: Location,
    private dialog: MatDialog,
    private alertService: AlertService,
    private translate: TranslateService) {
  }

  ngOnInit() {
      this.sub = this.route.params.subscribe(params => {
          this.id = params['id'];
      });

      this.currentUser = JSON.parse(localStorage.getItem('currentUser'));

      this.groupEditable = this.dataService.isGroupEditable();
      this.loadGroup();
  }

  public loadGroup() {
      this.loading = true;
      this.httpService.get(environment.apiEndpoint + 'users/group/' + this.id)
          .subscribe(
              data => {
                  this.group = data;
                  if (this.group){
                      this.alertService.success(this.translate.instant('message.data'));
                  }
                  else{
                      this.alertService.error(this.translate.instant('message.data.notProvided'));
                  }
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

  saveGroup() {
    this.loading = true;

    this.url = environment.apiEndpoint + 'users/group/' + this.currentUser.userID + '/' + 'null';
    this.httpService.post(this.group, this.url).subscribe(
      data => {
          this.group = data;

        if (this.id === 'new') {
          this.alertService.success(this.translate.instant('message.user.create'));
        }
        else {
          this.alertService.success(this.translate.instant('message.user.update'));
        }
        this.cancel();
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

  cancel(){
      this.location.back();
  }
}
