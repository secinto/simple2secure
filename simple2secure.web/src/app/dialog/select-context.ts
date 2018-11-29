import {Component, Inject, Injectable} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA, MatDialog} from '@angular/material';
import {Context} from '../_models';

@Component({
  selector: 'select-context-dialog',
  styleUrls: ['select-context.css'],
  templateUrl: 'select-context.html'
})

export class SelectContextDialog {

    context: Context;

  constructor(private dialogRef: MatDialogRef<SelectContextDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {

  }

  updateContext(){
    console.log('Updating context, selected context ' + JSON.stringify(this.context));
    localStorage.setItem('context', JSON.stringify(this.context));

    if (localStorage.getItem('context')){
        this.dialogRef.close(true);
    }
    else{
        this.dialogRef.close(false);
    }
  }
}
