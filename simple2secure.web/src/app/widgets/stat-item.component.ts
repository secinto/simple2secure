import {BaseWidgetItem} from "./baseWidgetItem.component";
import {Component} from "@angular/core";
import {DataService} from "../_services";

@Component({
    selector: 'app-stat-item',
    templateUrl: './stat-item.component.html',
    styleUrls: ['./widgets.scss']
})
export class StatItemComponent extends BaseWidgetItem{
}
