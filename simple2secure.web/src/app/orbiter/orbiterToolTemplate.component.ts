import {HttpErrorResponse} from '@angular/common/http';
import {Component, ViewChild} from '@angular/core';
import {environment} from '../../environments/environment';
import {ToolDTO} from '../_models/DTO/toolDTO';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {TestCaseTemplate} from '../_models/testCaseTemplate';
import {AlertService, HttpService, DataService} from '../_services';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {OrbiterTestTemplateComponent} from './orbiterTestTemplate.component';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterToolTemplate.component.html'
})

export class OrbiterToolTemplateComponent {

	tool: ToolDTO;
	displayedColumns = ['toolName', 'name', 'action'];
	selectedTemplate: TestCaseTemplate;
	loading = false;
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService,
	)
	{
		this.tool = new ToolDTO();
	}

	ngOnInit() {
		this.tool = this.dataService.getTool();
		this.dataSource.data = this.tool.templates;
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

	public onMenuTriggerClick(template: TestCaseTemplate) {
		this.selectedTemplate = template;
	}

	openDialogAddTemplate(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';
		this.selectedTemplate = new TestCaseTemplate();
		this.selectedTemplate.toolId = this.tool.tool.id;

		dialogConfig.data = {
			template: this.selectedTemplate,
			isTestTemplate: true,
			isTestRun: false
		};


		const dialogRef = this.dialog.open(OrbiterTestTemplateComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('test.template.update'));
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

	openDialogEditTemplate(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		dialogConfig.data = {
			template: this.selectedTemplate,
			isTestTemplate: true,
			isTestRun: false
		};
		const dialogRef = this.dialog.open(OrbiterTestTemplateComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('test.template.update'));
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

	public deleteTemplate() {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'tools/delete/' + this.selectedTemplate.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.template.delete'));
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

	public openDialogDeleteTemplate() {
		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.template.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteTemplate();
			}
		});
	}
}
