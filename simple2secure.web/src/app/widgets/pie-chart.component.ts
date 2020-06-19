import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BaseWidget} from "./basewidget.component";
import {ChartType} from 'ng-chartist';
import {IPieChartOptions} from 'chartist';
import {AlertService, HttpService} from "../_services";
import {TranslateService} from "@ngx-translate/core";
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {BaseComponent} from "../components";

@Component({
    selector: 'app-pie-chart',
    templateUrl: './chart.component.html',
    styleUrls: ['./widgets.scss']
})
export class PieChartComponent extends BaseWidget{
      
    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() data: any;
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();

    type: ChartType = 'Pie'; 

    options: IPieChartOptions = {
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