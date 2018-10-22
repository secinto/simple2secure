import { Component, ViewChild} from '@angular/core';
import { AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {Step} from '../_models/index';

@Component({
    moduleId: module.id,
    templateUrl: 'networkConfigurationStepDetails.component.html'
})

export class NetworkConfigurationStepDetailsComponent {

    currentUser: any;
    steps: Step[];
    loading = false;
    type: number;
    groupId: string;
    probeId: string;
    deleted: boolean;
    displayedColumns = ['name', 'number', 'state', 'action'];
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private dataService: DataService,
        private router: Router,
        private dialog: MatDialog,
        private route: ActivatedRoute,
        private translate: TranslateService
    ) {}

    ngOnInit() {
        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.route.queryParams.subscribe(params => {
            this.type = params['type'];

            if (this.type == 3){
                this.groupId = params['groupId'];
            }
            else{
                this.probeId = params['probeId'];
            }
        });

        this.loadSteps();
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim();
        filterValue = filterValue.toLowerCase();
        this.dataSource.filter = filterValue;
    }

    loadSteps(){
        if (this.type == 3){
            this.loadGroupSteps();
        }
        else{
            this.loadDeviceSteps();
        }
    }

    loadDeviceSteps(){
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'steps/' + this.probeId + '/true')
            .subscribe(
                data => {
                    this.steps = data;
                    this.dataSource.data = this.steps;
                    if (data.length > 0){
                        if (this.deleted == false){
                            this.alertService.success(this.translate.instant('message.data'));
                        }
                        else{
                            this.deleted = false;
                        }
                    }
                    else{
                        this.alertService.error(this.translate.instant('message.data.notProvided'));
                    }
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

    loadGroupSteps(){
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'steps/group/' + this.groupId + '/true')
            .subscribe(
                data => {
                    this.steps = data;
                    this.dataSource.data = this.steps;
                    if (data.length > 0){
                        if (this.deleted == false){
                            this.alertService.success(this.translate.instant('message.data'));
                        }
                        else{
                            this.deleted = false;
                        }
                    }
                    else{
                        this.alertService.error(this.translate.instant('message.data.notProvided'));
                    }
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

    onMenuTriggerClick(item: any){
        this.dataService.set(item);
    }

    onEditClick(){
        if (this.type == 3){
            this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { type: this.type, groupId: this.groupId, action: 'edit' } });
        }
        else{
            this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { type: this.type, probeId: this.probeId, action: 'edit' } });
        }
    }

    onDeleteClick(){
        this.openDialog(this.dataService.get());
    }

    public openDialog(item: any){

        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;

        dialogConfig.data = {
            id: 1,
            title: this.translate.instant('message.areyousure'),
            content: this.translate.instant('message.step.dialog')
        };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true){
                this.deleteStep(item);
            }
        });
    }

    deleteStep(step: any){
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'steps/' + step.id).subscribe(
            data => {
                this.alertService.success(this.translate.instant('message.step.delete'));
                this.deleted = true;
                this.loadSteps();
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

    public addStep(){
        if (this.type == 3){
            this.router.navigate(['new'], { relativeTo: this.route, queryParams: { type: this.type, groupId: this.groupId, action: 'new' } });
        }
        else{
            this.router.navigate(['new'], { relativeTo: this.route, queryParams: { type: this.type, probeId: this.probeId, action: 'new' } });
        }

    }
}
