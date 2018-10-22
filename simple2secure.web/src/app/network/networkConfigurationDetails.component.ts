import { Component} from '@angular/core';
import { AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';

@Component({
    moduleId: module.id,
    templateUrl: 'networkConfigurationDetails.component.html'
})

export class NetworkConfigurationDetailsComponent {

    currentUser: any;
    queries: any[];
    loading = false;
    type: number;
    private sub: any;


    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private dataService: DataService,
        private router: Router,
        private route: ActivatedRoute,
        private translate: TranslateService
    ) {}

    ngOnInit() {
        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.route.queryParams.subscribe(params => {
            this.type = params['type'];
        });

        if (this.type == 1) {
            this.loadQueries();
        } else {
            this.loadDeviceQueries();
        }
    }

    loadDeviceQueries() {
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'config/query/client/' + DataService.getProbe()['id'] + '/true')
            .subscribe(
                data => {
                    this.queries = data;
                    if (data.length > 0) {
                        this.alertService.success(this.translate.instant('message.data'));
                    } else {
                        this.alertService.error(this.translate.instant('message.data.notProvided'));
                    }
                },
                error => {
                    if (error.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(error.error.errorMessage);
                    }
                    this.loading = false;
                });
    }

    loadQueries() {
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'config/query/user/' + this.currentUser['userID'] + '/true')
            .subscribe(
                data => {
                    this.queries = data;
                    if (data.length > 0) {
                        this.alertService.success(this.translate.instant('message.data'));
                    } else {
                        this.alertService.error(this.translate.instant('message.data.notProvided'));
                    }

                },
                error => {
                    if (error.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(error.error.errorMessage);
                    }
                    this.loading = false;
                });
    }

    editConfig(item: any) {
        this.dataService.set(item);
        this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { action: 'edit' } });
    }

    addQuery() {
        this.router.navigate(['new'], { relativeTo: this.route, queryParams: { type: this.type, action: 'new' } });
    }
}
