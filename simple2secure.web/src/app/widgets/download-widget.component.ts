import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BaseWidget} from "./basewidget.component";

@Component({
    selector: 'app-download-item',
    templateUrl: './download-widget.component.html',
    styleUrls: ['./widgets.scss']
})
export class DownloadWidgetComponent extends BaseWidget{
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() label: string;
    @Input() data: number;
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();
    loading = false;
}
