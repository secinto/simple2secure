import { Component, ViewChild} from '@angular/core';
import { AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {Step} from '../_models/index';
import {HttpErrorResponse} from '@angular/common/http';
import {NetworkStepConfigurationEditComponent} from './networkStepConfigurationEdit.component';

@Component({
    moduleId: module.id,
    selector: 'networkConfigurationStep',
    templateUrl: 'networkConfigurationStepDetails.component.html'
})

export class NetworkConfigurationStepDetailsComponent {

    currentUser: any;
    private sub: any;
    steps: Step[];
    selectedItem: Step;
    loading = false;
    type: number;
    groupId: string;
    deleted = false;
    added = false;
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

        this.sub = this.route.params.subscribe(params => {
            this.groupId = params['id'];
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
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'steps/group/' + this.groupId + '/true')
            .subscribe(
                data => {
                    this.steps = data;
                    this.dataSource.data = this.steps;
                    if (data.length > 0){
                        if (this.deleted == false && this.added == false){
                            this.alertService.success(this.translate.instant('message.data'));
                        }
                        else{
                            this.deleted = false;
                            this.added = false;
                        }
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
        this.selectedItem = item;
    }

    onEditClick(){
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            step: this.selectedItem,
            groupId: this.groupId
        };

        const dialogRef = this.dialog.open(NetworkStepConfigurationEditComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.step.update'));
            }
            else{
                if (result instanceof HttpErrorResponse){
                    if (result.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(result.error.errorMessage);
                    }
                }
            }
        });
    }

    public onAddClick(){
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            step: null,
            groupId: this.groupId
        };

        const dialogRef = this.dialog.open(NetworkStepConfigurationEditComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.step.add'));
                this.added = true;
                this.loadSteps();
            }
            else{
                if (result instanceof HttpErrorResponse){
                    if (result.status == 0){
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else{
                        this.alertService.error(result.error.errorMessage);
                    }
                }
            }
        });
    }

    onDeleteClick(){
        this.openDialog(this.selectedItem);
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
}
