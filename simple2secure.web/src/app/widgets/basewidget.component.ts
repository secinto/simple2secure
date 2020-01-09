import {OnInit} from "@angular/core";
import {AlertService, HttpService} from "../_services";
import {TranslateService} from "@ngx-translate/core";
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {BaseComponent} from "../components";
import {environment} from "../../environments/environment";

export class BaseWidget{

    loading = false;

    constructor(public httpService: HttpService,
                public alertService: AlertService,
                public translate: TranslateService,
                public location: Location,
                public router: Router,
                public route: ActivatedRoute,
                public baseComponent: BaseComponent) {}

    deleteWidgetProperty(widgetPropId: string) {
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'widget/delete/prop/' + widgetPropId).subscribe(
            data => {
                this.alertService.success(this.translate.instant('widget.deleted'));
                this.loading = false;
                this.baseComponent.loadAllWidgetsByUserId(this.route.component["name"]);
            },
            error => {
                if (error.status == 0) {
                    this.alertService.error(this.translate.instant('server.notresponding'));
                }
                else {
                    this.alertService.error(error.error.errorMessage);
                }
                this.loading = false;
            });
    }
}