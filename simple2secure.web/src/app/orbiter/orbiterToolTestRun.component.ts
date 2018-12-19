import {Component, Inject} from '@angular/core';
import { Location } from '@angular/common';
import {AlertService, HttpService, DataService} from '../_services';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {environment} from '../../environments/environment';
import {TestCase, Command} from '../_models/index';
import {TranslateService} from '@ngx-translate/core';
import {ToolDTO} from '../_models/DTO/toolDTO';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterToolTestRun.component.html'
})

export class OrbiterToolTestRunComponent {

  tool: ToolDTO;
  selectedTest: TestCase;
  isTestSelected = false;
  loading = false;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private translate: TranslateService,
    private dataService: DataService,
    private location: Location,
    private dialogRef: MatDialogRef<OrbiterToolTestRunComponent>,
    @Inject(MAT_DIALOG_DATA) data
	) {
		this.tool = data.tool;
	}

  submitTestRun(){

        this.loading = true;
        this.selectedTest.id = null;
        this.httpService.post(this.selectedTest, environment.apiEndpoint + 'tools/' + this.tool.tool.id + '/run').subscribe(
            data => {
	            this.dialogRef.close(true);

            },
            error => {
	            this.dialogRef.close(error);
	            this.loading = false;
            });
    }

    onSelectChange(){
        this.isTestSelected = true;
    }

    addCommand(){
        this.selectedTest.commands.push(new Command());
    }
}
