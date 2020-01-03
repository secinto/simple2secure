
import {stringify} from 'querystring';
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

	title = 'simple2secure Portal';
	translatedTitle = 'simple2secure Portal';

	constructor(
		private router: Router,
		private activatedRoute: ActivatedRoute,
		private translate: TranslateService,
		private titleService: Title)
	{
		translate.setDefaultLang('en');
	}

	ngOnInit() {
		this.router.events
			.filter((event) => event instanceof NavigationEnd)
			.map(() => this.activatedRoute)
			.map((route) => {
				while (route.firstChild) route = route.firstChild;
				return route;
			})
			.filter((route) => route.outlet === 'primary')
			.mergeMap((route) => route.data)
			.subscribe((event) => {
				if (event['title']){
					this.translate.get(event['title']).subscribe((translated: string) => {
						this.titleService.setTitle(translated);
					});
				}
				else{
					this.titleService.setTitle(this.translatedTitle);
				}
			});
	}
}
