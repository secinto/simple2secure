import {Component, ViewChild} from '@angular/core';
import {AlertService, HttpService, DataService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {Tool, Test, TestResult, Command, QueryRun} from '../_models/index';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterToolTestRun.component.html'
})

export class OrbiterToolTestRunComponent {

  tool: Tool;
  id: string;
  private sub: any;
  action: string;
  testResult: TestResult;
  selectedTest: Test;
  isTestSelected: boolean;
  genericTests: Test[];
  customTests: Test[];
  customTestExecuted: boolean;
  displayedColumns = ['name', 'commands', 'testResults', 'action'];
  dataSource = new MatTableDataSource();
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private translate: TranslateService,
    private dataService: DataService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.tool = new Tool();
    this.genericTests = new Array();
    this.customTests = new Array();
  }

  testExecuted = false;
  loading = false;

  ngOnInit() {

    this.isTestSelected = false;
    this.customTestExecuted = false;


    this.tool = DataService.getTool();

    for (let i = 0; i < this.tool.tests.length; i++){
        if (this.tool.tests[i].customTest == false){
            this.genericTests.push(this.tool.tests[i]);
        }
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

  submitRunTest(){

        this.loading = true;
        this.tool.tests.push(this.selectedTest);

        this.httpService.post(this.selectedTest, environment.apiEndpoint + 'tools/' + this.tool.name + '/run').subscribe(
            data => {
                this.testResult = data;
                this.testExecuted = true;
                this.loading = false;
            },
            error => {
                if (error.status == 0){
                    this.alertService.error(this.translate.instant('server.notresponding'));
                }
                else{
                    this.alertService.error(error.error.errorMessage);
                }
                this.testExecuted = true;
                this.loading = false;
            });
    }

    onSelectChange(){
        this.isTestSelected = true;
        this.getCustomTests();
		this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
    }

    getCustomTests(){
       this.customTests = [];
       for (let i = 0; i < this.tool.tests.length; i++){
        if (this.tool.tests[i].customTest == true){
            this.customTests.push(this.tool.tests[i]);
        }
       }
	   this.dataSource.data = this.customTests;
    }

    addCommand(){
        this.selectedTest.commands.push(new Command());
        this.selectedTest.customTest = true;
    }

    runTest(test: Test, createInstance: boolean){
       this.selectedTest = test;
       this.selectedTest.createInstance = createInstance;

       if (createInstance){
           this.customTestExecuted = false;
       }
       else{
           this.customTestExecuted = true;
       }

       this.submitRunTest();
    }
}
