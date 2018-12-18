import {Component, ViewChild} from '@angular/core';
import { Location } from '@angular/common';
import {AlertService, HttpService, DataService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator} from '@angular/material';
import {environment} from '../../environments/environment';
import {TestCase, Command} from '../_models/index';
import {TranslateService} from '@ngx-translate/core';
import {ToolDTO} from '../_models/DTO/toolDTO';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterToolTestRun.component.html'
})

export class OrbiterToolTestRunComponent {

  tool: ToolDTO;
  selectedTest: TestCase;
  isTestSelected = false;
  loading = false;
  displayedColumns = ['name', 'commands', 'testResults', 'action'];
  dataSource = new MatTableDataSource();
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private alertService: AlertService,
    private httpService: HttpService,
    private translate: TranslateService,
    private dataService: DataService,
    private location: Location
  ) {
    this.tool = new ToolDTO();
  }

  ngOnInit() {
    this.tool = this.dataService.getTool();
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

  submitTestRun(){

        this.loading = true;

        this.httpService.post(this.selectedTest, environment.apiEndpoint + 'tools/' + this.tool.tool.id + '/run').subscribe(
            data => {
                this.alertService.success(this.translate.instant('test.scheduled'));

                setTimeout(() =>
                    {
                        this.loading = false;
                        this.location.back();
                    },
                    2000);

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

    onSelectChange(){
        this.isTestSelected = true;
		this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
    }

    addCommand(){
        this.selectedTest.commands.push(new Command());
    }
}
