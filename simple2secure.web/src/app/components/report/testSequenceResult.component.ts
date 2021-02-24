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

import { ChangeDetectorRef, Component } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { TestSequenceResult } from '../../_models/testSequenceResult';
import { TestSequenceResultDetailsComponent } from './testSequenceResultDetails.component';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';
import { fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { ReportComponent } from './report.component';
import { ReportType } from '../../_models/reportType';
import { DeviceType } from '../../_models/deviceType';

@Component({
	moduleId: module.id,
	styleUrls: ['report.css'],
	templateUrl: 'testSequenceResult.component.html'
})

export class TestSequenceResultComponent extends ReportComponent {
	displayedColumns = ['select', 'name', 'podId', 'timestamp'];

	constructor(
		alertService: AlertService,
		httpService: HttpService,
		router: Router,
		dataService: DataService,
		route: ActivatedRoute,
		dialog: MatDialog,
		translate: TranslateService,
		cdr: ChangeDetectorRef
	) {
		super(route, router, httpService, alertService, dataService, dialog, translate, cdr);
	}

	ngOnInit() {
		this.paginator.pageSize = 10;
		this.getGroups(ReportType.TESTSEQUENCE, DeviceType.POD);
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;

		fromEvent(this.filterValue.nativeElement, 'keyup')
			.pipe(
				debounceTime(150),
				distinctUntilChanged(),
				tap(() => {
					this.paginator.pageIndex = 0;
					this.loadReports(ReportType.TESTSEQUENCE);
				})
			)
			.subscribe();

		this.paginator.page
			.pipe(
				tap(() => this.loadReports(ReportType.TESTSEQUENCE))
			)
			.subscribe();
	}

	public openDialogShowTestSequenceResult(testSequenceResult: TestSequenceResult) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';

		dialogConfig.data = {
			result: testSequenceResult,
		};

		this.dialog.open(TestSequenceResultDetailsComponent, dialogConfig);
	}
}
