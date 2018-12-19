import {HttpErrorResponse} from '@angular/common/http';
import { Component, ViewChild} from '@angular/core';
import {AlertService, DataService, HttpService} from '../_services';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {ContextDTO} from '../_models/index';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {ToolDTO} from '../_models/DTO/toolDTO';
import {OrbiterToolTestRunComponent} from './orbiterToolTestRun.component';

@Component({
    moduleId: module.id,
    templateUrl: 'orbiterTools.component.html'
})

export class OrbiterToolsComponent {

    currentUser: any;
    tools: ToolDTO[];
    selectedTool: ToolDTO;
    loading = false;
    type: number;
    context: ContextDTO;
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
        private dialog: MatDialog,
        private translate: TranslateService
    ) {}

    ngOnInit() {
        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.context = JSON.parse(localStorage.getItem('context'));
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
        this.httpService.get(environment.apiEndpoint + 'tools/' + this.context.context.id)
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

    public onMenuTriggerClick(tool: ToolDTO) {
        this.selectedTool = tool;
     }

    showTests(){
        this.dataService.setTool(this.selectedTool);
        this.router.navigate(['test'], {relativeTo: this.route});
    }

    showTemplates(){
        this.dataService.setTool(this.selectedTool);
        this.router.navigate(['template'], {relativeTo: this.route});
    }

	openDialogScheduleTest(): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		dialogConfig.data = {
			tool: this.selectedTool,
		};
		const dialogRef = this.dialog.open(OrbiterToolTestRunComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true){
				this.alertService.success(this.translate.instant('test.scheduled'));
				this.loadTools();
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
