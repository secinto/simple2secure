import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {WidgetStoreComponent} from './widgetStore.component';
import {Widget} from '../_models/widget';
import {DataService} from '../_services';
import {BaseWidgetItem} from "./baseWidgetItem.component";

@Component({
    selector: 'app-stat-item',
    templateUrl: './stat-item.component.html',
    styleUrls: ['./widgets.scss']
})
export class StatItemComponent extends BaseWidgetItem{
}
