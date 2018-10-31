import {Component, Inject} from '@angular/core';

import {CompanyGroup, UrlParameter, UserRegistration, UserRegistrationType, UserRole} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
  moduleId: module.id,
  templateUrl: 'userDetails.component.html',
  selector: 'UserDetailsComponent'
})

export class UserDetailsComponent {
  public user: UserRegistration;
  addedByUserId: string;
  url: string;
  currentUser: any;
  showGroupSelectBox: boolean;
  rolesArray: UserRole[];
  groups: CompanyGroup[];
  action: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private httpService: HttpService,
    private dataService: DataService,
    private location: Location,
    private alertService: AlertService,
    private dialogRef: MatDialogRef<UserDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) data,
    private translate: TranslateService) {
      if (data.user == null){
          this.action = UrlParameter.NEW;
          this.user = new UserRegistration();
          this.addedByUserId = data.addedByUserId;
      }
      else{
          this.action = UrlParameter.EDIT;
          this.user = data.user;
          if (this.user.userRole === UserRole.SUPERUSER){
              this.showGroupSelectBox = true;
      }
          this.addedByUserId = data.addedByUserId;
      }
  }

  ngOnInit() {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.loadGroups();
  }

    userRoleKeys(): Array<string> {
        if (this.currentUser.userRole == UserRole.SUPERADMIN || this.currentUser.userRole == UserRole.ADMIN){
            // Return ADMIN, SUPERUSER and USER
            this.rolesArray = [UserRole.ADMIN, UserRole.SUPERUSER, UserRole.USER];
            return this.rolesArray;
        }
        else if (this.currentUser.userRole == UserRole.SUPERUSER){
            // Return USER
            this.rolesArray = [UserRole.USER];
            return this.rolesArray;
        }
    }

    onRoleChange(value: string){
      if (value === UserRole.SUPERUSER){
          this.showGroupSelectBox = true;
      }
      else if (value === UserRole.USER){
          this.showGroupSelectBox = false;
      }
      else{
          this.showGroupSelectBox = false;
      }
    }

    private loadGroups() {
        this.httpService.get(environment.apiEndpoint + 'users/group/user/' + this.currentUser.userID)
            .subscribe(
                data => {
                    this.groups = data;
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

	saveUser() {
        this.url = environment.apiEndpoint + 'users';
        if (this.action === UrlParameter.NEW) {
            this.user.registrationType = UserRegistrationType.ADDED_BY_USER;
            this.user.addedByUserId = this.addedByUserId;
        }
        else{
            this.user.registrationType = UserRegistrationType.UPDATE_USER_INFO;
        }

        console.log("USERRRR: " + JSON.stringify(this.user));

        this.httpService.post(this.user, this.url).subscribe(
          data => {
              this.dialogRef.close(true);
          },
          error => {
              this.dialogRef.close(error);
          });
  }
}
