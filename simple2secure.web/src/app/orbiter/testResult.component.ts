import {Component, ViewChild} from '@angular/core';
import {AlertService, DataService, HttpService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {ContextDTO, TestResult, TestResultDTO} from '../_models/index';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {NetworkReportDetailsComponent} from '../report';
import {TestResultDetailsComponent} from './testResultDetails.component';

@Component({
	moduleId: module.id,
	templateUrl: 'testResult.component.html'
})

export class TestResultComponent {

	currentUser: any;
	testResults: TestResultDTO[];
	loading = false;
	context: ContextDTO;
	displayedColumns = ['name', 'license', 'group'];
	dataSource = new MatTableDataSource();
	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private alertService: AlertService,
		private httpService: HttpService,
		private router: Router,
		private dataService: DataService,
		private route: ActivatedRoute,
		private dialog: MatDialog,
		private translate: TranslateService
	)
	{}

	ngOnInit() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadTestResults();
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

	loadTestResults() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'test/testresult/' + this.context.context.id)
			.subscribe(
				data => {
					this.testResults = data;
					this.dataSource.data = this.testResults;
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

	openDialogShowTestResult(result: TestResultDTO): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		dialogConfig.data = {
			result: result.testResult,
		};

		this.dialog.open(TestResultDetailsComponent, dialogConfig);

	}
}
