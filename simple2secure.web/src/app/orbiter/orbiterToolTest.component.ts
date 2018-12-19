import {HttpErrorResponse} from '@angular/common/http';
import {Component, ViewChild} from '@angular/core';
import {Tool} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services';
import {TestDTO} from '../_models/DTO/testDTO';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {OrbiterTestTemplateComponent} from './orbiterTestTemplate.component';
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

      this.dialog.open(OrbiterToolTestResultComponent, dialogConfig);

  }

	public openDialogDeleteTest(){
		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.test.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true){
				this.deleteTest();
			}
		});
	}

	public deleteTest() {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'tools/delete/test/' + this.selectedTest.test.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.test.delete'));
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

	showTestDetails(){
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		dialogConfig.data = {
			template: this.selectedTest.test,
			isTestTemplate: false,
			isTestRun: false
		};
		const dialogRef = this.dialog.open(OrbiterTestTemplateComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true){
				this.alertService.success(this.translate.instant('test.template.update'));
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
}
