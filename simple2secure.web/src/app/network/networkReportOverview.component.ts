import {Component, Inject, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {HttpService, AlertService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {Modal} from 'ngx-modialog/plugins/bootstrap';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {NetworkReport} from '../_models/index';

@Component({
  moduleId: module.id,
  templateUrl: 'networkReportOverview.component.html'
})

export class NetworkReportOverviewComponent {
  currentUser: any;
  reports: NetworkReport[];
  selectedReport: any;
  loading = false;

  displayedColumns = ['probe', 'processorName', 'startTime', 'action'];
  dataSource = new MatTableDataSource();
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private httpService: HttpService,
    private alertService: AlertService,
    private dataService: DataService,
    public modal: Modal,
    private dialog: MatDialog,
    private translate: TranslateService) {}

  ngOnInit() {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.loadAllReports();
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

  private loadAllReports() {
    this.loading = true;
    this.httpService.get(environment.apiEndpoint + 'reports/network/' + this.currentUser.userID)
      .subscribe(
      data => {
        this.reports = data;
        this.dataSource.data = this.reports;
        if (data.length > 0) {
          this.alertService.success(this.translate.instant('message.report'));
        }
        else {
          this.alertService.error(this.translate.instant('message.report.notProvided'));
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

  public deleteReport(report: any) {
    this.httpService.delete(environment.apiEndpoint + 'packet/' + report.id).subscribe(
      data => {
        this.alertService.success(this.translate.instant('message.report.delete'));
        this.loadAllReports();
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

  public onDeleteClick(report: any){
      this.selectedReport = report;
      this.openDialog(this.selectedReport);
  }

  public openDialog(report: any){
      const dialogConfig = new MatDialogConfig();

      dialogConfig.disableClose = true;
      dialogConfig.autoFocus = true;

      dialogConfig.data = {
              id: 1,
              title: this.translate.instant('message.areyousure'),
              content: this.translate.instant('message.report.dialog')
          };

      const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

      dialogRef.afterClosed().subscribe(data => {
          if (data === true){
              this.deleteReport(this.selectedReport);
          }
        });
  }

  public showDetails(report: NetworkReport) {
      this.loading = true;
      this.dataService.set(report);
      this.router.navigate([report.id], {relativeTo: this.route});
    }
}
