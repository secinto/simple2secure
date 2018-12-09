import {Component, Inject} from '@angular/core';

import {Processor, Timeunit, UrlParameter} from '../_models/index';

import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {Location, Time} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
  moduleId: module.id,
  templateUrl: 'networkProcessorConfigurationEdit.component.html'
})

export class NetworkProcessorConfigurationEditComponent {

  processor: Processor;
  action: string;
  groupId: string;
  timeUnits = Timeunit;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private dataService: DataService,
    private translate: TranslateService,
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    private dialogRef: MatDialogRef<NetworkProcessorConfigurationEditComponent>,
    @Inject(MAT_DIALOG_DATA) data
  ) {
      if (data.processor == null){
          this.action = UrlParameter.NEW;
          this.processor = new Processor();
          this.groupId = data.groupId;
      }
      else{
          this.action = UrlParameter.EDIT;
          this.processor = data.processor;
          this.groupId = data.groupId;
      }
  }

  extractTimeUnits(): Array<string> {
      const keys = Object.keys(this.timeUnits);
      return keys.slice();
  }

  saveProcessor() {

    if (this.action == UrlParameter.NEW) {
      this.processor.groupId = this.groupId;
    }

    console.log("HEREEEEEEEEEEE");
    this.httpService.post(this.processor, environment.apiEndpoint + 'processors').subscribe(
      data => {
        this.dialogRef.close(true);
      },
      error => {
          this.dialogRef.close(error);
      });
  }

  cancel(){
      this.location.back();
  }
}
