import {Component, Inject, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {Email} from '../_models/index';
import {AlertService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';


@Component({
	moduleId: module.id,
	styleUrls: ['email.component.css'],
	templateUrl: 'emailInbox.component.html',
	selector: 'emailInbox',
})
export class EmailInboxComponent {

	mails: Email[];
	loading = false;
	id: string;

	displayedColumns = ['from', 'subject', 'time'];
	dataSource = new MatTableDataSource();

	@ViewChild(MatSort) sort: MatSort;
	@ViewChild(MatPaginator) paginator: MatPaginator;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private alertService: AlertService,
		private translate: TranslateService,
		private dialogRef: MatDialogRef<EmailInboxComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.mails = data.emails;
		this.dataSource.data = this.mails;
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
}
