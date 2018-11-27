import {Component} from '@angular/core';
import {NetworkReport} from '../_models/index';
import {DataService, AlertService} from '../_services/index';

@Component({
  moduleId: module.id,
  templateUrl: 'networkReportDetails.component.html'
})

export class NetworkReportDetailsComponent {
  report: NetworkReport;
  loading = false;
  //objectKeys = Object.keys;
  result: string;
  constructor(
    private dataService: DataService) {}

  ngOnInit() {
    this.report = this.dataService.get();
  }

    getStringResult () {
        this.result = JSON.parse(this.report.stringContent);
        return this.result;
    }
}
