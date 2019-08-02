import {Component, ViewChild} from '@angular/core';
import {environment} from '../../environments/environment';
import {ContextDTO} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {TestRun} from '../_models/testRun';
import {AlertService, HttpService, DataService} from '../_services';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterToolTestScheduledList.component.html'
})

export class OrbiterToolTestScheduledListComponent {

	selectedTest: TestRun = new TestRun();
	podId: string;
	isTestChanged: boolean;
	tests: TestRun[];
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

	public onMenuTriggerClick(test: TestRun) {
		this.selectedTest = test;

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
				this.deleteTest(this.selectedTest);
			}
		});
	}

	public deleteTest(selectedTest: TestRun) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'test/delete/' + selectedTest.testId).subscribe(
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
}
