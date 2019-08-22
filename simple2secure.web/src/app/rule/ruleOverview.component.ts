import {Component, Inject, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {AlertService, HttpService, DataService} from '../_services/index';
import {Rule} from '../_models/rule';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {ConfirmationDialog} from '../dialog/confirmation-dialog';
import {TranslateService} from '@ngx-translate/core';
import {EmailAccountAddComponent} from '../email';
import {HttpErrorResponse} from '@angular/common/http';
import { RuleAddComponent } from '.';
import {ContextDTO} from '../_models';

@Component({
	moduleId: module.id,
	templateUrl: 'ruleOverview.component.html',
	selector: 'ruleOverview'
})
export class RuleOverviewComponent {

	rules: Rule[];
	loading = false;
	selectedRule: Rule;
	toolId: string;
	currentUser: any;
	deleted = false;
	context: ContextDTO;

	displayedColumns = ['name', 'description', 'action'];// 'priority', 'action'];
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
		private translate: TranslateService)
	{

	}

	ngOnInit() {

		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadRules();
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



	private loadRules() {
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'rule/' + this.context.context.id)
			.subscribe(
				data => {
					this.rules = data;
					this.dataSource.data = this.rules;
					this.loading = false;
					this.alertService.success(this.translate.instant('message.rule'));
				},
				error => {
					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
					this.loading = false;
				});
	}

	public onMenuTriggerClick(rule: Rule) {
		this.selectedRule = rule;
	}

	public onOpenDialogAddRule(){
		this.openDialogAddRule();
	}

	public onEditClick() {
		this.editRule(this.selectedRule);
	}

	public onDeleteClick() {
		this.onDeleteDialog(this.selectedRule);
	}
/*
	public editRule(rule: Rule) {
		this.dataService.set(rule);
		this.router.navigate(['../edit'], {relativeTo: this.route, queryParams: {action: 'edit'}});
	}
*/
	private openDialogAddRule(){
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		dialogConfig.data = {
			rule: new Rule(),
		};
		const dialogRef = this.dialog.open(RuleAddComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.rule.dialog'));
				this.loadRules();
			}
			else {
                 if (result instanceof HttpErrorResponse) {
                    if (result.status == 0) {
                    	this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else {
                    	this.alertService.error(result.error.errorMessage);
                    }
                 }
			}
		});
	}


	private editRule(rule: Rule) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '500px';

		dialogConfig.data = {
			rule: rule,
		};
		const dialogRef = this.dialog.open(RuleAddComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			if (result == true) {
				this.alertService.success(this.translate.instant('message.rule.dialog'));
				this.loadRules();
			}
			else {
				if (result instanceof HttpErrorResponse) {
					if (result.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(result.error.errorMessage);
					}
				}
			}
		});
	}


	private onDeleteDialog(config: Rule) {
		const dialogConfig = new MatDialogConfig();

		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('message.areyousure'),
			content: this.translate.instant('message.rule.dialog')
		};

		const dialogRef = this.dialog.open(ConfirmationDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(data => {
			if (data === true) {
				this.deleteRule(this.selectedRule);
			}
		});
	}

	private deleteRule(rule: Rule) {
		this.loading = true;
		this.httpService.delete(environment.apiEndpoint + 'rule/' + rule.id).subscribe(
			data => {
				this.alertService.success(this.translate.instant('message.rule.delete'));
				this.deleted = true;
				this.loadRules();
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
				this.loading = false;
			});
	}
}
