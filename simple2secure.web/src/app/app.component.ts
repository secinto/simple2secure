
import {map, filter} from 'rxjs/operators';
import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute, Router, NavigationEnd} from '@angular/router';
import {Title} from '@angular/platform-browser';
import 'hammerjs';

@Component({
	moduleId: module.id,
	selector: 'app',
	templateUrl: 'app.component.html',
})

export class AppComponent implements OnInit {

	title = 'Simple2Secure Web';
	translatedTitle = 'Simple2Secure Web';

	constructor(
		private router: Router,
		private activatedRoute: ActivatedRoute,
		private translate: TranslateService,
		private titleService: Title)
	{
		translate.setDefaultLang('en');
	}

	ngOnInit() {
		this.router
			.events.pipe(
			filter(event => event instanceof NavigationEnd),
			map(() => {
				let child = this.activatedRoute.firstChild;
				while (child) {
					if (child.firstChild) {
						child = child.firstChild;
					} else if (child.snapshot.data && child.snapshot.data['title']) {
						return child.snapshot.data['title'];
					} else {
						return null;
					}
				}
				return null;
			}),).subscribe((title: any) => {
			if (title) {
				this.translatedTitle = this.translate.instant(title);
				this.titleService.setTitle(this.translatedTitle);
			}
			else {
				this.titleService.setTitle(title);
			}

		});
	}
}
