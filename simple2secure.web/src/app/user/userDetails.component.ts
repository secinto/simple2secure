import {Component} from '@angular/core';

import {CompanyGroup, User, UserRole} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';

@Component({
  moduleId: module.id,
  templateUrl: 'userDetails.component.html',
  selector: 'UserDetailsComponent'
})

export class UserDetailsComponent {
  public user: User;
  loading = false;
  id: string;
  private sub: any;
  url: string;
  currentUser: any;
  rolesArray: UserRole[];
  groups: CompanyGroup[];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private httpService: HttpService,
    private dataService: DataService,
    private location: Location,
    private alertService: AlertService,
    private translate: TranslateService) {
    this.user = new User();
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
    });

    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.groups = this.dataService.getGroups();

    if (this.id != 'new') {
        this.user = this.dataService.get();
    }
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

	saveUser() {
    this.loading = true;
    this.url = environment.apiEndpoint + 'users/update_user_info';
    if (this.id === 'new') {
        this.url = environment.apiEndpoint + 'users/add_by_user-' + this.currentUser.userID;
    }

    this.httpService.post(this.user, this.url).subscribe(
      data => {
        this.user = data;
        if (this.id === 'new') {
          this.alertService.success(this.translate.instant('message.user.create'));
        }
        else {
          this.alertService.success(this.translate.instant('message.user.update'));
        }
        this.loading = false;
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
