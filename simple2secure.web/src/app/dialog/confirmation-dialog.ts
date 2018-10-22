import {Component, Inject, Injectable} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA, MatDialog} from '@angular/material';

@Component({
  selector: 'dialog-overview-example',
  styleUrls: ['confirmation-dialog.css'],
  templateUrl: 'confirmation-dialog.html'
})

export class ConfirmationDialog {

  constructor(private dialogRef: MatDialogRef<ConfirmationDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {

  }

  public close(value: boolean){
    this.dialogRef.close(value);
  }
}
