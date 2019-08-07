import {Component} from '@angular/core';

import {DataService} from '../_services/index';

@Component({
  moduleId: module.id,
  templateUrl: 'osqueryReportDetails.component.html'
})

export class OsQueryReportDetailsComponent {
  report: any;
  queryResult: string;
  loading = false;

  constructor(
    private dataService: DataService) {}

  ngOnInit() {
    this.report = this.dataService.get();
  }

  getQueryResult () {
    this.queryResult = JSON.parse(this.report.queryResult);
    return this.queryResult;
  }
}
