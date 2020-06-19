import {Component, OnInit, Input, Output, EventEmitter, Injectable } from '@angular/core';
import {BaseWidget} from "./basewidget.component";
import {AlertService, HttpService} from "../_services";
import {TranslateService} from "@ngx-translate/core";
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {BaseComponent} from "../components";

@Component({
    selector: 'app-stat',
    templateUrl: './stat.component.html',
    styleUrls: ['./widgets.scss']
})
@Injectable()
export class StatComponent extends BaseWidget{
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() name: string;
    @Input() data: number;
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();
    loading = false;
	
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
