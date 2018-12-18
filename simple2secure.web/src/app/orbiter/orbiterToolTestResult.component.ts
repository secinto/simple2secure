import {Component, Inject} from '@angular/core';
import {AlertService, HttpService, DataService} from '../_services';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TestDTO} from '../_models/DTO/testDTO';
import {DatePipe} from '@angular/common';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterToolTestResult.component.html',
  selector: 'orbiterToolTestResultComponent'
})

export class OrbiterToolTestResultComponent {

  test: TestDTO;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private dataService: DataService,
    private dialogRef: MatDialogRef<OrbiterToolTestResultComponent>,
    @Inject(MAT_DIALOG_DATA) data,
  ) {
      this.test = data.test;
  }
}
