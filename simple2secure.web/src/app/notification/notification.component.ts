import {Component} from '@angular/core';
import {DataService, HttpService} from '../_services';

@Component({
	moduleId: module.id,
	templateUrl: 'notification.component.html',
	selector: 'notificationList',
	styleUrls: ['notification.scss'],
})

export class NotificationComponent {

	notifications: Notification[];

	constructor(private httpService: HttpService,
	            private dataService: DataService){
	}

	ngDoCheck() {
		this.notifications = this.dataService.getNotifications();
	}
}
