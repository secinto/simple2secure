import { EventEmitter, Input, Output, Injectable } from '@angular/core';
import { Widget } from '../../_models/widget';
import { IChartistAnimationOptions, IChartistData } from 'chartist';
import { ChartEvent } from 'ng-chartist';
import { DataService } from '../../_services/data.service';

@Injectable()
export class BaseWidgetItem {
  widget: Widget;

  @Input() id: string;
  @Input() name: string;
  @Input() tag: string;
  @Input() description: string;
  @Input() bgClass: string;
  @Input() icon: string;
  @Input() count: number;
  @Input() data: any[];
  @Output() event: EventEmitter<any> = new EventEmitter();

  constructor(private dataService: DataService) { }

  addWidget() {
    this.widget = new Widget();
    this.widget.id = this.id;
    this.widget.tag = this.tag;
    this.widget.description = this.description;
    this.widget.bgClass = this.bgClass;
    this.widget.icon = this.icon;
    this.widget.name = this.name;

    this.dataService.addWidgetToLS(this.widget);
  }

  events: ChartEvent = {
    draw: (data) => {
      if (data.type === 'bar' || data.type === 'line' || data.type === 'pie') {
        data.element.animate({
          y2: <IChartistAnimationOptions>{
            dur: '0.5s',
            from: data.y1,
            to: data.y2,
            easing: 'easeOutQuad'
          }
        });
      }
    }
  };

  widgetData: IChartistData = {
    series: [
      [3, 1, 2]
    ]
  };

  widgetDataPie: IChartistData = {
    series: [
      20, 30, 50
    ]
  };
}
