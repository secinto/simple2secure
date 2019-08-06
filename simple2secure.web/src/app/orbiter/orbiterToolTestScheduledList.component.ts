import {Component, ViewChild} from '@angular/core';
import {MatDialog, MatDialogConfig, MatPaginator, MatSort, MatTableDataSource} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {TestRunDTO} from '../_models/DTO/testRunDTO';
import {ContextDTO} from '../_models/index';
import {TestStatus} from '../_models/testStatus';
import {AlertService, DataService, HttpService} from '../_services';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TestResultDetailsComponent} from '../report/testResultDetails.component';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterToolTestScheduledList.component.html'
})

export class OrbiterToolTestScheduledListComponent {

	selectedTestRun: TestRunDTO = new TestRunDTO();
	podId: string;
	isTestChanged: boolean;
	showTestResult = false;
	tests: TestRunDTO[];
	context: ContextDTO;
	displayedColumns = ['name', 'type', 'status', 'action'];
	loading = false;
	url: string;
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private dataService: DataService,
		private dialog: MatDialog,
		private translate: TranslateService,
	) {}

	ngOnInit() {
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadScheduledTests();
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

	public onMenuTriggerClick(test: TestRunDTO) {
		this.showTestResult = false;
		if (test.testRun.testStatus == TestStatus.EXECUTED){
			if (test.testResult != null){
				this.showTestResult = true;
			}
		}
		this.selectedTestRun = test;
	}

	public loadScheduledTests(){
		this.loading = true;
		console.log(this.context);
		this.httpService.get(environment.apiEndpoint + 'test/getScheduledTests/' + this.context.context.id)
			.subscribe(
				data => {
					this.tests = data;
					this.dataSource.data = this.tests;
					if (!this.isTestChanged){
						if (data.length > 0) {
							this.alertService.success(this.translate.instant('message.data'));
						}
						else {
							this.alertService.error(this.translate.instant('message.data.notProvided'));
						}
					}


				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}

				});

				this.loading = false;
	}

	public openDeleteDialog() {
		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.test.dialog')
		};


		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteTestRun(this.selectedTestRun);
			}
		});
	}

	public deleteTestRun(testRun: TestRunDTO) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'test/delete/testrun/' + testRun.testRun.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.test.delete'));
				this.loading = false;
				this.isTestChanged = true;
				this.loadScheduledTests();
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

	public getEnumValue(value: any){
		if (value == 'MANUAL_POD'){
			return 'MANUAL POD';
		}
		else if (value == 'MANUAL_PORTAL'){
			return 'MANUAL PORTAL';
		}
		else{
			return 'AUTOMATIC PORTAL';
		}
	}

	public getTestStatusByTestResult(testRunDTO: TestRunDTO){
		if(testRunDTO.testResult == null){
			return 'UNKNOWN';
		}
		else{
			return testRunDTO.testRun.testStatus;
		}
	}

	openDialogShowTestResult(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			result: this.selectedTestRun.testResult
		};

		this.dialog.open(TestResultDetailsComponent, dialogConfig);

	}
}
