import { Component, OnInit } from '@angular/core';
import { Chart } from 'angular-highcharts';

@Component({
    moduleId: module.id,
    styleUrls: ['analysis.component.css'],
    selector: 'analysis',
    templateUrl: 'analysis.component.html'
})

export class AnalysisComponent {

    chart = new Chart({
        chart: {
          type: 'line'
        },
        title: {
          text: 'Linechart'
        },
        credits: {
          enabled: false
        },
        series: [
          {
            name: 'Line 1',
            data: [11, 21, 39]
          }
        ]
      });
     
      // add point to chart serie
      add() {
        this.chart.addPoint(Math.floor(Math.random() * 105));
      }


}
