import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {ConfigDTO, Config} from '../_models/index';
import {AlertService, HttpService} from '../_services/index';
import {TranslateService} from '@ngx-translate/core';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';



@Component({
  moduleId: module.id,
  templateUrl: 'configurationOverview.component.html',
  styleUrls: ['configuration.component.css'],
  selector: 'configurationOverview'
})

export class ConfigurationOverviewComponent {
  config: ConfigDTO[] = [];
  loading = false;
  currentUser: any;
  type: string;
  displayedColumns = [];
  dataSource = new MatTableDataSource();
  selectedItem: Config;
  showApplyGroupConfigButton = false;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private httpService: HttpService,
    private alertService: AlertService,
    private dialog: MatDialog,
    private translate: TranslateService) {}

    ngOnInit() {
        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.type = this.route.snapshot.params.type;

        if (this.type == 'group'){
            this.displayedColumns = ['groupName', 'owner', 'version', 'action'];
        }
        else if (this.type == 'probe'){
            this.displayedColumns = ['probeId', 'groupName', 'version', 'action'];
        }

        this.loadAllConfigurations();
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

    private loadAllConfigurations() {
    this.loading = true;
    this.httpService.get(environment.apiEndpoint + 'config/dto/' + this.type + '/' + this.currentUser['userID'])
      .subscribe(
      data => {

        this.config = data;
        this.dataSource.data = this.config;
        this.alertService.success(this.translate.instant('message.configuration'));
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

    onMenuTriggerClick(config: Config) {
        this.selectedItem = config;
        if (config.groupConfiguration){
            this.showApplyGroupConfigButton = false;
        }
        else{
            this.showApplyGroupConfigButton = true;
        }
    }

    onEditClick() {
        this.router.navigate([this.selectedItem.id], {relativeTo: this.route});
    }

    onCopyClick(){
      this.openDialog();
    }

    openDialog() {
        const dialogConfig = new MatDialogConfig();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = true;
        dialogConfig.data = {
            id: 1,
            title: this.translate.instant('message.areyousure'),
            content: this.translate.instant('message.copy.config.dialog')
        };

        const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(data => {
            if (data === true){
                this.copyCurrentGroupConfig();
            }
        });

    }

    copyCurrentGroupConfig(){
        this.httpService.post(this.selectedItem, environment.apiEndpoint + 'config/update/group').subscribe(
            data => {
                this.selectedItem = data;
                this.alertService.success(this.translate.instant('message.user.create'));
                this.loadAllConfigurations();
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
