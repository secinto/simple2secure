import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Notification} from "../_models";
import {BaseWidget} from "./basewidget.component";

@Component({
    selector: 'app-notification',
    templateUrl: './notification-card.component.html',
    styleUrls: ['./widgets.scss']
})
export class NotificationCardComponent extends BaseWidget{
    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() data: Notification[];
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();
}
