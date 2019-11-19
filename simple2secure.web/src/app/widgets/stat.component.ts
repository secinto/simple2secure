import {Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {AlertService, HttpService} from '../_services';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {Location } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'app-stat',
    templateUrl: './stat.component.html',
    styleUrls: ['./widgets.scss']
})
export class StatComponent implements OnInit {
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() label: string;
    @Input() data: number;
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();
    loading = false;

    constructor(private httpService: HttpService,
                private alertService: AlertService,
                private translate: TranslateService,
                private location: Location,
                private router: Router,
                private route: ActivatedRoute) {}

    ngOnInit() {
    }

    deleteWidgetProperty(widgetPropId: string) {
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'widget/delete/prop/' + widgetPropId).subscribe(
            data => {
                this.alertService.success(this.translate.instant('widget.deleted'));
                this.loading = false;
                this.refreshPage();
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

    refreshPage(){
        this.router.navigate([this.router.url]);
    }
}
