import {Component, Inject} from '@angular/core';
import {Location} from '@angular/common';
import {QueryRun, Timeunit, UrlParameter} from '../_models/index';

import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'osqueryConfigurationEdit.component.html'
})

export class OsqueryConfigurationEditComponent {

	queryRun: QueryRun;
	id: string;
	type: number;
	action: string;
	probeId: string;
	groupId: string;

	timeUnits = Timeunit;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private router: Router,
		private translate: TranslateService,
		private route: ActivatedRoute,
		private location: Location,
		private dialogRef: MatDialogRef<OsqueryConfigurationEditComponent>,
		@Inject(MAT_DIALOG_DATA) data
	)
	{
		if (data.queryRun == null) {
			this.action = UrlParameter.NEW;
			this.queryRun = new QueryRun();
			this.groupId = data.groupId;
		}
		else {
			this.action = UrlParameter.EDIT;
			this.queryRun = data.queryRun;
			this.groupId = data.groupId;
		}
	}

	extractTimeUnits(): Array<string> {
		const keys = Object.keys(this.timeUnits);
		return keys.slice();
	}

	saveQueryRun() {

		if (this.action == UrlParameter.NEW) {
			this.queryRun.groupId = this.groupId;
		}

		this.httpService.post(this.queryRun, environment.apiEndpoint + 'config/query').subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
			});
	}
}
