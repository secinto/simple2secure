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

import {Component, OnInit, ViewChild} from '@angular/core';
import {User} from '../_models/index';
import {NgxWidgetGridComponent, WidgetPositionChange} from 'ngx-widget-grid';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {UserContextAddDialogComponent} from '../user';
import {HttpErrorResponse} from '@angular/common/http';
import {WidgetStoreComponent} from '../widgets/widgetStore.component';
import {TranslateService} from '@ngx-translate/core';
import {AlertService} from '../_services';

@Component({
	styleUrls: ['home.component.scss'],
	moduleId: module.id,
	templateUrl: 'home.component.html'
})

export class HomeComponent implements OnInit {
	currentUser: User;
	users: User[] = [];
	@ViewChild('grid') grid: NgxWidgetGridComponent;

	constructor(private dialog: MatDialog,
				private alertService: AlertService,
				private translate: TranslateService) {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
	}

	ngOnInit() {
		console.log(this.grid.getNextPosition());
	}

	ngAfterViewInit(){
		console.log(this.grid.getNextPosition());
	}

	onWidgetChange(event: WidgetPositionChange) {
		console.log(event);
	}

	openDialogAddWidget(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '350px';

		dialogConfig.data = {
			widgets: null
		};
		const dialogRef = this.dialog.open(WidgetStoreComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.context.add'));
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
}
