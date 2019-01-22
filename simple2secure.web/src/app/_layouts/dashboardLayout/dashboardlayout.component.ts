import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';



import {Title} from '@angular/platform-browser';

@Component({
	moduleId: module.id,
	templateUrl: 'dashboardlayout.component.html',
	selector: 'dashboard'
})

export class DashboardLayoutComponent {

	pageTitle: string;

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private titleService: Title
	)
	{
	}

	ngOnInit() {

	}

	ngDoCheck() {
		this.pageTitle = this.titleService.getTitle();
	}

}
