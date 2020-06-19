import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AlertService, DataService, HttpService} from '../_services';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {BaseComponent} from '../components/base.component';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	templateUrl: 'emailRuleOverview.component.html'
})

export class EmailRuleOverviewComponent extends BaseComponent{

	location: string = this.route.snapshot.data["dashboardName"];

	constructor(dialog: MatDialog,
				alertService: AlertService,
				translate: TranslateService,
				dataService: DataService, 
				httpService: HttpService,
				route: ActivatedRoute) {
		super(dialog, alertService, translate, dataService, httpService, route);
	}
}
