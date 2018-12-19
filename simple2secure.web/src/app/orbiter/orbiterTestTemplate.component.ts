import {Component, Inject} from '@angular/core';
import {environment} from '../../environments/environment';
import {Command, TestCase} from '../_models';
import {TestCaseTemplate} from '../_models/testCaseTemplate';
import {AlertService, HttpService, DataService} from '../_services';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterTestTemplate.component.html',
  selector: 'orbiterTestTemplateComponent'
})

export class OrbiterTestTemplateComponent {

	template: TestCaseTemplate;
	test: TestCase;
	isTestTemplate = false;
	loading = false;
	url: string;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private dialogRef: MatDialogRef<OrbiterTestTemplateComponent>,
		@Inject(MAT_DIALOG_DATA) data,
	) {
		if (data.isTestTemplate){
			this.isTestTemplate = true;
			this.template = data.template;
		}
		else{
			this.isTestTemplate = false;
			this.test = data.template;

			console.log(JSON.stringify(this.test));
		}

	}

	saveTemplate(){

		this.loading = true;
		this.url = environment.apiEndpoint + 'tools/updateTemplate' ;
		this.httpService.post(this.template, this.url).subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
				this.loading = false;
			});

	}

	addCommand(){
		this.template.commands.push(new Command());
	}
}
