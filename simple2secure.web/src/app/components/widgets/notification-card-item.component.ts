import { Component } from '@angular/core';
import { BaseWidgetItem } from './baseWidgetItem.component';

@Component({
    selector: 'app-notification-item',
    templateUrl: './notification-card-item.component.html',
    styleUrls: ['./widgets.scss']
})
export class NotificationCardItem extends BaseWidgetItem {
}
