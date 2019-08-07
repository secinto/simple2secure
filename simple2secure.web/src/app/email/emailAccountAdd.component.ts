import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ContextDTO, EmailConfiguration, User} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {LocationStrategy} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	styleUrls: ['email.component.css'],
	templateUrl: 'emailAccountAdd.component.html',
	selector: 'emailAccountAdd'
})
export class EmailAccountAddComponent {

	public config: EmailConfiguration;
	loading = false;
	context: ContextDTO;
	isConfigAdded = false;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private dataService: DataService,
		private url: LocationStrategy,
		private translate: TranslateService,
		private dialogRef: MatDialogRef<EmailAccountAddComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{

		this.context = JSON.parse(localStorage.getItem('context'));
		this.config = data.config;

		if (this.config.id) {
			this.isConfigAdded = false;
		}
		else {
			this.isConfigAdded = true;
			this.config.contextId = this.context.context.id;
		}
	}

	saveConfig() {
		this.httpService.post(this.config, environment.apiEndpoint + 'email').subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
			});
		this.loading = false;
	}
}
