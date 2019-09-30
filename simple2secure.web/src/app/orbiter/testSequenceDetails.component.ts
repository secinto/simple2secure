/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */

import {Component, Inject, ElementRef, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator, MatDialogConfig, MatDialog} from '@angular/material';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {Timeunit} from '../_models';
import {TestObjWeb} from '../_models/testObjWeb';
import {AlertService, DataService, HttpService} from '../_services/index';
import { PodDTO } from '../_models/DTO/podDTO';
import { TestSequence } from '../_models/testSequence';
import {CdkDragDrop, moveItemInArray, transferArrayItem, CdkDropList} from '@angular/cdk/drag-drop';

@Component({
	moduleId: module.id,
	templateUrl: 'testSequenceDetails.component.html',
	styleUrls: ['orbiter.css'],
})

export class TestSequenceDetailsComponent{
	loading = false;
	sequence = new TestSequence();
	tests: TestObjWeb[] = [];
	type: string;
	isTestChanged = false;
	isNewTest = false;
	url: string;
	timeUnits = Timeunit;
	pod: PodDTO;
	testNamesCurrent: string[] = [];
	elements: string[] = ["None","adapter1", "adapter2", "adapter3"];
	isPreviousTest = false;
	
  	testSequence: string[] = [];

	constructor(
		private dataService: DataService,
		private alertService: AlertService,
		private dialogRef: MatDialogRef<TestSequenceDetailsComponent>,
		private dialog: MatDialog,
		private httpService: HttpService,
		private translate: TranslateService,
		@Inject(MAT_DIALOG_DATA) data)
	{	
		
		this.type = data.type;
		if (this.type == 'new'){
			this.isNewTest = true;
		}
		else if (this.type =='edit'){
			this.isNewTest = false;
			this.sequence = data.sequence;
			this.testSequence = this.sequence.sequenceContent;
		}else {
			
		}

		//this.isNewTest = true;
	}

	extractTimeUnits(): Array<string> {
		const keys = Object.keys(this.timeUnits);
		return keys.slice();
	}
	
	ngOnInit(){
		this.pod = this.dataService.getPods();
		this.tests = this.pod.test;		
		this.testNamesCurrent = this.getTestNamesFromObject(this.tests);
	}
	
	getTestNamesFromObject(tests: TestObjWeb[]){
		let testNames: string[] = [];
		for (var testObj of tests) {
			testNames.push(testObj.name);	
		}
		return testNames; 
	} 

	dropHorizontal(event: CdkDragDrop<any[]>) {		
		if (event.previousContainer === event.container) {
			moveItemInArray(this.testSequence, event.previousIndex, event.currentIndex);
		}
		else{
			this.testSequence.push(event.previousContainer.data[event.previousIndex]);
		}	    
	  }


	drop(event: CdkDragDrop<string[]>) {
		if (event.previousContainer === event.container) {
			moveItemInArray(this.testNamesCurrent, event.previousIndex, event.currentIndex);	
		}		
		else{
			let index = this.testSequence.indexOf(event.previousContainer.data[event.previousIndex]);
			if(index !== -1){
				this.testSequence.splice(index, 1);
			}
		}	    
	  }

	clicked(event){
		console.log(event);
	}


	
	public loadTests(podId: string){
		this.loading = true;
		this.httpService.get(environment.apiEndpoint + 'test/' + podId)
			.subscribe(
				data => {
					this.tests = data;
					if (!this.isTestChanged){
						if (data.length > 0) {
							this.alertService.success(this.translate.instant('message.data'));
						}
						else {
							this.alertService.error(this.translate.instant('message.data.notProvided'));
						}
					}

					this.loading = false;
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


	public updateSaveSequence() {
		this.loading = true;
		
		if(!this.sequence.podId){
			this.sequence.podId = this.pod.pod.podId;
		}
		
		if(!(this.testSequence.length == 0)){
			this.sequence.sequenceContent = this.testSequence;
		}
		
		this.url = environment.apiEndpoint + 'sequence/add';
		this.httpService.post(this.sequence, this.url).subscribe(
			data => {
				if (this.type === 'new') {
					this.alertService.success(this.translate.instant('message.sequence.create'));
				}
				else {
					this.alertService.success(this.translate.instant('message.sequence.update'));
				}
				this.close(true);
			},
			error => {
				if (error.status == 0) {
					this.alertService.error(this.translate.instant('server.notresponding'));
				}
				else {
					this.alertService.error(error.error.errorMessage);
				}
				this.loading = false;
			}
		);
	}
	
	public close(value: boolean){
		this.dialogRef.close(value);
	}

}
