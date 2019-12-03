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

import {Component, ViewChild} from '@angular/core';
import {MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {AlertService, DataService, HttpService} from '../_services';
import {saveAs as importedSaveAs} from 'file-saver';
import {CompanyGroup, User, UserDTO, UserRole} from '../_models';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {RuleOverviewComponent} from '../rule';
import {UserGroupDialogComponent} from './userGroupDialog.component';
import {HttpErrorResponse} from '@angular/common/http';
import {UserDetailsComponent} from './userDetails.component';
import {UserGroupApplyConfigComponent} from './userGroupApplyConfig.component';
import {UserContextAddDialogComponent} from './userContextAddDialog.component';
import {UserInfo} from '../_models/userInfo';

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
	options = {focused: true, allowDrag: true};

	@ViewChild('userPaginator') userPaginator: MatPaginator;
	@ViewChild('contextPaginator') contextPaginator: MatPaginator;
	@ViewChild('sortUser') sortUser: MatSort;
	@ViewChild('sortContext ') sortContext: MatSort;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private dataService: DataService,
		private alertService: AlertService,
		private dialog: MatDialog,
		private dialog2: MatDialog,
		private translate: TranslateService)
	{
		this.myProfile = new UserDTO();
		this.myProfile.myProfile = new UserInfo();
	}

	ngOnInit() {
		this.selectedItem = new CompanyGroup();
		this.userRole = sessionStorage.getItem('role');
		this.loadMyProfile();
		if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN ||
			this.userRole == UserRole.SUPERUSER)
		{
			this.showMyUsers = true;
			this.addNewGroup = true;
		}
		else {
			this.showMyUsers = false;
			this.addNewGroup = false;
		}

		if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN) {
			this.addNewContext = true;
		}
		else {
			this.addNewContext = false;
		}
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
		this.httpService.get(environment.apiEndpoint + 'user')
			.subscribe(
				data => {
					this.myProfile = data;
					this.userDataSource.data = this.myProfile.myUsersList;
					this.groupDataSource.data = this.myProfile.myGroups;
					this.contextDataSource.data = this.myProfile.myContexts;

					this.checkMyGroupSize(this.myProfile.myGroups);
					this.checkMyUsersSize(this.myProfile.myUsersList);
					this.checkMyContextsSize(this.myProfile.myContexts);

					if (!this.userDeleted && !this.groupDeleted &&
						!this.contextDeleted && !this.groupAdded && !this.userAdded && !this.moveNotPossible &&
						!this.contextAdded) {
						this.alertService.success(this.translate.instant('message.user'));
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

					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}

					this.loading = false;
				});
	}

	loadRulesByContextId(){
		this.loading = true;

		this.httpService.get(environment.apiEndpoint + 'rule/' + this.selectedItem.id)
			.subscribe(
				data => {
					this.loading = false;
					this.openDialogShowRules(data);
				},
				error => {

					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.loading = false;
				});
		this.loading = false;

	}

	checkMyGroupSize(groups: any) {
		if (groups.length > 0) {
			this.showGroupTable = true;
		}
		else {
			this.showGroupTable = false;
		}
	}

	checkMyUsersSize(users: any) {
		if (users.length > 0) {
			this.showUserTable = true;
		}
		else {
			this.showUserTable = false;
		}
	}

	checkMyContextsSize(contexts: any) {
		if (contexts.length > 0) {
			this.showContextTable = true;
		}
		else {
			this.showContextTable = false;
		}
	}

	updateUserInfo() {
		this.loading = true;
		this.url = environment.apiEndpoint + 'user/update';
		this.httpService.post(this.myProfile.myProfile, this.url).subscribe(
			data => {
				this.user = data;
				this.alertService.success(this.translate.instant('message.user.update'));
				this.router.navigate(['user']);
				this.loading = false;
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
				this.loading = false;
			});


	}


	public editGroup(groupItem: any) {
		this.router.navigate(['../user/group', groupItem.id], {relativeTo: this.route});
	}

	public deleteUser(user: any) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'user/' + user.user.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.user.delete'));
				this.userDeleted = true;
				this.loadMyProfile();
				this.loading = false;
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
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
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
				this.loading = false;
			});
	}

	public deleteContext(context: any) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'context/delete/' + context.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.context.delete'));
				this.loadMyProfile();
				this.contextDeleted = true;
				this.loading = false;
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
				this.loading = false;
			});
	}

	public onMenuTriggerClick(item: any) {
		this.selectedItem = item;

		if (this.userRole === UserRole.SUPERADMIN || this.userRole === UserRole.ADMIN) {
			this.showEditAndDelete = true;
			this.dataService.setGroupEditable(this.showEditAndDelete);
		}
		else {
			if (this.userRole === UserRole.SUPERUSER) {
				this.showEditAndDelete = this.checkIfUserCanEditGroup(item);
				this.dataService.setGroupEditable(this.showEditAndDelete);
			}
			else {
				this.showEditAndDelete = false;
				this.dataService.setGroupEditable(false);
			}
		}

		if (item.standardGroup) {
			this.isGroupDeletable = false;
		}
		else {
			this.isGroupDeletable = true;
		}

		if (this.userRole === UserRole.SUPERADMIN) {
			this.canDeleteAndEditUser = true;
		}
		else if (this.userRole === UserRole.ADMIN) {
			if (item.userRole === UserRole.SUPERADMIN) {
				this.canDeleteAndEditUser = false;
			}
			else {
				this.canDeleteAndEditUser = true;
			}
		}
		else if (this.userRole === UserRole.SUPERUSER) {
			if (item.userRole === UserRole.SUPERUSER || item.userRole === UserRole.USER) {
				this.canDeleteAndEditUser = true;
			}
			else {
				this.canDeleteAndEditUser = false;
			}
		}
	}

	checkIfUserCanMoveGroup(fromGroup: CompanyGroup, toGroup: CompanyGroup) {

		if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN) {
			return true;
		}
		else if (this.userRole == UserRole.SUPERUSER) {
			if (fromGroup && toGroup) {
				if (this.myProfile.assignedGroups) {
					if (this.myProfile.assignedGroups.indexOf(fromGroup.id) > -1 && this.myProfile.assignedGroups.indexOf(toGroup.id) > -1) {
						return true;
					}
					else {
						return false;
					}
				}
				else {
					if (toGroup.id) {
						return true;
					}
					else {
						return false;
					}
				}
			}
			else if (fromGroup && !toGroup) {
				if (this.myProfile.assignedGroups) {
					if (this.myProfile.assignedGroups.indexOf(fromGroup.id) > -1) {
						return true;
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
		}
		else {
			return false;
		}
	}

	checkIfUserCanEditGroup(group: CompanyGroup) {
		if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN) {
			return true;
		}
		else if (this.userRole == UserRole.SUPERUSER) {
			if (group) {
				if (this.myProfile.assignedGroups) {
					if (this.myProfile.assignedGroups.indexOf(group.id) > -1) {
						return true;
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}

			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	public onGroupEditClick() {
		this.editGroup(this.selectedItem);
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
				this.alertService.success(this.translate.instant('message.config.group.update'));
				this.userAdded = true;
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
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
			groups: this.myProfile.myGroups
		};


		const dialogRef = this.dialog.open(UserDetailsComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.user.create'));
				this.userAdded = true;
				this.loadMyProfile();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
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
			groups: this.myProfile.myGroups
		};

		const dialogRef = this.dialog.open(UserDetailsComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.user.update'));
				this.userAdded = true;
				this.loadMyProfile();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
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
				this.alertService.success(this.translate.instant('message.context.add'));
				this.contextAdded = true;
				this.loadMyProfile();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
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
				this.alertService.success(this.translate.instant('message.context.edit'));
				this.contextAdded = true;
				this.loadMyProfile();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
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
			if (result == true) {
				this.alertService.success(this.translate.instant('message.group.add'));
				this.groupAdded = true;
				this.loadMyProfile();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
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
			if (result == true) {
				this.alertService.success(this.translate.instant('message.group.add'));
				this.groupAdded = true;
				this.loadMyProfile();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
				}
			}
		});
	}


	onMoveGroupNode($event) {
		if (this.checkIfUserCanMoveGroup($event.node, $event.to.parent)) {
			this.url = environment.apiEndpoint + 'group/move/' + $event.node.id + '/' + $event.to.parent.id;
			this.httpService.post(null, this.url).subscribe(
				data => {
					this.alertService.success(this.translate.instant('group.move.success'));
					this.loading = false;
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.moveNotPossible = true;
					this.loading = false;
					this.loadMyProfile();
				});
		}
		else {
			this.moveNotPossible = true;
			this.alertService.error(this.translate.instant('group.move.error'));
			this.loadMyProfile();
		}
	}

	public onDownloadClick() {
		this.loading = true;
		this.httpService.getFile(environment.apiEndpoint + 'license/' + this.selectedItem.id)
			.subscribe(
				data => {
					importedSaveAs(data, 'license-' + this.selectedItem.id + '.zip');
					this.loading = false;
				},
				error => {
					this.alertService.error('Error occured while downloading license');
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
		}
		else if (type == 'context') {
			dialogConfig.data = {
				id: 1,
				title: this.translate.instant('message.areyousure'),
				content: this.translate.instant('message.context.dialog')
			};
		}
		else if (type == 'group') {
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
					this.deleteUser(this.selectedItem);
				}
				else if (type == 'group') {
					this.deleteGroup(this.selectedItem);
				}
				else if (type == 'context') {
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
