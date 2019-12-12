import {OnInit} from "@angular/core";
import {AlertService, HttpService} from "../_services";
import {TranslateService} from "@ngx-translate/core";
import {Location} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {HomeComponent} from "../home";
import {environment} from "../../environments/environment";

export class BaseWidget{

    loading = false;

    constructor(private httpService: HttpService,
                private alertService: AlertService,
                private translate: TranslateService,
                private location: Location,
                private router: Router,
                private route: ActivatedRoute,
                private homeComponent: HomeComponent ) {}

    deleteWidgetProperty(widgetPropId: string) {
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'widget/delete/prop/' + widgetPropId).subscribe(
            data => {
                this.alertService.success(this.translate.instant('widget.deleted'));
                this.loading = false;
                this.homeComponent.loadAllWidgetsByUserId();
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