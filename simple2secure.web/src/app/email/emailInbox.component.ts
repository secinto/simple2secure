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

import {Component, Inject, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {Email} from '../_models/index';
import {AlertService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';


@Component({
	moduleId: module.id,
	styleUrls: ['email.component.css'],
	templateUrl: 'emailInbox.component.html',
	selector: 'emailInbox',
})
export class EmailInboxComponent {

	mails: Email[];
	loading = false;
	id: string;

	displayedColumns = ['from', 'subject', 'time'];
	dataSource = new MatTableDataSource();

	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private translate: TranslateService,
		private dialogRef: MatDialogRef<EmailInboxComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.mails = data.emails;
		this.dataSource.data = this.mails;
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
}
