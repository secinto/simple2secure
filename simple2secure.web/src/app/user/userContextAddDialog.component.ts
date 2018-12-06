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
    templateUrl: 'userContextAddDialog.component.html',
    selector: 'userContextAddDialog',
    providers: [DatePipe]
})

export class UserContextAddDialogComponent {
    loading = false;
    id: string;
    private sub: any;
    url: string;
    currentUser: any;
    isDialogOpen: boolean;
    context = new Context();
    contextDTO: ContextDTO;
    isNewContext: boolean;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private httpService: HttpService,
        private dataService: DataService,
        private location: Location,
        private alertService: AlertService,
        private translate: TranslateService,
        private datePipe: DatePipe,
        private dialogRef: MatDialogRef<UserContextAddDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data) {
        if (data.context != null){
            this.context = data.context;
            this.isNewContext = false;
        }
        else{
            this.isNewContext = true;
        }
    }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.id = params['id'];
        });

        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.contextDTO = JSON.parse(localStorage.getItem('context'));
    }

    saveContext() {
        this.loading = true;

        this.url = environment.apiEndpoint + 'context/add/' + this.currentUser.userID + '/' + this.contextDTO.context.id;
        this.httpService.post(this.context, this.url).subscribe(
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
