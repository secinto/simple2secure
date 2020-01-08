/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */

import {Component, ComponentFactoryResolver, Inject, Injector, ViewChild, ViewContainerRef} from '@angular/core';
import {AlertService, DataService, HttpService} from '../_services';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {Widget} from '../_models/widget';
import {MatDialogRef} from '@angular/material';
import {StatItemComponent} from './stat-item.component';
import {NotificationCardItem} from "./notification-card-item.component";
import {TrafficLightItemComponent} from "./traffic-light-item.component";
import {DownloadWidgetItemComponent} from "./download-item.component";
import {Notification} from "../_models";

@Component({
	moduleId: module.id,
	templateUrl: 'widgetStore.component.html',
	selector: 'widgetStore',
	styleUrls: ['./widgets.scss', './widget-store.scss']
})

export class WidgetStoreComponent {
	loading = false;
	widgets: Widget[];

	@ViewChild('container', {read: ViewContainerRef}) container: ViewContainerRef;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private alertService: AlertService,
		private translate: TranslateService,
		private dialogRef: MatDialogRef<WidgetStoreComponent>,
		private injector: Injector,
		private componentFactoryResolver: ComponentFactoryResolver)
	{


	}

	ngOnInit() {
		this.dataService.clearWidgets();
		this.loadWidgets();
	}

	private loadWidgets() {
		this.httpService.get(environment.apiEndpoint + 'widget')
			.subscribe(
				data => {
					this.widgets = data;
					this.loadComponents(data);
				},
				error => {

					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
				});
	}

	loadComponents(widgets: Widget[]){
		for (const widget of widgets) {
			if (widget.tag == 'app-stat'){
				const componentFactory = this.componentFactoryResolver.resolveComponentFactory(StatItemComponent);
				const component = this.container.createComponent(componentFactory);

				(<StatItemComponent>component.instance).id = widget.id;
				(<StatItemComponent>component.instance).name = widget.name;
				(<StatItemComponent>component.instance).tag = widget.tag;
				(<StatItemComponent>component.instance).description = widget.description;
				(<StatItemComponent>component.instance).bgClass = widget.bgClass;
				(<StatItemComponent>component.instance).icon = widget.icon;
				(<StatItemComponent>component.instance).count = 10;
				(<StatItemComponent>component.instance).label = widget.label;
			}
			else if (widget.tag == 'app-notification'){
				const componentFactory = this.componentFactoryResolver.resolveComponentFactory(NotificationCardItem);
				const component = this.container.createComponent(componentFactory);
				let notifications : any[] = ['First notification','Second notification'];

				(<NotificationCardItem>component.instance).id = widget.id;
				(<NotificationCardItem>component.instance).name = widget.name;
				(<NotificationCardItem>component.instance).tag = widget.tag;
				(<NotificationCardItem>component.instance).description = widget.description;
				(<NotificationCardItem>component.instance).bgClass = widget.bgClass;
				(<NotificationCardItem>component.instance).label = widget.label;
				(<NotificationCardItem>component.instance).data = notifications;
			}
			else if (widget.tag == 'app-traffic-light'){
				const componentFactory = this.componentFactoryResolver.resolveComponentFactory(TrafficLightItemComponent);
				const component = this.container.createComponent(componentFactory);

				(<TrafficLightItemComponent>component.instance).id = widget.id;
				(<TrafficLightItemComponent>component.instance).name = widget.name;
				(<TrafficLightItemComponent>component.instance).tag = widget.tag;
				(<TrafficLightItemComponent>component.instance).description = widget.description;
				(<TrafficLightItemComponent>component.instance).bgClass = widget.bgClass;
				(<TrafficLightItemComponent>component.instance).label = widget.label;
			}
			else if (widget.tag == 'app-download'){
				const componentFactory = this.componentFactoryResolver.resolveComponentFactory(DownloadWidgetItemComponent);
				const component = this.container.createComponent(componentFactory);

				(<DownloadWidgetItemComponent>component.instance).id = widget.id;
				(<DownloadWidgetItemComponent>component.instance).name = widget.name;
				(<DownloadWidgetItemComponent>component.instance).tag = widget.tag;
				(<DownloadWidgetItemComponent>component.instance).description = widget.description;
				(<DownloadWidgetItemComponent>component.instance).bgClass = widget.bgClass;
				(<DownloadWidgetItemComponent>component.instance).label = widget.label;
			}
		}
	}
}
