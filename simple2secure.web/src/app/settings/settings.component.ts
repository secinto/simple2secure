import {Component} from '@angular/core';
import {MatDialog} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {Settings, Timeunit} from '../_models';
import {environment} from '../../environments/environment';

@Component({
  moduleId: module.id,
  styleUrls: ['settings.component.css'],
  templateUrl: 'settings.component.html',
  selector: 'settings'
})
export class SettingsComponent {

    loading = false;
    currentUser: any;
    settingsObj = Settings;
    timeUnits = Timeunit;
    updated = false;

    constructor(
            private route: ActivatedRoute,
            private router: Router,
            private httpService: HttpService,
            private alertService: AlertService,
            private dataService: DataService,
            private dialog: MatDialog,
            private translate: TranslateService) {}

    ngOnInit() {
        this.loadSettings();
      }

    extractTimeUnits(): Array<string> {
        const keys = Object.keys(this.timeUnits);
        return keys.slice();
    }

    loadSettings() {
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'settings')
            .subscribe(
                data => {
                    this.settingsObj = data;
                    if (this.updated){
                        this.alertService.success(this.translate.instant('message.settings.update'));
                    }
                    else{
                        this.alertService.success(this.translate.instant('message.data'));
                    }

                    this.updated = true;
                    this.loading = false;
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

    updateSettings(){
        this.loading = true;
        this.httpService.post(this.settingsObj, environment.apiEndpoint + 'settings').subscribe(
            data => {
                this.settingsObj = data;
                this.updated = true;
                this.loadSettings();
            },
            error => {
                if (error.status == 0){
                    this.alertService.error(this.translate.instant('server.notresponding'));
                }
                else{
                    this.alertService.error(error.error.errorMessage);
                }
            });
        this.loading = false;
    }
}
