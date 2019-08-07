import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ContextDTO, Notification} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';

@Component({
	moduleId: module.id,
	styleUrls: ['notification.component.css'],
	templateUrl: 'notificationOverview.component.html',
	selector: 'notificationOverview'
})
export class NotificationOverviewComponent {

	notifications: Notification[];
	loading = false;
	selectedNotification: Notification;
	context: ContextDTO;

	displayedColumns = ['tool', 'name', 'content', 'timestamp'];
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
		this.loadAllNotifications();
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

	private loadAllNotifications() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'notification/' + this.context.context.id)
			.subscribe(
				data => {
					this.notifications = data;
					this.dataSource.data = this.notifications;
					this.loading = false;
					this.alertService.success(this.translate.instant('message.notifications'));

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

	public onMenuTriggerClick(notification: Notification) {
		this.selectedNotification = notification;
	}
}
