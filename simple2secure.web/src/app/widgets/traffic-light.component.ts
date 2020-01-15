import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Lights} from "../_models/lights";
import {BaseWidget} from "./basewidget.component";

@Component({
    selector: 'app-traffic-light',
    templateUrl: './traffic-light.component.html',
    styleUrls: ['./widgets.scss']
})
export class TrafficLightComponent extends BaseWidget{

      public lights: Lights = {
        isRed: false,
        isYellow: false,
        isGreen: false
      };
      
    @Input() id: string;
    @Input() name: string;
    @Input() tag: string;
    @Input() description: string;
    @Input() bgClass: string;
    @Input() data: any[];
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();
}
