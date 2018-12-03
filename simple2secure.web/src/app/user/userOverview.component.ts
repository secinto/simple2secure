import {Component, ViewChild} from '@angular/core';
import {MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {AlertService, DataService, HttpService} from '../_services/index';
import {saveAs as importedSaveAs} from 'file-saver';
import {Context, CompanyGroup, User, UserDTO, UserRole} from '../_models/index';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {v4} from 'uuid';
import {UserGroupDialogComponent} from './userGroupDialog.component';
import {HttpErrorResponse} from '@angular/common/http';
import {UserDetailsComponent} from './userDetails.component';
import {UserGroupApplyConfigComponent} from './userGroupApplyConfig.component';
import {UserProbeChangeGroupComponent} from './userProbeChangeGroup.component';

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
  probeDeleted = false;
  groupAdded = false;
  userAdded = false;
  selectedItem: any;
  myProfile: any;
  moveNotPossible = false;
  url: string;
  id: string;
  public user: User;
  private sub: any;
  currentUser: any;
  context: Context;
  showMyUsers: boolean;
  addNewGroup: boolean;
  showGroupTable: boolean;
  showUserTable: boolean;
  showProbeTable: boolean;
  showEditAndDelete: boolean;
  isGroupDeletable = false;

  displayedColumnsUsers = ['email', 'userRole', 'action'];
  displayedColumnsDevices = ['probe', 'group', 'activated', 'action'];

  dataSource = new MatTableDataSource();
  dataSource2 = new MatTableDataSource();
  dataSource3 = new MatTableDataSource();
  options = { focused: true, allowDrag: true};

  @ViewChild('paginator') paginator: MatPaginator;
  @ViewChild('paginator2') paginator2: MatPaginator;
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
    private dialog2: MatDialog,
    private translate: TranslateService) {
      this.myProfile = new UserDTO();
      this.myProfile.myProfile = new User();
  }

  ngOnInit() {
      this.selectedItem = new CompanyGroup();
      this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
      this.context = JSON.parse(localStorage.getItem('context'));
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
      this.dataSource.sort = this.sort;
      this.dataSource2.sort = this.sortDev;
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

  private loadMyProfile() {
      this.loading = true;
      this.httpService.get(environment.apiEndpoint + 'users/' + this.currentUser.userID + '/' + this.context.id)
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
        if (!this.userDeleted && !this.groupDeleted && !this.probeDeleted && !this.groupAdded && !this.userAdded && !this.moveNotPossible){
            this.alertService.success(this.translate.instant('message.user'));
        }

        this.moveNotPossible = false;
        this.groupDeleted = false;
        this.probeDeleted = false;
        this.groupAdded = false;
        this.userAdded = false;
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


  updateUserInfo() {
      this.loading = true;
      this.url = environment.apiEndpoint + 'users/update';
      this.httpService.post(this.myProfile.myProfile, this.url).subscribe(
              data => {
                  this.user = data;
                  this.alertService.success(this.translate.instant('message.user.update'));
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
    this.httpService.delete(environment.apiEndpoint + 'group/' + group.id).subscribe(
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

    public deleteProbe(probe: any) {
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'probe/deleteProbe/' + probe.probeId).subscribe(
            data => {
                this.alertService.success(this.translate.instant('message.probe.delete"'));
                this.loadMyProfile();
                this.probeDeleted = true;
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

      if (this.currentUser.userRole === UserRole.SUPERADMIN || this.currentUser.userRole === UserRole.ADMIN){
          this.showEditAndDelete = true;
          this.dataService.setGroupEditable(this.showEditAndDelete);
      }
      else{
          if (this.currentUser.userRole === UserRole.SUPERUSER){
              this.showEditAndDelete = this.checkIfUserCanEditGroup(item);
              this.dataService.setGroupEditable(this.showEditAndDelete);
          }
          else{
              this.showEditAndDelete = false;
              this.dataService.setGroupEditable(false);
          }

      }

      if (item.standardGroup){
          this.isGroupDeletable = false;
      }
      else{
          this.isGroupDeletable = true;
      }
  }

    checkIfUserCanMoveGroup(fromGroup: CompanyGroup, toGroup: CompanyGroup){

        if (this.currentUser.userRole == UserRole.SUPERADMIN || this.currentUser.userRole == UserRole.ADMIN){
            return true;
        }
        else if (this.currentUser.userRole == UserRole.SUPERUSER){
            if (fromGroup && toGroup){
                if (fromGroup.superUserIds && toGroup.superUserIds){
                    if (fromGroup.superUserIds.indexOf(this.currentUser.userID) > -1 && toGroup.superUserIds.indexOf(this.currentUser.userID) > -1){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
                else{
                    if(toGroup.id){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            }
            else if (fromGroup && !toGroup){
                if (fromGroup.superUserIds){
                    if (fromGroup.superUserIds.indexOf(this.currentUser.userID) > -1){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
                else{
                    return false;
                }
            }
            }
            else{
                return false;
            }
    }

  checkIfUserCanEditGroup(group: CompanyGroup){
      if (this.currentUser.userRole == UserRole.SUPERADMIN || this.currentUser.userRole == UserRole.ADMIN){
          return true;
      }
      else if (this.currentUser.userRole == UserRole.SUPERUSER){
          if (group){
              if (group.superUserIds){
                  if (group.superUserIds.indexOf(this.currentUser.userID) > -1){
                      return true;
                  }
                  else{
                      return false;
                  }
              }
              else{
                  return false;
              }

          }
          else{
              return false;
          }
      }
      else{
          return false;
      }
  }

  public onGroupEditClick(){
      this.editGroup(this.selectedItem);
  }

  public onDeleteGroupClick() {
      this.openDialog('group');
  }

  public onDeleteClick(){
      this.openDialog('user');
      // this.deleteUser(this.selectedUser);
  }

  onProbeDeleteClick(){
      this.openDialog('probe');
  }

  public onApplyConfigClick(){
      const dialogConfig = new MatDialogConfig();
      dialogConfig.width = '450px';
      dialogConfig.data = {
          destGroup: this.selectedItem,
      };

      const dialogRef = this.dialog.open(UserGroupApplyConfigComponent, dialogConfig);

      dialogRef.afterClosed().subscribe(result => {
          if (result == true){
              this.alertService.success(this.translate.instant('message.config.group.update'));
              this.userAdded = true;
          }
          else{
              if (result instanceof HttpErrorResponse){
                  if (result.status == 0){
                      this.alertService.error(this.translate.instant('server.notresponding'));
                  }
                  else{
                      this.alertService.error(result.error.errorMessage);
                  }
              }
          }
      });
  }

    openDialogAddUser(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            user: null,
            addedByUserId: this.currentUser.userID,
            groups: this.myProfile.myGroups
        };


        const dialogRef = this.dialog.open(UserDetailsComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.user.create'));
                this.userAdded = true;
                this.loadMyProfile();
            }
            else{
                if (result instanceof HttpErrorResponse){
                    if (result.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(result.error.errorMessage);
                    }
                }
            }
        });
    }

    openDialogEditUser(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            user: this.selectedItem,
            addedByUserId: null,
            groups: this.myProfile.myGroups
        };

        const dialogRef = this.dialog.open(UserDetailsComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.user.update'));
                this.userAdded = true;
                this.loadMyProfile();
            }
            else{
                if (result instanceof HttpErrorResponse){
                    if (result.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(result.error.errorMessage);
                    }
                }
            }
        });
    }


    openDialogSubGroup(): void {
        const dialogRef = this.dialog2.open(UserGroupDialogComponent, {
            width: '350px',
            data: this.selectedItem
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.group.add'));
                this.groupAdded = true;
                this.loadMyProfile();
            }
            else{
                if (result instanceof HttpErrorResponse){
                    if (result.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(result.error.errorMessage);
                    }
                }
            }
        });
    }

    openDialogChangeProbeGroup(): void {
        const dialogRef = this.dialog2.open(UserProbeChangeGroupComponent, {
            width: '350px',
            data: this.selectedItem
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.group.add'));
                this.groupAdded = true;
                this.loadMyProfile();
            }
            else{
                if (result instanceof HttpErrorResponse){
                    if (result.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(result.error.errorMessage);
                    }
                }
            }
        });
    }

    openDialogRootGroup(): void {
        const dialogRef = this.dialog.open(UserGroupDialogComponent, {
            width: '250px',
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.group.add'));
                this.groupAdded = true;
                this.loadMyProfile();
            }
            else{
                if (result instanceof HttpErrorResponse){
                    if (result.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(result.error.errorMessage);
                    }
                }
            }
        });
    }

    onMoveGroupNode($event) {
        if (this.checkIfUserCanMoveGroup($event.node, $event.to.parent)){
            this.url = environment.apiEndpoint + 'group/move/' + $event.node.id + '/' + $event.to.parent.id + '/' +  this.currentUser.userID;
            this.httpService.post(null, this.url).subscribe(
                data => {
                    this.alertService.success(this.translate.instant('node.move.success'));
                    this.loading = false;
                },
                error => {
                    if (error.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(error.error.errorMessage);
                    }
                    this.moveNotPossible = true;
                    this.loading = false;
                    this.loadMyProfile();
                });
        }
        else{
            this.moveNotPossible = true;
            this.alertService.error(this.translate.instant('node.move.error'));
            this.loadMyProfile();
        }
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
      else if (type == 'probe'){
          dialogConfig.data = {
              id: 1,
              title: this.translate.instant('message.arayousure'),
              content: this.translate.instant('message.probe.dialog')
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
              else if (type == 'probe') {
                  this.deleteProbe(this.selectedItem);
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
          case 3:
            !this.dataSource2.paginator ? this.dataSource2.paginator = this.paginator2 : null;
            !this.dataSource2.sort ? this.dataSource2.sort = this.sortDev : null;
        }
      });
    }
}
