import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {PodDTO} from '../_models/DTO/podDTO';
import {ContextDTO, Test} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {TestObjWeb} from '../_models/testObjWeb';
import {AlertService, HttpService, DataService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TestDetailsComponent} from './testDetails.component';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterToolTestList.component.html'
})

export class OrbiterToolTestListComponent {

	selectedTest: TestObjWeb = new TestObjWeb();
	podId: string;
	isTestChanged: boolean;
	pod: PodDTO;
	tests: TestObjWeb[];
	context: ContextDTO;
	displayedColumns = ['testId', 'version', 'status', 'action'];
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
		private router: Router,
		private route: ActivatedRoute
	) {}

	ngOnInit() {
		this.isTestChanged = false;
		this.context = JSON.parse(localStorage.getItem('context'));
		this.pod = this.dataService.getPods();
		this.loadTests(this.pod.pod.podId);
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

	public onMenuTriggerClick(test: TestObjWeb) {
		this.selectedTest = test;

	}

	public loadTests(podId: string){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'test/' + podId)
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

	openDialogShowTest(type: string): void {

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		dialogConfig.data = {
			tests: this.selectedTest,
			type: type,
			podId: this.pod.pod.podId
		};

		const dialogRef = this.dialog.open(TestDetailsComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.isTestChanged = true;
				this.loadTests(this.pod.pod.podId);
			}
		});

	}

	public runTest(){

		this.loading = true;

		this.url = environment.apiEndpoint + 'test/scheduleTest/' + this.context.context.id;
		this.httpService.post(this.selectedTest, this.url).subscribe(
			data => {

				this.alertService.success(this.translate.instant('message.test.schedule'));
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

	public deleteTest(selectedTest: TestObjWeb) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'test/delete/' + selectedTest.testId).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.test.delete'));
				this.loading = false;
				this.isTestChanged = true;
				this.loadTests(this.pod.pod.podId);
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
