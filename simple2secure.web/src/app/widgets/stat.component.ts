import {Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {BaseWidget} from "./basewidget.component";

@Component({
    selector: 'app-stat',
    templateUrl: './stat.component.html',
    styleUrls: ['./widgets.scss']
})
export class StatComponent extends BaseWidget{
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() label: string;
    @Input() data: number;
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();
    loading = false;
}
