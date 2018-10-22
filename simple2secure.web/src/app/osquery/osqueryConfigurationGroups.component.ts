import {Component, ViewChild} from '@angular/core';
import {CompanyGroup, CompanyGroupDTO} from '../_models/index';
import {ActivatedRoute, Router} from '@angular/router';
import {AlertService, DataService, HttpService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';

@Component({
  moduleId: module.id,
  templateUrl: 'osqueryConfigurationGroups.component.html'
})

export class OsqueryConfigurationGroupsComponent {

  currentUser: any;
  groups: CompanyGroupDTO[];
  loading = false;
  type: number;
  displayedColumns = ['name', 'owner', 'action'];
  dataSource = new MatTableDataSource();
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private httpService: HttpService,
    private alertService: AlertService,
    private dataService: DataService,
    private translate: TranslateService
  ) {}

  ngOnInit() {

  this.route.queryParams.subscribe(params => {
      this.type = params['type'];
  });

    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.loadGroups();
  }

  ngAfterViewInit() {
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }

  applyFilter(filterValue: string) {
      filterValue = filterValue.trim(); // Remove whitespace
      filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
      this.dataSource.filter = filterValue;
  }

  loadGroups() {
    this.loading = true;
    this.httpService.get(environment.apiEndpoint + 'users/groups/' + this.currentUser.userID)
      .subscribe(
      data => {
        this.groups = data;
        this.dataSource.data = this.groups;
        if (data.length > 0) {
          this.alertService.success(this.translate.instant('message.data'));
        }
        else {
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

  editGroup(item: any) {
    if (this.type == 3){
        this.router.navigate(['../type'], {relativeTo: this.route, queryParams: {type: this.type, groupId: item.id}});
    }
    else{
        this.router.navigate(['../details'], {relativeTo: this.route, queryParams: {type: 1, groupId: item.id}});
    }

  }

}
