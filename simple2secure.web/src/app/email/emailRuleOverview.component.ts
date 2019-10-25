import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
	moduleId: module.id,
	templateUrl: 'emailRuleOverview.component.html'
})

export class EmailRuleOverviewComponent {

	constructor(
		private route: ActivatedRoute,
		private router: Router)
	{}

	navigateTo(path: string) {
		this.router.navigate([path], {relativeTo: this.route});
	}
}
