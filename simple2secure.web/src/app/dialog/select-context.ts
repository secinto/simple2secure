import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {ContextDTO} from '../_models';
import {HttpService} from '../_services';

@Component({
	selector: 'select-context-dialog',
	styleUrls: ['select-context.css'],
	templateUrl: 'select-context.html'
})

export class SelectContextDialog {

	context: ContextDTO;
	currentUser: any;

	constructor(private dialogRef: MatDialogRef<SelectContextDialog>,
	            private httpService: HttpService)
	{

	}

	updateContext() {
		localStorage.setItem('context', JSON.stringify(this.context));
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.httpService.updateContext(this.context.context, this.currentUser.userID);

		if (localStorage.getItem('context')) {
			this.dialogRef.close(true);
		}
		else {
			this.dialogRef.close(false);
		}
	}
}
