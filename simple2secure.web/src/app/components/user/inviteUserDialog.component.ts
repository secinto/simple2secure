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

import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import { DatePipe, Location } from '@angular/common';
import { MatDialogRef } from '@angular/material';
import { HttpService } from '../../_services/http.service';
import { DataService } from '../../_services/data.service';
import { UserRole } from '../../_models/userRole';
import { UserInvitationRequest } from '../../_models/userInvitationRequest';

@Component({
    moduleId: module.id,
    templateUrl: 'inviteUserDialog.component.html',
    selector: 'inviteUserDialog',
    providers: [DatePipe]
})

export class InviteUserDialogComponent {
    loading = false;
    id: string;
    url: string;
    userInvitationRequest = new UserInvitationRequest();
    rolesArray: UserRole[];
    userRole: string;

    constructor(

        private httpService: HttpService,
        private dataService: DataService,
        private location: Location,
        private dialogRef: MatDialogRef<InviteUserDialogComponent>) {

        this.userRole = this.dataService.getRole();
    }

    ngOnInit() {
    }

    userRoleKeys(): Array<string> {
        if (this.userRole == UserRole.SUPERADMIN || this.userRole == UserRole.ADMIN) {
            // Return ADMIN, SUPERUSER and USER
            this.rolesArray = [UserRole.ADMIN, UserRole.SUPERUSER, UserRole.USER];
            return this.rolesArray;
        } else if (this.userRole == UserRole.SUPERUSER) {
            // Return USER
            this.rolesArray = [UserRole.USER];
            return this.rolesArray;
        }
    }

    inviteUser() {
        this.loading = true;
        this.httpService.post(this.userInvitationRequest, environment.apiUserInvite).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
                this.loading = false;
            });
    }

    cancel() {
        this.location.back();
    }
}
