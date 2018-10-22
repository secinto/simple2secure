import {Component, ViewChild} from '@angular/core';
import {AlertService, HttpService, DataService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';

@Component({
    moduleId: module.id,
    templateUrl: 'osqueryConfigurationDetails.component.html'
})

export class OsqueryConfigurationDetailsComponent {

    displayedColumns = ['name', 'query', 'runAlways', 'interval', 'active', 'action'];
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

    currentUser: any;
    queries: any[];
    loading = false;
    type: number;
    deleted: boolean;
    private sub: any;
    currentProbe: any;
    groupId: string;
    probeId: string;


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
            if (this.type == 1){
                this.groupId = params['groupId'];
            }
            else{
                this.probeId = params['probeId'];
            }
        });

        this.loadQueries();
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

    loadQueries(){
        if (this.type == 1){
            this.loadGroupQueries();
        }
        else{
            this.loadDeviceQueries();
        }
    }

    loadDeviceQueries(){
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'config/query/' + this.probeId + '/true')
            .subscribe(
                data => {
                    this.queries = data;
                    this.dataSource.data = this.queries;
                    if (data.length > 0){
                        if (this.deleted == false){
                            this.alertService.success(this.translate.instant('message.data'));
                        }
                        else{
                            this.deleted = true;
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

    loadGroupQueries(){
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'config/query/group/' + this.groupId + '/true')
            .subscribe(
                data => {
                    this.queries = data;
                    this.dataSource.data = this.queries;
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
        this.router.navigate(['edit'], { relativeTo: this.route, queryParams: { action: 'edit' } });
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
            content: this.translate.instant('message.config.dialog')
        };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true){
                this.deleteConfig(item);
            }
        });
    }

    deleteConfig(queryConfig: any){
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'config/query/' + queryConfig.id).subscribe(
            data => {
                this.alertService.success(this.translate.instant('message.config.delete'));
                this.deleted = true;
                this.loadQueries();
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



    addQuery(){
        if (this.type == 1){
            this.router.navigate(['new'], { relativeTo: this.route, queryParams: { type: this.type, groupId: this.groupId , action: 'new' } });
        }
        else{
            this.router.navigate(['new'], { relativeTo: this.route, queryParams: { type: this.type, probeId: this.probeId, action: 'new' } });
        }
    }
}
