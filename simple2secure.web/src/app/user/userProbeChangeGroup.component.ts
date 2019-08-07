import {Component, Inject} from '@angular/core';
import {CompanyGroup, Probe, ContextDTO} from '../_models/index';
import {AlertService, DataService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Location} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
	moduleId: module.id,
	templateUrl: 'userProbeChangeGroup.component.html',
	selector: 'UserProbeChangeGroupComponent',
})

export class UserProbeChangeGroupComponent {
	loading = false;
	id: string;
	url: string;
	currentUser: any;
	groups: CompanyGroup[];
	probe: Probe;
	selectedGroup: CompanyGroup;
	context: ContextDTO;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private httpService: HttpService,
		private dataService: DataService,
		private location: Location,
		private alertService: AlertService,
		private translate: TranslateService,
		private dialogRef: MatDialogRef<UserProbeChangeGroupComponent>,
		@Inject(MAT_DIALOG_DATA) data)
	{
		this.probe = data;
		this.selectedGroup = this.probe.group;
	}

	ngOnInit() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadGroups();

	}

	private loadGroups() {
		this.httpService.get(environment.apiEndpoint + 'group/context/' + this.context.context.id)
			.subscribe(
				data => {
					this.extractGroups(data);
				},
				error => {

					if (error.status == 0) {
						this.alertService.error(this.translate.instant('server.notresponding'));
					}
					else {
						this.alertService.error(error.error.errorMessage);
					}
				});
	}

	public changeGroup() {

		this.loading = true;

		this.url = environment.apiEndpoint + 'probe/changeGroup/' + this.probe.probeId;
		this.httpService.post(this.selectedGroup, this.url).subscribe(
			data => {
				this.dialogRef.close(true);
			},
			error => {
				this.dialogRef.close(error);
				this.loading = false;
			});
	}

	extractGroups(groups: CompanyGroup[]) {
		this.groups = [];
		for (let i = 0; i < groups.length; i++) {
			if (groups[i].id == this.selectedGroup.id) {
			}
			else {
				this.groups.push(groups[i]);
			}
		}
	}
}
