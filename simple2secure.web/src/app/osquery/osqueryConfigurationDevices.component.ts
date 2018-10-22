import {Component, ViewChild} from '@angular/core';
import {Probe} from '../_models/index';
import {ActivatedRoute, Router} from '@angular/router';
import {AlertService, DataService, HttpService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';

@Component({
  moduleId: module.id,
  templateUrl: 'osqueryConfigurationDevices.component.html'
})

export class OsqueryConfigurationDevicesComponent {

  currentUser: any;
  probes: Probe[];
  loading = false;
  selectedItem: Probe;
  type: number;
  displayedColumns = ['name', 'groupName', 'activated', 'action'];
  dataSource = new MatTableDataSource();
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private httpService: HttpService,
    private alertService: AlertService,
    private dataService: DataService,
    private dialog: MatDialog,
    private translate: TranslateService
  ) {}

    ngOnInit() {

      this.route.queryParams.subscribe(params => {
          this.type = params['type'];
      });

      this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
      this.loadDevices();
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

    loadDevices() {
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'users/devices/' + this.currentUser.userID)
        .subscribe(
      data => {
            this.probes = data;
            this.dataSource.data = this.probes;
            if (data.length > 0) {
                this.alertService.success(this.translate.instant('message.data'));
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

    onEditClick() {
        if (this.type == 4){
            this.router.navigate(['../type'], {relativeTo: this.route, queryParams: {type: this.type, probeId: this.selectedItem.probeId}});
        }
        else{
            this.router.navigate(['../details'], {relativeTo: this.route, queryParams: {type: 2, probeId: this.selectedItem.probeId}});
        }
    }

    onCopyClick(){
        this.openDialog();
    }

    openDialog() {
        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;
        if (this.type == 4){
            dialogConfig.data = {
                id: 1,
                title: this.translate.instant('message.areyousure'),
                content: this.translate.instant('message.copy.stepsAndProcessor.dialog')
            };
        }
        else{
            dialogConfig.data = {
                id: 1,
                title: this.translate.instant('message.areyousure'),
                content: this.translate.instant('message.copy.osqueryconfig.dialog')
            };
        }


        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true){
                if (this.type == 4){
                    this.copyCurrentGroupStepsAndProcessors();
                }
                else{
                    this.copyCurrentGroupConfig();
                }

            }
        });

    }

    copyCurrentGroupStepsAndProcessors(){
        this.httpService.post(this.selectedItem, environment.apiEndpoint + 'processors/update/group').subscribe(
            data => {
                this.selectedItem = data;
                this.alertService.success(this.translate.instant('message.user.create'));
                this.loadDevices();
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

    copyCurrentGroupConfig(){
        this.httpService.post(this.selectedItem, environment.apiEndpoint + 'config/query/update/group').subscribe(
            data => {
                this.selectedItem = data;
                this.alertService.success(this.translate.instant('message.user.create'));
                this.loadDevices();
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

    onMenuTriggerClick(probe: Probe) {
        this.selectedItem = probe;
    }
}
