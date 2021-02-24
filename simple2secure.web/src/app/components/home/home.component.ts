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
import { MatDialog } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';
import { BaseComponent } from '../widgets/base.component';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { AuthenticationService } from '../../_services/authentication.service';
import { DataService } from '../../_services/data.service';
import { environment } from '../../../environments/environment';
import { MatDialogConfig } from '@angular/material/dialog';
import { InvitationContextAccept } from '../dialog/invitation-context-accept';

@Component({
    styleUrls: ['home.component.scss'],
    moduleId: module.id,
    templateUrl: 'home.component.html'
})

export class HomeComponent extends BaseComponent {
    location: string = this.route.snapshot.data['dashboardName'];
    tokenType: string = null;
    accessToken: string = null;
    url: string;

    constructor(dialog: MatDialog,
        alertService: AlertService,
        translate: TranslateService,
        dataService: DataService,
        httpService: HttpService,
        route: ActivatedRoute,
        private authService: AuthenticationService) {
        super(dialog, alertService, translate, dataService, httpService, route);
    }

    ngAfterViewInit() {
        this.getPendingInvitations();
    }

    getPendingInvitations() {
        this.httpService.get(environment.apiUserInvitations).subscribe(
            data => {
                const itemsLength = Object.keys(data).length;
                if (data != null && itemsLength > 0) {


                    const dialogConfig = new MatDialogConfig();

                    dialogConfig.disableClose = true;
                    dialogConfig.autoFocus = true;
                    dialogConfig.width = '600px';
                    // Open dialog to select context
                    dialogConfig.data = {
                        title: this.translate.instant('message.pending.invitations'),
                        content: this.translate.instant('message.invitationsDialog'),
                        invitations: data
                    };
                    const dialogRef = this.dialog.open(InvitationContextAccept, dialogConfig);
                    // this.dialogRef.close(true);
                }
            },
            error => {

            });
    }

    public loadWidgets(location: string) {
        this.loadAllWidgetsByUserId(location);
    }
}
