import {Component, Inject} from '@angular/core';
import {Context, CompanyGroup, ContextDTO} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {DatePipe} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
    moduleId: module.id,
    templateUrl: 'userGroupDialog.component.html',
    selector: 'UserGroupDialogComponent',
    providers: [DatePipe]
})

export class UserGroupDialogComponent {
    public group: CompanyGroup;
    loading = false;
    id: string;
    private sub: any;
    url: string;
    currentUser: any;
    isDialogOpen: boolean;
    parentGroup: CompanyGroup;
    parentGroupId: string;
    context: ContextDTO;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private httpService: HttpService,
        private dataService: DataService,
        private location: Location,
        private alertService: AlertService,
        private translate: TranslateService,
        private datePipe: DatePipe,
        private dialogRef: MatDialogRef<UserGroupDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
            this.group = new CompanyGroup();
            this.parentGroup = data;
    }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.id = params['id'];
        });

        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.context = JSON.parse(localStorage.getItem('context'));
    }

    saveGroup() {
        this.loading = true;
        if (!this.parentGroup){
            this.parentGroupId = null;
        }
        else{
            this.parentGroupId = this.parentGroup.id;
        }

        this.url = environment.apiEndpoint + 'group/' + this.currentUser.userID + '/' + this.parentGroupId + '/'
            + this.context.context.id;
        this.httpService.post(this.group, this.url).subscribe(
            data => {
                this.dialogRef.close(true);
            },
            error => {
                this.dialogRef.close(error);
                this.loading = false;
            });
    }

    cancel(){
        this.location.back();
    }
}
