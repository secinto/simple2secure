import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Notification} from "../_models";
import {BaseWidget} from "./basewidget.component";
import {AlertService, HttpService} from "../_services";
import {TranslateService} from "@ngx-translate/core";
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {BaseComponent} from "../components";

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
