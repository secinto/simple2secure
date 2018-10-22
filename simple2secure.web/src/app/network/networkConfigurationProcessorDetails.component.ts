import { Component, ViewChild} from '@angular/core';
import { AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {Processor} from '../_models';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';

@Component({
    moduleId: module.id,
    templateUrl: 'networkConfigurationProcessorDetails.component.html'
})

export class NetworkConfigurationProcessorDetailsComponent {

    currentUser: any;
    processors: Processor[];
    loading = false;
    type: number;
    groupId: string;
    probeId: string;
    deleted: boolean;
    displayedColumns = ['name', 'class', 'interval', 'packet', 'action'];
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

        this.loadProcessors();
    }

    loadProcessors(){
        if (this.type == 3) {
            this.loadGroupProcessors();
        }
        else {
            this.loadDeviceProcessors();
        }
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

    loadDeviceProcessors() {
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'processors/' + this.probeId)
            .subscribe(
                data => {
                    this.processors = data;
                    this.dataSource.data = this.processors;
                    if (data.length > 0) {
                        if (this.deleted == false){
                            this.alertService.success(this.translate.instant('message.data'));
                        }
                        else{
                            this.deleted = false;
                        }
                    }
                    else {
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

    loadGroupProcessors() {
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'processors/group/' + this.groupId)
            .subscribe(
                data => {
                    this.processors = data;
                    this.dataSource.data = this.processors;
                    if (data.length > 0) {
                        if (this.deleted == false){
                            this.alertService.success(this.translate.instant('message.data'));
                        }
                        else{
                            this.deleted = false;
                        }
                    }
                    else {
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

    onEditClick() {
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

    addProcessor() {
        if (this.type == 3){
            this.router.navigate(['new'], { relativeTo: this.route, queryParams: { type: this.type, groupId: this.groupId, action: 'new' } });
        }
        else{
            this.router.navigate(['new'], { relativeTo: this.route, queryParams: { type: this.type, probeId: this.probeId, action: 'new' } });
        }

    }
}
