import {Component} from '@angular/core';

import {QueryRun} from '../_models/index';

import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {Test, TestResult, Command} from '../_models/index';
import {Nl2BrPipeModule} from 'nl2br-pipe';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterToolTestResult.component.html'
})

export class OrbiterToolTestResultComponent {

  test: Test;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private dataService: DataService,
    private router: Router,
    private route: ActivatedRoute
  ) {
      this.test = new Test();
  }

  testExecuted = false;
  loading = false;

  ngOnInit() {
    this.test = this.dataService.get();
  }
}
