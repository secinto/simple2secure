import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BaseWidget} from "./basewidget.component";
import {ChartType} from 'ng-chartist';
import {IPieChartOptions} from 'chartist';

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
}