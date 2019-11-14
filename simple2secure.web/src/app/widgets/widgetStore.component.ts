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
import {StatComponent} from './stat.component';
import {MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'widgetStore.component.html',
	selector: 'widgetStore',
	styleUrls: ['./widgets.scss', './widget-store.scss']
})

export class WidgetStoreComponent {
	loading = false;
	currentUser: any;
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
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
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
			if (widget.startTag == '<app-stat>'){
				const componentFactory = this.componentFactoryResolver.resolveComponentFactory(StatComponent);
				const component = this.container.createComponent(componentFactory);
				(<StatComponent>component.instance).bgClass = widget.bgClass;
				(<StatComponent>component.instance).icon = widget.icon;
				(<StatComponent>component.instance).count = 10;
				(<StatComponent>component.instance).label = widget.label;
			}
		}
	}

	cancel() {
		this.location.back();
	}
}
