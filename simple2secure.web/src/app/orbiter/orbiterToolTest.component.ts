import {Component, ViewChild} from '@angular/core';
import {Tool} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services';
import {TestDTO} from '../_models/DTO/testDTO';
import {OrbiterToolTestResultComponent} from './orbiterToolTestResult.component';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterToolTest.component.html'
})

export class OrbiterToolTestComponent {

    tool: Tool;
    displayedColumns = ['name', 'testResults', 'action'];
    selectedTest: TestDTO;
    loading = false;
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private dataService: DataService,
    private dialog: MatDialog,
    private translate: TranslateService,
  ) {
    this.tool = new Tool();
  }

  ngOnInit() {

    this.tool = this.dataService.getTool();
    this.dataSource.data = this.tool.tests;
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

  public onMenuTriggerClick(test: TestDTO) {
      this.selectedTest = test;
  }

  showTestResults(){
      const dialogConfig = new MatDialogConfig();
      dialogConfig.width = '450px';
      dialogConfig.data = {
          test: this.selectedTest,
      };


      const dialogRef = this.dialog.open(OrbiterToolTestResultComponent, dialogConfig);

      dialogRef.afterClosed().subscribe(result => {

      });
  }

    repeatTest(){

        this.loading = true;

        this.httpService.post(this.selectedTest.test, environment.apiEndpoint + 'tools/' + this.tool.id + '/run').subscribe(
            data => {
                this.alertService.success(this.translate.instant('test.scheduled'));
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
