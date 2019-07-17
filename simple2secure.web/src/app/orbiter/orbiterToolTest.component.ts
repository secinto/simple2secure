import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {PodDTO} from '../_models/DTO/podDTO';
import {ContextDTO} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterToolTest.component.html'
})

export class OrbiterToolTestComponent {

	selectedPod: PodDTO;
	pods: PodDTO[];
	context: ContextDTO;
	displayedColumns = ['pod', 'group', 'status', 'action'];
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

	public onMenuTriggerClick(pod: PodDTO) {
		console.log(pod);
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

	public showPodTests() {
		this.dataService.setPods(this.selectedPod);
		this.router.navigate([this.selectedPod.pod.podId], {relativeTo: this.route});
	}
}
