import {EventEmitter, Input, OnInit, Output} from "@angular/core";
import {AlertService, DataService, HttpService} from "../_services";
import {TranslateService} from "@ngx-translate/core";
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {HomeComponent} from "../home";
import {environment} from "../../environments/environment";
import {Widget} from "../_models/widget";
import {Notification} from "../_models";

export class BaseWidgetItem{
    widget: Widget;

    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() label: string;
    @Input() data: any[];
    @Output() event: EventEmitter<any> = new EventEmitter();

    constructor(private dataService: DataService) {}

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