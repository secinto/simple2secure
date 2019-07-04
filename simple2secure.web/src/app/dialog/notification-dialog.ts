import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ContextDTO} from '../_models';
import {HttpService} from '../_services';

@Component({
	selector: 'notification-dialog',
	styleUrls: ['notification-dialog.css'],
	templateUrl: 'notification-dialog.html'
})

export class NotificationDialog {

	constructor(private dialogRef: MatDialogRef<NotificationDialog>,
	            private httpService: HttpService,
	            @Inject(MAT_DIALOG_DATA) public data: any)
	{

	}
}
