import {HttpErrorResponse} from '@angular/common/http';
import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ContextDTO, EmailConfiguration, EmailConfigurationDTO} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {EmailAccountAddComponent} from './emailAccountAdd.component';
import {EmailInboxComponent} from './emailInbox.component';

@Component({
	moduleId: module.id,
	styleUrls: ['email.component.css'],
	templateUrl: 'emailOverview.component.html',
	selector: 'emailOverview'
})
export class EmailOverviewComponent {

	config: EmailConfigurationDTO[];
	loading = false;
	selectedConfig: EmailConfigurationDTO;
	deleted = false;
	context: ContextDTO;
	isConfigUpdated = false;
	isConfigAdded = false;

	displayedColumns = ['email', 'id', 'incomingPort', 'action'];
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService)
	{}

	ngOnInit() {
		this.context = JSON.parse(localStorage.getItem('context'));
		console.log(this.context.context.id);
		this.loadAllConfigurations();
	}

	ngAfterViewInit() {
		this.dataSource.sort = this.sort;
		this.dataSource.paginator = this.paginator;
	}

	applyFilter(filterValue: string) {
		filterValue = filterValue.trim(); // Remove whitespace
		filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
		this.dataSource.filter = filterValue;
	}

	private loadAllConfigurations() {

		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'email/' + this.context.context.id)
			.subscribe(
				data => {
					this.config = data;
					this.dataSource.data = this.config;

					if (!this.deleted && !this.isConfigUpdated && !this.isConfigAdded) {
						if (data.length > 0) {
							this.alertService.success(this.translate.instant('message.emailConfig'));
						}
						else {
							this.alertService.error(this.translate.instant('message.emailConfig.notProvided'));
						}
						this.loading = false;
					}
					this.deleted = false;
					this.isConfigUpdated = false;
					this.isConfigAdded = false;
					this.loading = false;
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.loading = false;
				});
	}

	public onMenuTriggerClick(config: EmailConfigurationDTO) {
		this.selectedConfig = config;
	}

	openDialogViewInbox(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';

		dialogConfig.data = {
			emails: this.selectedConfig.emails
		};
		this.dialog.open(EmailInboxComponent, dialogConfig);

	}

	openDialogAddConfig(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		dialogConfig.data = {
			config: new EmailConfiguration(),
		};

		const dialogRef = this.dialog.open(EmailAccountAddComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.email'));
				this.isConfigAdded = true;
				this.loadAllConfigurations();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
				}
			}
		});
	}

	openDialogEditConfig(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';
		dialogConfig.data = {
			config: this.selectedConfig.configuration,
		};
		const dialogRef = this.dialog.open(EmailAccountAddComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.emailConfig.update'));
				this.isConfigUpdated = true;
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
				}
			}
		});
	}

	public openDialogDeleteConfig() {
		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.emailConfig.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteConfig(this.selectedConfig.configuration);
			}
		});
	}

	public deleteConfig(config: EmailConfiguration) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'email/' + config.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.emailConfig.delete'));
				this.deleted = true;
				this.loadAllConfigurations();
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
				this.loading = false;
			});
	}

	private openDialogShowRules()
	{
		console.log("inside openDialogShowRules: not implemented yet");
	}

	private openDialogAddRule()
	{
		console.log("inside openDialogAddRule: not implemented yet");
	}
}
