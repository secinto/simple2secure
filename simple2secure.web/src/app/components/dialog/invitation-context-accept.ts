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

import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { HttpService } from '../../_services/http.service';
import { MatTableDataSource } from '@angular/material/table';
import { environment } from '../../../environments/environment';
import { UserInvitationDTO } from '../../_models/DTO/userInvitationDTO';

@Component({
    selector: 'invitation-context-accept',
    templateUrl: 'invitation-context-accept.html'
})

export class InvitationContextAccept {

    displayedColumnsContext = ['context', 'role', 'action'];

    dataSource = new MatTableDataSource();

    constructor(private dialogRef: MatDialogRef<InvitationContextAccept>,
        private httpService: HttpService,
        @Inject(MAT_DIALOG_DATA) public data: any) {
        this.dataSource.data = data.invitations;
    }

    onCloseClick() {
        this.dialogRef.close();
    }

    onButtonClick(invitationContext: UserInvitationDTO, status: boolean) {

        invitationContext.accepted = status;

        this.httpService.post(invitationContext, environment.apiUserAcceptInvitation).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
            });
    }
}
