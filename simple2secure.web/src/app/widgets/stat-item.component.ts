import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {WidgetStoreComponent} from './widgetStore.component';
import {Widget} from '../_models/widget';
import {DataService} from '../_services';

@Component({
    selector: 'app-stat-item',
    templateUrl: './stat-item.component.html',
    styleUrls: ['./widgets.scss']
})
export class StatItemComponent implements OnInit {
    widget: Widget;

    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() label: string;
    @Input() data: number;
    @Output() event: EventEmitter<any> = new EventEmitter();

    constructor(private dataService: DataService) {}

    ngOnInit() {}

    addWidget(){
        this.widget = new Widget();
        this.widget.id = this.id;
        this.widget.tag = this.tag;
        this.widget.description = this.description;
        this.widget.label = this.label;
        this.widget.bgClass = this.bgClass;
        this.widget.icon = this.icon;
        this.widget.name = this.name;

        this.dataService.addWidgetToLS(this.widget);

    }
}
