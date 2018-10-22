import { Component, ViewChild} from '@angular/core';
import {AlertService, DataService, HttpService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {Tool, User} from '../_models/index';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';

@Component({
    moduleId: module.id,
    templateUrl: 'orbiterTools.component.html'
})

export class OrbiterToolsComponent {

    currentUser: any;
    tools: Tool[];
    selectedTool: Tool;
    loading = false;
    type: number;
    private sub: any;
    displayedColumns = ['name', 'length', 'state', 'action'];
    dataSource = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor(
        private alertService: AlertService,
        private httpService: HttpService,
        private router: Router,
        private dataService: DataService,
        private route: ActivatedRoute,
        private translate: TranslateService
    ) {}

    ngOnInit() {
        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.loadTools();
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

    loadTools(){
        this.loading = true;
        this.httpService.get(environment.apiEndpoint + 'tools')
            .subscribe(
                data => {
                    this.tools = data;
                    this.dataSource.data = this.tools;
                    if (data.length > 0){
                        this.alertService.success(this.translate.instant('message.data'));
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

    public onMenuTriggerClick(tool: Tool) {
        this.selectedTool = tool;
     }

    runTests(){
        DataService.setTool(this.selectedTool);
        this.router.navigate(['test/run'], { relativeTo: this.route});
    }

    showTests(){
        DataService.setTool(this.selectedTool);
        this.router.navigate(['test'], {relativeTo: this.route});
    }
}
