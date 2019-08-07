import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
	moduleId: module.id,
	templateUrl: 'orbiterOverview.component.html'
})

export class OrbiterOverviewComponent {

	constructor(
		private route: ActivatedRoute,
		private router: Router)
	{}

	navigateTo(path: string) {
		this.router.navigate([path], {relativeTo: this.route});
	}
}
