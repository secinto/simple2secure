import { BaseWidgetItem } from './baseWidgetItem.component';
import { Component } from '@angular/core';
import { IBarChartOptions } from 'chartist';
import { ChartType } from 'ng-chartist';

@Component({
    selector: 'app-bar-chart-item',
    templateUrl: './chart-item.component.html',
    styleUrls: ['./widgets.scss']
})
export class BarChartItemComponent extends BaseWidgetItem {

    type: ChartType = 'Bar';

    options: IBarChartOptions = {
        axisX: {
            showGrid: false
        },
        height: 150
    };
}
