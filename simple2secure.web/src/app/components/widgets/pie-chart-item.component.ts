import { BaseWidgetItem } from './baseWidgetItem.component';
import { Component } from '@angular/core';
import { IPieChartOptions } from 'chartist';
import { ChartType } from 'ng-chartist';

@Component({
    selector: 'app-pie-chart-item',
    templateUrl: './chart-item.component.html',
    styleUrls: ['./widgets.scss']
})
export class PieChartItemComponent extends BaseWidgetItem {

    type: ChartType = 'Pie';

    options: IPieChartOptions = {
        height: 150
    };
}
