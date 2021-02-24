import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BaseWidget } from './basewidget.component';
import { TranslateService } from '@ngx-translate/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BaseComponent } from './base.component';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';

@Component({
    selector: 'app-notification',
    templateUrl: './notification-card.component.html',
    styleUrls: ['./widgets.scss']
})
export class NotificationCardComponent extends BaseWidget {
    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() data: Notification[];
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();

    constructor(httpService: HttpService,
        alertService: AlertService,
        translate: TranslateService,
        location: Location,
        router: Router,
        route: ActivatedRoute,
        baseComponent: BaseComponent) {
        super(httpService, alertService, translate, location, router, route, baseComponent);
    }
}
