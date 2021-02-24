import { TranslateService } from '@ngx-translate/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { IChartistAnimationOptions } from 'chartist';
import { ChartEvent } from 'ng-chartist';
import { BaseComponent } from './base.component';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';

export class BaseWidget {

    loading = false;

    constructor(public httpService: HttpService,
        public alertService: AlertService,
        public translate: TranslateService,
        public location: Location,
        public router: Router,
        public route: ActivatedRoute,
        public baseComponent: BaseComponent) {
    }

    deleteWidgetProperty(widgetPropId: string) {
        this.loading = true;
        const apiUrl = environment.apiWidgetDeleteProp.replace('{widgetPropId}', widgetPropId);
        this.httpService.delete(apiUrl).subscribe(
            data => {
                this.alertService.showSuccessMessage(data, 'widget.deleter');
                this.loading = false;
                this.baseComponent.loadAllWidgetsByUserId(this.route.snapshot.data['dashboardName']);
            },
            error => {
                this.alertService.showErrorMessage(error);
                this.loading = false;
            });
    }

    events: ChartEvent = {
        draw: (data) => {
            if (data.type === 'bar' || data.type === 'line') {
                data.element.animate({
                    y2: <IChartistAnimationOptions>{
                        dur: '0.5s',
                        from: data.y1,
                        to: data.y2,
                        easing: 'easeOutQuad'
                    }
                });
            }
        }
    };
}
