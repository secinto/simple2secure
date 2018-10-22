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
  objectKeys = Object.keys;

  constructor(
    private dataService: DataService,
    private alertService: AlertService) {}

  ngOnInit() {
    this.report = this.dataService.get();
  }
}
