import {Component, Inject} from '@angular/core';
import {CompanyGroup} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {DatePipe} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
    moduleId: module.id,
    templateUrl: 'userGroupApplyConfig.component.html',
    selector: 'UserGroupApplyConfig',
    providers: [DatePipe]
})

export class UserGroupApplyConfigComponent {
    public destGroup: CompanyGroup;
    groups: CompanyGroup[];
    sourceGroup: CompanyGroup;
    url: string;
    loading = false;
    currentUser: any;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private httpService: HttpService,
        private dataService: DataService,
        private location: Location,
        private alertService: AlertService,
        private translate: TranslateService,
        private datePipe: DatePipe,
        private dialogRef: MatDialogRef<UserGroupApplyConfigComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
            this.destGroup = data.destGroup;
    }

    ngOnInit() {
        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.loadGroups();
    }

    private loadGroups() {
        this.httpService.get(environment.apiEndpoint + 'users/group/user/' + this.currentUser.userID)
            .subscribe(
                data => {
                    this.groups = data;
                },
                error => {

                    if (error.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(error.error.errorMessage);
                    }
                });
    }

    applyConfig() {
        this.url = environment.apiEndpoint + 'config/copy/' + this.sourceGroup.id;
        this.httpService.post(this.destGroup, this.url).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
                this.loading = false;
            });
    }
}
