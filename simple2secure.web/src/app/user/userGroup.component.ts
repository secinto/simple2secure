import {Component} from '@angular/core';
import {CompanyGroup} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {DatePipe} from '@angular/common';

@Component({
  moduleId: module.id,
  templateUrl: 'userGroup.component.html',
  selector: 'UserGroupComponent',
  providers: [DatePipe]
})

export class UserGroupComponent {
  public group: CompanyGroup;
  loading = false;
  id: string;
  private sub: any;
  url: string;
  currentUser: any;
  selectedDate: string;
  tempDate: Date;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private httpService: HttpService,
    private dataService: DataService,
    private location: Location,
    private alertService: AlertService,
    private translate: TranslateService,
    private datePipe: DatePipe) {
        this.group = new CompanyGroup();
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
    });

    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));

    if (this.id === 'newGroup') {

    }
    else {
      this.loadGroup();
      this.tempDate = new Date(this.group.licenseExpirationDate + ' ');
      this.selectedDate = this.datePipe.transform(this.tempDate, 'yyyy-MM-dd', 'Europe/Vienna');
    }
  }

  private loadGroup() {
      this.group = this.dataService.get();
  }

  saveGroup() {
    this.loading = true;
    this.url = environment.apiEndpoint + 'users/group';
    if (this.id === 'newGroup') {
        this.group.addedByUserId = this.currentUser['userID'];
    }
    this.group.licenseExpirationDate = this.datePipe.transform(this.selectedDate, 'MM/dd/yyyy', 'Europe/Vienna');
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
