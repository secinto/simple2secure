import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BaseWidget } from './basewidget.component';
import { environment } from '../../../environments/environment';
import { saveAs as importedSaveAs } from 'file-saver';
import { TranslateService } from '@ngx-translate/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BaseComponent } from './base.component';
import { HttpService } from '../../_services/http.service';
import { AlertService } from '../../_services/alert.service';

@Component({
    selector: 'app-download',
    templateUrl: './download-widget.component.html',
    styleUrls: ['./widgets.scss']
})
export class DownloadWidgetComponent extends BaseWidget {
    @Input() bgClass: string;
    @Input() icon: string;
    @Input() count: number;
    @Input() name: string;
    @Input() data: any[];
    @Input() propertiesId: string;
    @Output() event: EventEmitter<any> = new EventEmitter();
    loading = false;
    selectedGroup = 'Standard';
    downloadGroup: any;

    constructor(httpService: HttpService,
        alertService: AlertService,
        translate: TranslateService,
        location: Location,
        router: Router,
        route: ActivatedRoute,
        baseComponent: BaseComponent) {
        super(httpService, alertService, translate, location, router, route, baseComponent);
    }

    downloadLicense() {
        this.loading = true;
        this.downloadGroup = this.getGroupByName(this.selectedGroup);

        if (this.downloadGroup) {
            const apiUrl = environment.apiLicenseGroupId.replace('{groupId}', this.downloadGroup.id);
            this.httpService.getFile(apiUrl)
                .subscribe(
                    data => {
                        importedSaveAs(data, 'license-' + this.downloadGroup.id + '.zip');
                        this.loading = false;
                    },
                    error => {
                        this.alertService.showErrorMessage(error);
                        this.loading = false;
                    });
        } else {
            this.alertService.showErrorMessage(null, false, 'error.licenseDownload')
            this.loading = false;
        }
    }

    getGroupByName(name: string) {
        for (const item of this.data) {
            if (item.name === name) {
                return item;
            }
        }
        this.downloadGroup = null;
        return null;
    }
}
