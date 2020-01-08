import {EventEmitter, Input, Output} from "@angular/core";
import {DataService} from "../_services";
import {Widget} from "../_models/widget";

export class BaseWidgetItem{
    widget: Widget;

    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() data: any[];
    @Output() event: EventEmitter<any> = new EventEmitter();

    constructor(private dataService: DataService) {}

    addWidget(){
        this.widget = new Widget();
        this.widget.id = this.id;
        this.widget.tag = this.tag;
        this.widget.description = this.description;
        this.widget.bgClass = this.bgClass;
        this.widget.icon = this.icon;
        this.widget.name = this.name;

        this.dataService.addWidgetToLS(this.widget);
    }
}