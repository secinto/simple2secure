import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BaseWidget} from "./basewidget.component";
import {ChartType} from 'ng-chartist';
import {IBarChartOptions} from 'chartist';

@Component({
    selector: 'app-bar-chart',
    templateUrl: './chart.component.html',
    styleUrls: ['./widgets.scss']
})
export class BarChartComponent extends BaseWidget{
      
    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() data: any;
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();

    type: ChartType = 'Bar';

    options: IBarChartOptions = {
        axisX: {
          showGrid: false
        },
        height: 300
    };
}