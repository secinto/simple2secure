import {Component} from '@angular/core';
import {BaseWidgetItem} from "./baseWidgetItem.component";
import {Lights} from "../_models/lights"

@Component({
    selector: 'app-traffic-lights-item',
    templateUrl: './traffic-light-item.component.html',
    styleUrls: ['./widgets.scss']
})
export class TrafficLightItemComponent extends BaseWidgetItem {
	public lights: Lights = {
		isRed: false,
		isYellow: false,
		isGreen: false
	};
}
