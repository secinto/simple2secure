import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material';
import { BaseComponent } from '../widgets/base.component';
import { TranslateService } from '@ngx-translate/core';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';
import { DataService } from '../../_services/data.service';

@Component({
    moduleId: module.id,
    templateUrl: 'emailRuleOverview.component.html'
})

export class EmailRuleOverviewComponent extends BaseComponent {

    location: string = this.route.snapshot.data['dashboardName'];

    constructor(dialog: MatDialog,
        alertService: AlertService,
        translate: TranslateService,
        dataService: DataService,
        httpService: HttpService,
        route: ActivatedRoute) {
        super(dialog, alertService, translate, dataService, httpService, route);
    }
}
