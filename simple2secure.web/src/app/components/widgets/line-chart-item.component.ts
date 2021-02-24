import { BaseWidgetItem } from './baseWidgetItem.component';
import { Component } from '@angular/core';
import { ILineChartOptions } from 'chartist';
import { ChartType } from 'ng-chartist';

@Component({
    selector: 'app-line-chart-item',
    templateUrl: './chart-item.component.html',
    styleUrls: ['./widgets.scss']
})
export class LineChartItemComponent extends BaseWidgetItem {

    type: ChartType = 'Line';

    options: ILineChartOptions = {
        axisX: {
            showGrid: false
        },
        height: 150
    };
}
