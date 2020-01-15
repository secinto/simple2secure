import {Component, OnInit, ViewChild, Input} from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {AlertService, DataService, HttpService} from '../_services';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {NgxWidgetGridComponent, Rectangle, WidgetPositionChange} from 'ngx-widget-grid';
import {WidgetDTO} from '../_models/DTO/widgetDTO';
import {WidgetStoreComponent} from '../widgets/widgetStore.component';

@Component({
	selector: 'base-component-widget',
	templateUrl: 'base.component.html',
})
export class BaseComponent{

	widgets: WidgetDTO[] = [];
	widgetDTO: WidgetDTO;
	@ViewChild('grid') grid: NgxWidgetGridComponent;
	@Input() _location: string;

	constructor(public dialog: MatDialog,
				public alertService: AlertService,
				public translate: TranslateService,
				public dataService: DataService,
				public httpService: HttpService,
				public route: ActivatedRoute) {
	}

	ngAfterViewInit(){
		this.loadAllWidgetsByUserId(this._location);
	}

	public loadAllWidgetsByUserId(location: string) {
		this.httpService.get(environment.apiEndpoint + 'widget/get/' + location)
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

	public onWidgetChange(event: WidgetPositionChange) {
		this.widgets[event.index].widgetProperties.width = event.newPosition.width;
		this.widgets[event.index].widgetProperties.height = event.newPosition.height;
		this.widgets[event.index].widgetProperties.left = event.newPosition.left;
		this.widgets[event.index].widgetProperties.top = event.newPosition.top;

		this.updateSaveWidgetPosition(this.widgets[event.index]);

	}


	public updateSaveWidgetPosition(widget: WidgetDTO){
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

	public getRectangle(widget: WidgetDTO){
		const rectangle = new Rectangle();
		rectangle.width = widget.widgetProperties.width;
		rectangle.height = widget.widgetProperties.height;
		rectangle.left = widget.widgetProperties.left;
		rectangle.top = widget.widgetProperties.top;
		return rectangle;
	}

	public openDialogAddWidget(): void {
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

	public addWidgetsToTheList(){
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
				this.widgetDTO.widgetProperties.location = this._location;

				this.widgets.push(this.widgetDTO);
			}
			else{
				this.alertService.error(this.translate.instant('widget.noplace'));
			}

			this.dataService.clearWidgets();
		}
	}

}