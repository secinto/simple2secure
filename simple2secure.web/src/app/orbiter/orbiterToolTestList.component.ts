import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {PodDTO} from '../_models/DTO/podDTO';
import {ContextDTO, Test} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {TestDetailsComponent} from './testDetails.component';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterToolTestList.component.html'
})

export class OrbiterToolTestListComponent {

	selectedTest: Test = new Test();
	podId: string;
	pod: PodDTO;
	context: ContextDTO;
	displayedColumns = ['testId', 'hostname', 'version', 'action'];
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
		private router: Router,
		private route: ActivatedRoute
	) {}

	ngOnInit() {

		this.context = JSON.parse(localStorage.getItem('context'));
		this.pod = this.dataService.getPods();
		this.dataSource.data = this.pod.test;
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

	public onMenuTriggerClick(test: Test) {
		this.selectedTest = test;
	}

	openDialogShowTest(type: string): void {

		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '750px';
		dialogConfig.data = {
			tests: this.selectedTest,
			type: type
		};

		this.dialog.open(TestDetailsComponent, dialogConfig);

	}

	public runTest(){
		console.log('Run test');
	}
}
