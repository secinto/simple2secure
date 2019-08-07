import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {ContextDTO} from '../_models';
import {HttpService} from '../_services';

@Component({
	selector: 'add-query-dialog',
	templateUrl: 'addQueryDialog.html'
})

export class AddQueryDialog {

	selectedQuery: any;

	constructor(private dialogRef: MatDialogRef<AddQueryDialog>,
	            private httpService: HttpService,
	            @Inject(MAT_DIALOG_DATA) public data: any)
	{

	}

	selectQuery() {
		this.httpService.post(this.selectedQuery.sqlQuery, environment.apiEndpoint + 'reports/report/name')
			.subscribe(
				dataAPI => {
					this.dialogRef.close(dataAPI);
				});
	}
}
