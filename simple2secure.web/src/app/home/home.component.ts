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

import {
	Component,
	OnInit,
	ViewChild} from '@angular/core';
import {NgxWidgetGridComponent, Rectangle, WidgetPositionChange} from 'ngx-widget-grid';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {WidgetStoreComponent} from '../widgets/widgetStore.component';
import {TranslateService} from '@ngx-translate/core';
import {AlertService, DataService, HttpService} from '../_services';
import {WidgetDTO} from '../_models/DTO/widgetDTO';
import {environment} from '../../environments/environment';

@Component({
	styleUrls: ['home.component.scss'],
	moduleId: module.id,
	templateUrl: 'home.component.html'
})

export class HomeComponent{
	widgets: WidgetDTO[] = [];
	widgetDTO: WidgetDTO;
	@ViewChild('grid') grid: NgxWidgetGridComponent;
	constructor(private dialog: MatDialog,
				private alertService: AlertService,
				private translate: TranslateService,
				private dataService: DataService,
				private httpService: HttpService) {
	}

	ngAfterViewInit(){
		this.loadAllWidgetsByUserId(); 
	}

	public loadAllWidgetsByUserId() {
		this.httpService.get(environment.apiEndpoint + 'widget/get')
			.subscribe(
				data => {
					this.widgets = data;

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

	onWidgetChange(event: WidgetPositionChange) {
		this.widgets[event.index].widgetProperties.width = event.newPosition.width;
		this.widgets[event.index].widgetProperties.height = event.newPosition.height;
		this.widgets[event.index].widgetProperties.left = event.newPosition.left;
		this.widgets[event.index].widgetProperties.top = event.newPosition.top;

		this.updateSaveWidgetPosition(this.widgets[event.index]);

	}


	updateSaveWidgetPosition(widget: WidgetDTO){
		this.httpService.post(widget, environment.apiEndpoint + 'widget/updatePosition')
			.subscribe(
				data => {
					widget.widgetProperties = data;
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

	openDialogAddWidget(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '350px';

		dialogConfig.data = {
			widgets: null
		};
		const dialogRef = this.dialog.open(WidgetStoreComponent, dialogConfig);
		this.dataService.clearWidgets();
		dialogRef.afterClosed().subscribe(result => {
			this.addWidgetsToTheList();
		});
	}

	addWidgetsToTheList(){
		if (this.dataService.getSelectedWidget() != null) {
			const position = this.grid.getNextPosition();
			if (position) {
				this.widgetDTO = new WidgetDTO();
				this.widgetDTO.widget = this.dataService.getSelectedWidget();
				this.widgetDTO.widgetProperties.height = 1;
				this.widgetDTO.widgetProperties.left = position.left;
				this.widgetDTO.widgetProperties.top = position.top;
				this.widgetDTO.widgetProperties.width = 1;
				this.widgetDTO.widgetProperties.widgetId = this.dataService.getSelectedWidget().id;

				this.widgets.push(this.widgetDTO);
			}
			else{
				this.alertService.error(this.translate.instant('widget.noplace'));
			}

			this.dataService.clearWidgets();
		}
	}

	getRectangle(widget: WidgetDTO){
		const rectangle = new Rectangle();
		rectangle.width = widget.widgetProperties.width;
		rectangle.height = widget.widgetProperties.height;
		rectangle.left = widget.widgetProperties.left;
		rectangle.top = widget.widgetProperties.top;
		return rectangle;
	}
}
