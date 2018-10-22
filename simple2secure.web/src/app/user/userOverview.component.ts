import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {HttpService, AlertService, DataService} from '../_services/index';
import {saveAs as importedSaveAs} from 'file-saver';
import {CompanyGroup, User, UserRole} from '../_models/index';
import {UserDTO} from '../_models/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {ITreeState, ITreeOptions} from 'angular-tree-component';
import { v4 } from 'uuid';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  moduleId: module.id,
  templateUrl: 'userOverview.component.html',
  selector: 'userOverviewComponent'
})

export class UserOverviewComponent {
  users: any[];
  loading = false;
  userDeleted = false;
  groupDeleted = false;
  selectedItem: any;
  myProfile: any;
  url: string;
  id: string;
  public user: User;
  private sub: any;
  currentUser: any;
  showMyUsers: boolean;
  addNewGroup: boolean;
  showGroupTable: boolean;
  showUserTable: boolean;
  showProbeTable: boolean;
  showEditAndDelete: boolean;

  displayedColumnsUsers = ['email', 'userRole', 'group', 'action'];
  displayedColumnsDevices = ['probe', 'group', 'activated', 'action'];
  displayedColumnsGroups = ['name', 'owner', 'licenseExpirationDate', 'numOfUsedLicenses', 'action'];

  dataSource = new MatTableDataSource();
  dataSource2 = new MatTableDataSource();
  dataSource3 = new MatTableDataSource();

  @ViewChild('paginator') paginator: MatPaginator;
  @ViewChild('paginator2') paginator2: MatPaginator;
  @ViewChild('paginator3') paginator3: MatPaginator;
  @ViewChild('sort') sort: MatSort;
  @ViewChild('sortDev') sortDev: MatSort;
  @ViewChild('sortGrp') sortGrp: MatSort;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private httpService: HttpService,
    private dataService: DataService,
    private alertService: AlertService,
    private dialog: MatDialog,
    private translate: TranslateService) {
      this.myProfile = new UserDTO();
      this.myProfile.myProfile = new User();
  }

  ngOnInit() {
      this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
      this.loadMyProfile();
      if (this.currentUser.userRole == UserRole.SUPERADMIN || this.currentUser.userRole == UserRole.ADMIN ||
          this.currentUser.userRole == UserRole.SUPERUSER){
      	this.showMyUsers = true;
      	this.addNewGroup = true;
      }
      else{
      	this.showMyUsers = false;
      	this.addNewGroup = false;
      }
  }

  ngAfterViewInit() {
      this.dataSource.paginator = this.paginator;
      this.dataSource2.paginator = this.paginator2;
      this.dataSource3.paginator = this.paginator3;
      this.dataSource.sort = this.sort;
      this.dataSource2.sort = this.sortDev;
      this.dataSource3.sort = this.sortGrp;
  }

  applyFilter(filterValue: string) {
      filterValue = filterValue.trim(); // Remove whitespace
      filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
      this.dataSource.filter = filterValue;
  }

  applyFilterDev(filterValue: string) {
      filterValue = filterValue.trim(); // Remove whitespace
      filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
      this.dataSource2.filter = filterValue;
  }

  applyFilterGrp(filterValue: string) {
      filterValue = filterValue.trim(); // Remove whitespace
      filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
      this.dataSource3.filter = filterValue;
  }

  private loadMyProfile() {
      this.loading = true;
      this.httpService.get(environment.apiEndpoint + 'users/' + this.currentUser.userID)
      .subscribe(
      data => {
        this.myProfile = data;
        this.dataSource.data = this.myProfile.myUsersList;
        this.dataSource2.data = this.myProfile.myProfile.myProbes;
        this.dataSource3.data = this.myProfile.myGroups;
        this.checkMyGroupSize(this.myProfile.myGroups);
        this.checkMyUsersSize(this.myProfile.myUsersList);
        this.checkMyProbesSize(this.myProfile.myProfile.myProbes);
        this.dataService.setGroups(this.myProfile.myGroups);

        if (!this.userDeleted && !this.groupDeleted){
            this.alertService.success(this.translate.instant('message.user'));
        }

        this.groupDeleted = false;
        this.userDeleted = false;
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

  checkMyGroupSize(groups: any){
      if (groups.length > 0){
          this.showGroupTable = true;
      }
      else{
          this.showGroupTable = false;
      }
  }

    checkMyUsersSize(users: any){
        if (users.length > 0){
            this.showUserTable = true;
        }
        else{
            this.showUserTable = false;
        }
    }

    checkMyProbesSize(probes: any){
        if (probes.length > 0){
            this.showProbeTable = true;
        }
        else{
            this.showProbeTable = false;
        }
    }

    public download(){
        this.loading = true;
        this.httpService.getFile(environment.apiEndpoint + 'download')
            .subscribe(
                data  => {
                    importedSaveAs(data, 's2s_setup.exe');
                    this.loading = false;
                },
                error => {
                    this.alertService.error(error.errorMessage);
                    this.loading = false;
                });
    }


  saveUser() {
      this.loading = true;

      if (this.id === 'new') {
          this.url = environment.apiEndpoint + 'users/add_by_user-' + this.currentUser.userID;
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
                    this.router.navigate(['user']);
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
      else{
          this.url = environment.apiEndpoint + 'users/update_user_info';
          this.httpService.post(this.myProfile.myProfile, this.url).subscribe(
                  data => {
                    this.user = data;
                    if (this.id === 'new') {
                      this.alertService.success(this.translate.instant('message.user.create'));
                    }
                    else {
                      this.alertService.success(this.translate.instant('message.user.update'));
                    }
                    this.router.navigate(['user']);
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

  public editUser(userItem: any) {
    this.dataService.set(userItem);
    this.router.navigate(['../user', userItem.id], {relativeTo: this.route});
  }

  public editGroup(groupItem: any) {
      this.dataService.set(groupItem);
      this.router.navigate(['../user/group', groupItem.id], {relativeTo: this.route});
    }

  public deleteUser(user: any) {
    this.loading = true;
    this.httpService.delete(environment.apiEndpoint + 'users/' + user.id).subscribe(
      data => {
        this.alertService.success(this.translate.instant('message.user.delete'));
        this.userDeleted = true;
        this.loadMyProfile();
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

  public deleteGroup(group: any) {
    this.loading = true;
    this.httpService.delete(environment.apiEndpoint + 'users/group/' + group.id).subscribe(
      data => {
        this.alertService.success(this.translate.instant('message.group.delete'));
        this.loadMyProfile();
        this.groupDeleted = true;
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

  public onMenuTriggerClick(item: any) {
      this.selectedItem = item;

      if (item.addedByUserId == this.currentUser.userID){
          this.showEditAndDelete = true;
      }
      else{
          this.showEditAndDelete = false;
      }
  }

  public onGroupEditClick(){
      this.editGroup(this.selectedItem);
  }

  public onDeleteGroupClick() {
      this.openDialog('group');
  }

  public onEditClick(){
      this.editUser(this.selectedItem);
  }

  public onDeleteClick(){
      this.openDialog('user');
      // this.deleteUser(this.selectedUser);
  }


  public onDownloadClick(){
      this.loading = true;
      this.httpService.getFile(environment.apiEndpoint + 'license/' + this.selectedItem.id + '/' +  this.currentUser.userID)
        .subscribe(
        data  => {
          importedSaveAs(data, 'license-' + this.selectedItem.id + '.zip');
          this.loading = false;
        },
        error => {
          this.alertService.error(error.errorMessage);
          this.loading = false;
        });
    }

  public openDialog(type: string){
      const dialogConfig = new MatDialogConfig();

      dialogConfig.disableClose = true;
      dialogConfig.autoFocus = true;

      if (type == 'user'){
          dialogConfig.data = {
              id: 1,
              title: this.translate.instant('message.areyousure'),
              content: this.translate.instant('message.user.dialog')
          };
      }
      else{
          dialogConfig.data = {
              id: 1,
              title: this.translate.instant('message.areyousure'),
              content: this.translate.instant('message.group.dialog')
          };
      }


      const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

      dialogRef.afterClosed().subscribe(data => {
          if (data === true){
              if (type == 'user'){
                  this.deleteUser(this.selectedItem);
              }
              else if (type == 'group'){
                  this.deleteGroup(this.selectedItem);
              }
          }
        });
  }

  _setDataSource(indexNumber) {
      setTimeout(() => {
        switch (indexNumber) {
          case 1:
            !this.dataSource.paginator ? this.dataSource.paginator = this.paginator : null;
            !this.dataSource.sort ? this.dataSource.sort = this.sort : null;
            break;
          case 2:
              !this.dataSource3.paginator ? this.dataSource3.paginator = this.paginator3 : null;
              !this.dataSource3.sort ? this.dataSource3.sort = this.sortGrp : null;
              break;
          case 3:
            !this.dataSource2.paginator ? this.dataSource2.paginator = this.paginator2 : null;
            !this.dataSource2.sort ? this.dataSource2.sort = this.sortDev : null;
        }
      });
    }
}
