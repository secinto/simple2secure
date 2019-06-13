import {HttpErrorResponse} from '@angular/common/http';
import {Component, ViewChild} from '@angular/core';
import {ContextDTO, TestResultDTO, Tool} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {Pod} from '../_models/pod';
import {AlertService, HttpService, DataService} from '../_services';
import {TestDTO} from '../_models/DTO/testDTO';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {OrbiterTestTemplateComponent} from './orbiterTestTemplate.component';
import {OrbiterToolTestResultComponent} from './orbiterToolTestResult.component';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterToolTest.component.html'
})

export class OrbiterToolTestComponent {

	selectedPod: Pod;
	pods: Pod[];
	context: ContextDTO;
	displayedColumns = ['pod', 'group', 'action'];
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
	}

	ngOnInit() {
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadPods();
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

	public onMenuTriggerClick(pod: Pod) {
		this.selectedPod = pod;
	}

	loadPods() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'pod/' + this.context.context.id)
			.subscribe(
				data => {
					this.pods = data;
					this.dataSource.data = this.pods;
					if (data.length > 0) {
						this.alertService.success(this.translate.instant('message.data'));
					}
					else {
						this.alertService.error(this.translate.instant('message.data.notProvided'));
					}
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

	public showConfiguration(){

	}
}
