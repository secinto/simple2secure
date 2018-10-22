import {Component, ViewChild} from '@angular/core';
import {QueryRun, Tool, Test, TestResult, Command} from '../_models/index';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterToolTest.component.html'
})

export class OrbiterToolTestComponent {

    tool: Tool;
    customTests: Test[];
    displayedColumns = ['name', 'commands', 'testResults', 'action'];
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private dataService: DataService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.tool = new Tool();
    this.customTests = new Array();
  }

  testExecuted = false;
  loading = false;

  ngOnInit() {
    this.tool = DataService.getTool();
    this.getCustomTests();
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

  showTestResults(item: Tool){
      this.dataService.set(item);
      this.router.navigate(['result'], { relativeTo: this.route});
  }

  getCustomTests(){
      this.customTests = [];
      for (let i = 0; i < this.tool.tests.length; i++){
       if (this.tool.tests[i].customTest == true){
           this.customTests.push(this.tool.tests[i]);
       }

       this.dataSource.data = this.customTests;
      }
   }
}
