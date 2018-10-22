import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {EmailConfiguration} from '../_models/index';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  styleUrls: ['email.component.css'],
  templateUrl: 'emailOverview.component.html',
  selector: 'emailOverview'
})
export class EmailOverviewComponent {

    config: EmailConfiguration[];
    tempConfig: EmailConfiguration;
    loading = false;
    selectedConfig: EmailConfiguration;
    currentUser: any;
    deleted = false;

    displayedColumns = ['email', 'id', 'incomingPort', 'action'];
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
            private translate: TranslateService) {}

    ngOnInit() {
        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.loadAllConfigurations();
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

    /**
     * Dummy configuration in case that we are using the mock environment
     */
    private createTempConfiguration(){
        this.tempConfig = new EmailConfiguration();
        this.tempConfig.email = 'fakeMail@secinto.com';
        this.tempConfig.id = '11111';
        this.tempConfig.incomingPort = 'testPort';
        this.tempConfig.incomingServer = 'testServer';
        this.tempConfig.outgoingPort = 'outTestPort';
        this.tempConfig.outgoingServer = 'outTestServer';
        this.tempConfig.password = 'slapdlpsad';
        this.tempConfig.userUUID = '222222';
    }

    private loadAllConfigurations() {
        if (environment.envName === 'mock'){
            this.config = [];
            this.createTempConfiguration();
            this.config.push(this.tempConfig);
            this.dataSource.data = this.config;
            this.loading = false;
            this.alertService.success(this.translate.instant('message.emailConfig'));
        }
        else{
            this.loading = true;
            this.httpService.get(environment.apiEndpoint + 'email/' + this.currentUser.userID)
              .subscribe(
              data => {
                this.config = data;
                this.dataSource.data = this.config;

                if (!this.deleted) {
                    if (data.length > 0) {
                        this.alertService.success(this.translate.instant('message.emailConfig'));
                      }
                      else {
                        this.alertService.error(this.translate.instant('message.emailConfig.notProvided'));
                      }
                      this.loading = false;
                }
                else{
                    this.deleted = false;
                    this.loading = false;
                }

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

    public onMenuTriggerClick(config: EmailConfiguration) {
      this.selectedConfig = config;
    }

    public onViewClick(){
      this.viewInbox(this.selectedConfig);
    }

    public onEditClick(){
        this.editConfig(this.selectedConfig);
    }

    public onDeleteClick(){
        this.openDialog(this.selectedConfig);
    }

    public editConfig(selectedConfig: EmailConfiguration) {
        this.dataService.set(selectedConfig);
        this.router.navigate(['edit'], {relativeTo: this.route});
    }

    public viewInbox(selectedConfig: EmailConfiguration) {
        this.router.navigate([selectedConfig.id , 'inbox'], {relativeTo: this.route});
    }

    public openDialog(config: EmailConfiguration){
        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;

        dialogConfig.data = {
                id: 1,
                title: this.translate.instant('message.areyousure'),
                content: this.translate.instant('message.emailConfig.dialog')
            };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true){
                this.deleteConfig(this.selectedConfig);
            }
          });
    }

    public deleteConfig(config: EmailConfiguration) {
        this.loading = true;
        this.httpService.delete(environment.apiEndpoint + 'email/' + config.id).subscribe(
          data => {
            this.alertService.success(this.translate.instant('message.emailConfig.delete'));
            this.deleted = true;
            this.loadAllConfigurations();
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
