import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BaseWidget } from './basewidget.component';
import { ILineChartOptions } from 'chartist';
import { ChartType } from 'ng-chartist';
import { TranslateService } from '@ngx-translate/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BaseComponent } from './base.component';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';

@Component({
    selector: 'app-line-chart',
    templateUrl: './chart.component.html',
    styleUrls: ['./widgets.scss']
})
export class LineChartComponent extends BaseWidget {

    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() data: any;
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();

    type: ChartType = 'Line';

    options: ILineChartOptions = {
        axisX: {
            showGrid: false
        },
        height: 300
    };

    constructor(httpService: HttpService,
        alertService: AlertService,
        translate: TranslateService,
        location: Location,
        router: Router,
        route: ActivatedRoute,
        baseComponent: BaseComponent) {
        super(httpService, alertService, translate, location, router, route, baseComponent);
    }
}
