import {Component} from '@angular/core';
import {environment} from '../../environments/environment';
import {DataService, HttpService} from '../_services';
import {Notification} from '../_models';

@Component({
	moduleId: module.id,
	templateUrl: 'notification.component.html',
	selector: 'notificationList',
	styleUrls: ['notification.scss'],
})

export class NotificationComponent {

	notifications: Notification[];
	url: string;

	constructor(private httpService: HttpService,
	            private dataService: DataService){
	}

	ngDoCheck() {
		this.notifications = this.dataService.getNotifications();
	}

	isRead(notification: Notification){

		if (!notification.read){
			console.log('Notification ID ' + notification.id);
			this.url = environment.apiEndpoint + 'notification/read';
			this.httpService.post(notification, this.url).subscribe(
				data => {
					notification.read = true;
				},
				error => {
				});
		}

	}
}
