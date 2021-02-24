/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */

import { Component, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { saveAs as importedSaveAs } from 'file-saver';
import { environment } from '../../../environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse } from '@angular/common/http';
import { UserGroupDialogComponent } from './userGroupDialog.component';
import { UserDetailsComponent } from './userDetails.component';
import { UserGroupEditComponent } from './userGroupEdit.component';
import { UserGroupApplyConfigComponent } from './userGroupApplyConfig.component';
import { UserContextAddDialogComponent } from './userContextAddDialog.component';
import { RuleOverviewComponent } from '../rule/ruleOverview.component';
import { ConfirmationDialog } from '../dialog/confirmation-dialog';
import { User } from '../../_models/user';
import { HttpService } from '../../_services/http.service';
import { UserRole } from '../../_models/userRole';
import { UserDTO } from '../../_models/DTO/userDTO';
import { AlertService } from '../../_services/alert.service';
import { CompanyGroup } from '../../_models/companygroup';
import { UserInfo } from '../../_models/userInfo';
import { DataService } from '../../_services/data.service';
import { InviteUserDialogComponent } from './inviteUserDialog.component';

@Component({
    moduleId: module.id,
    styleUrls: ['user.pod.css'],
    templateUrl: 'userOverview.component.html',
    selector: 'userOverviewComponent'
})

export class UserOverviewComponent {
    userRole: string;
    loading = false;
    userDeleted = false;
    groupDeleted = false;
    contextDeleted = false;
    groupAdded = false;
    contextAdded = false;
    userAdded = false;
    canDeleteAndEditUser = false;
    selectedItem: any;
    myProfile: UserDTO;
    moveNotPossible = false;
    url: string;
    id: string;
    public user: User;
    private sub: any;
    showMyUsers: boolean;
    addNewGroup: boolean;
    addNewContext: boolean;
    showGroupTable: boolean;
    showUserTable: boolean;
    showContextTable: boolean;
    showEditAndDelete: boolean;
    isGroupDeletable = false;

    displayedColumnsUsers = ['email', 'userRole', 'action'];
    displayedColumnsContext = ['name', 'licenseDownloads', 'action'];

    userDataSource = new MatTableDataSource();
    groupDataSource = new MatTableDataSource();
    contextDataSource = new MatTableDataSource();
    options = { focused: true, allowDrag: true };

    @ViewChild('userPaginator') userPaginator: MatPaginator;
    @ViewChild('contextPaginator') contextPaginator: MatPaginator;
    @ViewChild('sortUser') sortUser: MatSort;
    @ViewChild('sortContext ') sortContext: MatSort;

    constructor(
        private httpService: HttpService,
        private dataService: DataService,
        private alertService: AlertService,
        private dialog: MatDialog,
        private dialog2: MatDialog,
        private translate: TranslateService) {
        this.myProfile = new UserDTO();
        this.myProfile.myProfile = new UserInfo();
    }

    ngOnInit() {
        this.selectedItem = new CompanyGroup();
        this.userRole = this.dataService.getRole();
        this.loadMyProfile();
        if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN ||
            this.userRole == UserRole.SUPERUSER) {
            this.showMyUsers = true;
            this.addNewGroup = true;
        } else {
            this.showMyUsers = false;
            this.addNewGroup = false;
        }

        this.addNewContext = this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN;
    }

    ngAfterViewInit() {
        this.userDataSource.paginator = this.userPaginator;
        this.contextDataSource.paginator = this.contextPaginator;
        this.userDataSource.sort = this.sortUser;
        this.contextDataSource.sort = this.sortContext;
    }

    applyFilterUser(filterValue: string) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.userDataSource.filter = filterValue;
    }

    private loadMyProfile() {
        this.loading = true;
        this.httpService.get(environment.apiUser)
            .subscribe(
                data => {
                    this.myProfile = data;
                    this.userDataSource.data = this.myProfile.myUsersList;
                    this.groupDataSource.data = this.myProfile.myGroups;
                    this.contextDataSource.data = this.myProfile.myContexts;

                    this.checkMyGroupSize(this.myProfile.myGroups);
                    // this.checkMyUsersSize(this.myProfile.myUsersList);
                    this.checkMyContextsSize(this.myProfile.myContexts);

                    if (!this.userDeleted && !this.groupDeleted &&
                        !this.contextDeleted && !this.groupAdded && !this.userAdded && !this.moveNotPossible &&
                        !this.contextAdded) {
                        this.alertService.showSuccessMessage(data, 'message.user', false, true);
                    }

                    this.moveNotPossible = false;
                    this.groupDeleted = false;
                    this.contextDeleted = false;
                    this.groupAdded = false;
                    this.contextAdded = false;
                    this.userAdded = false;
                    this.userDeleted = false;
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    checkMyGroupSize(groups: any) {
        this.showGroupTable = groups.length > 0;
    }

    checkMyUsersSize(users: any) {
        this.showUserTable = users.length > 0;
    }

    checkMyContextsSize(contexts: any) {
        this.showContextTable = contexts.length > 0;
    }

    /*updateUserInfo() {
        this.loading = true;
        this.url = environment.apiEndpoint + 'user/update';
        this.httpService.post(this.myProfile.myProfile, this.url).subscribe(
            data => {
                this.user = data;
                this.alertService.showSuccessMessage(data, 'message.user.update');
                this.router.navigate(['user']);
                this.loading = false;
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });


    }

    public deleteUser(user: any) {
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'user/' + user.user.id).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.user.delete');
                this.userDeleted = true;
                this.loadMyProfile();
                this.loading = false;
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }*/

    public deleteGroup(group: any) {
        this.loading = true;

        const apiUrl = environment.apiGroupGroupId.replace('{groupId}', group.id);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.group.delete');
                this.loadMyProfile();
                this.groupDeleted = true;
                this.loading = false;
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public deleteContext(context: any) {
        this.loading = true;
        this.httpService.delete(environment.apiContextDelete).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'message.context.delete');
                this.loadMyProfile();
                this.contextDeleted = true;
                this.loading = false;
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    public onMenuTriggerClick(item: any) {
        this.selectedItem = item;

        if (this.userRole === UserRole.SUPERADMIN || this.userRole === UserRole.ADMIN) {
            this.showEditAndDelete = true;
            this.dataService.setGroupEditable(this.showEditAndDelete);
        } else {
            if (this.userRole === UserRole.SUPERUSER) {
                this.showEditAndDelete = this.checkIfUserCanEditGroup(item);
                this.dataService.setGroupEditable(this.showEditAndDelete);
            } else {
                this.showEditAndDelete = false;
                this.dataService.setGroupEditable(false);
            }
        }

        if (item.standardGroup) {
            this.isGroupDeletable = false;
        } else {
            this.isGroupDeletable = true;
        }

        if (this.userRole === UserRole.SUPERADMIN) {
            this.canDeleteAndEditUser = true;
        } else if (this.userRole === UserRole.ADMIN) {
            if (item.userRole === UserRole.SUPERADMIN) {
                this.canDeleteAndEditUser = false;
            } else {
                this.canDeleteAndEditUser = true;
            }
        } else if (this.userRole === UserRole.SUPERUSER) {
            if (item.userRole === UserRole.SUPERUSER || item.userRole === UserRole.USER) {
                this.canDeleteAndEditUser = true;
            } else {
                this.canDeleteAndEditUser = false;
            }
        }
    }

    checkIfUserCanMoveGroup(fromGroup: CompanyGroup, toGroup: CompanyGroup) {

        if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN) {
            return true;
        } else if (this.userRole == UserRole.SUPERUSER) {
            if (fromGroup && toGroup) {
                if (this.myProfile.assignedGroups) {
                    if (this.myProfile.assignedGroups.indexOf(fromGroup.id) > -1 && this.myProfile.assignedGroups.indexOf(toGroup.id) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (toGroup.id) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else if (fromGroup && !toGroup) {
                if (this.myProfile.assignedGroups) {
                    if (this.myProfile.assignedGroups.indexOf(fromGroup.id) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    checkIfUserCanEditGroup(group: CompanyGroup) {
        if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN) {
            return true;
        } else if (this.userRole == UserRole.SUPERUSER) {
            if (group) {
                if (this.myProfile.assignedGroups) {
                    if (this.myProfile.assignedGroups.indexOf(group.id) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public onDeleteGroupClick() {
        this.openDialog('group');
    }

    public onDeleteUserClick() {
        this.openDialog('user');
    }

    public onDeleteContextClick() {
        this.openDialog('context');
    }

    public onDeleteProbeClick() {
        this.openDialog('probe');
    }

    public onDeletePodClick() {
        this.openDialog('pod');
    }

    public onApplyConfigClick() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            destGroup: this.selectedItem,
        };

        const dialogRef = this.dialog.open(UserGroupApplyConfigComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.config.group.update');
                this.userAdded = true;
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    public onGroupNameEditClick() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            group: this.selectedItem,
        };

        const dialogRef = this.dialog.open(UserGroupEditComponent, dialogConfig);

    }

    openDialogAddUser(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            user: null,
            groups: this.myProfile.myGroups
        };


        const dialogRef = this.dialog.open(UserDetailsComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.user.create');
                this.userAdded = true;
                this.loadMyProfile();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    openDialogEditUser(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            user: this.selectedItem,
            groups: this.myProfile.myGroups
        };

        const dialogRef = this.dialog.open(UserDetailsComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.user.update');
                this.userAdded = true;
                this.loadMyProfile();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    openDialogNewContext(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '350px';

        dialogConfig.data = {
            context: null
        };
        const dialogRef = this.dialog.open(UserContextAddDialogComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.context.add');
                this.contextAdded = true;
                this.loadMyProfile();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    openDialogInviteUser(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '350px';

        dialogConfig.data = {
            context: null
        };
        const dialogRef = this.dialog.open(InviteUserDialogComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.user.invite');
                this.loadMyProfile();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    openDialogEditContext(): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '350px';

        dialogConfig.data = {
            context: this.selectedItem
        };
        const dialogRef = this.dialog.open(UserContextAddDialogComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.context.edit');
                this.contextAdded = true;
                this.loadMyProfile();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
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
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.group.add');
                this.groupAdded = true;
                this.loadMyProfile();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }

    openDialogRootGroup(): void {
        const dialogRef = this.dialog.open(UserGroupDialogComponent, {
            width: '250px',
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result == true) {
                this.alertService.showSuccessMessage(result, 'message.group.add');
                this.groupAdded = true;
                this.loadMyProfile();
            } else {
                if (result instanceof HttpErrorResponse) {
                    this.alertService.showErrorMessage(result);
                }
            }
        });
    }


    onMoveGroupNode($event) {
        if (this.checkIfUserCanMoveGroup($event.node, $event.to.parent)) {
            const apiUrl = environment.apiGroupMove.replace('{groupId}', $event.node.id);
            const apiUrlFull = apiUrl.replace('{destGroupId}', $event.to.parent.id);
            this.httpService.post(null, apiUrlFull).subscribe(
                data => {
                    this.alertService.showSuccessMessage(data, 'group.move.success');
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.moveNotPossible = true;
                    this.loading = false;
                    this.loadMyProfile();
                });
        } else {
            this.moveNotPossible = true;
            this.alertService.showErrorMessage(null, false, 'group.move.error');
            this.loadMyProfile();
        }
    }

    public onDownloadClick() {
        this.loading = true;
        const apiUrl = environment.apiLicenseGroupId.replace('{groupId}', this.selectedItem.id);
        this.httpService.getFile(apiUrl)
            .subscribe(
                data => {
                    importedSaveAs(data, 'license-' + this.selectedItem.id + '.zip');
                    this.loading = false;
                },
                error => {
                    this.alertService.showErrorMessage(error);
                    this.loading = false;
                });
    }

    openDialogShowRules(frontendRules: any): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '700px';

        dialogConfig.data = {
            rules: frontendRules
        };

        this.dialog.open(RuleOverviewComponent, dialogConfig);

    }

    public openDialog(type: string) {
        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;

        if (type == 'user') {
            dialogConfig.data = {
                id: 1,
                title: this.translate.instant('message.areyousure'),
                content: this.translate.instant('message.user.dialog')
            };
        } else if (type == 'context') {
            dialogConfig.data = {
                id: 1,
                title: this.translate.instant('message.areyousure'),
                content: this.translate.instant('message.context.dialog')
            };
        } else if (type == 'group') {
            dialogConfig.data = {
                id: 1,
                title: this.translate.instant('message.areyousure'),
                content: this.translate.instant('message.group.dialog')
            };
        }
        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true) {
                if (type == 'user') {
                    // this.deleteUser(this.selectedItem);
                } else if (type == 'group') {
                    this.deleteGroup(this.selectedItem);
                } else if (type == 'context') {
                    this.deleteContext(this.selectedItem);
                }
            }
        });
    }

    _setDataSource(indexNumber) {
        setTimeout(() => {
            switch (indexNumber) {
                case 1:
                    !this.userDataSource.paginator ? this.userDataSource.paginator = this.userPaginator : null;
                    !this.userDataSource.sort ? this.userDataSource.sort = this.sortUser : null;
                    break;
                case 2:
                    !this.contextDataSource.paginator ? this.contextDataSource.paginator = this.contextPaginator : null;
                    !this.contextDataSource.sort ? this.contextDataSource.sort = this.sortContext : null;
                    break;
            }
        });
    }
}
