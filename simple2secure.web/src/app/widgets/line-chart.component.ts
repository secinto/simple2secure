import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BaseWidget} from "./basewidget.component";
import {ILineChartOptions} from 'chartist';
import {ChartType} from 'ng-chartist';

@Component({
    selector: 'app-line-chart',
    templateUrl: './chart.component.html',
    styleUrls: ['./widgets.scss']
})
export class LineChartComponent extends BaseWidget{
      
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
}