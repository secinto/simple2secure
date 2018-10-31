import { Component, ViewChild} from '@angular/core';
import { AlertService, HttpService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {Processor} from '../_models';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {NetworkProcessorConfigurationEditComponent} from './networkProcessorConfigurationEdit.component';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
    moduleId: module.id,
    selector: 'networkConfigurationProcessor',
    templateUrl: 'networkConfigurationProcessorDetails.component.html'
})

export class NetworkConfigurationProcessorDetailsComponent {

    currentUser: any;
    processors: Processor[];
    private sub: any;
    loading = false;
    type: number;
    groupId: string;
    deleted = false;
    added = false;
    selectedItem: Processor;
    displayedColumns = ['name', 'class', 'interval', 'packet', 'action'];
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
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

        this.loadProcessors();
    }

    loadProcessors(){
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'processors/group/' + this.groupId)
            .subscribe(
                data => {
                    this.processors = data;
                    this.dataSource.data = this.processors;
                    if (data.length > 0) {
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

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.dataSource.filter = filterValue;
    }

    onMenuTriggerClick(item: any){
        this.selectedItem = item;
    }

    onEditClick() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            processor: this.selectedItem,
            groupId: this.groupId
        };

        const dialogRef = this.dialog.open(NetworkProcessorConfigurationEditComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.processor.update'));
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

    onAddClick() {

        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        dialogConfig.data = {
            processor: null,
            groupId: this.groupId
        };
        const dialogRef = this.dialog.open(NetworkProcessorConfigurationEditComponent, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            if (result == true){
                this.alertService.success(this.translate.instant('message.processor.add'));
                this.added = true;
                this.loadProcessors();
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
            content: this.translate.instant('message.processor.dialog')
        };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true){
                this.deleteProcessor(item);
            }
        });
    }

    deleteProcessor(processor: any){
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'processors/' + processor.id).subscribe(
            data => {
                this.alertService.success(this.translate.instant('message.processor.delete'));
                this.deleted = true;
                this.loadProcessors();
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
