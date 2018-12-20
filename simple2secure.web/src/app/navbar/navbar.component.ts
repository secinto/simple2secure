import {ViewChild, Component} from '@angular/core';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/mergeMap';
import {MatDialog, MatDialogConfig, MatMenuTrigger} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Context, ContextDTO, UserRole} from '../_models';
import {environment} from '../../environments/environment';
import {SelectContextDialog} from '../dialog/select-context';
import {AlertService, AuthenticationService, HttpService} from '../_services';

declare var $: any;

export interface Language {
	value: string;
	viewValue: string;
	localeVal: string;
}

@Component({
	moduleId: module.id,
	templateUrl: 'navbar.component.html',
	styleUrls: ['navbar.component.css'],
	selector: 'navbar'
})

export class NavbarComponent {
	@ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;
	currentUser: any;
	currentContext: ContextDTO;
	loggedIn: boolean;
	currentLang: string;
	showSettings: boolean;
	returnUrl: string;


	languages: Language[] = [
		{value: 'en', viewValue: 'English', localeVal: 'EN'},
		{value: 'de', viewValue: 'German', localeVal: 'DE'}
	];

	constructor(private translate: TranslateService,
	            private router: Router,
	            private route: ActivatedRoute,
	            private httpService: HttpService,
	            private alertService: AlertService,
	            private authenticationService: AuthenticationService,
	            private dialog: MatDialog)
	{
		this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
	}

	ngDoCheck() {

		this.currentLang = this.translate.currentLang;
		if (!this.currentLang) {
			this.currentLang = this.translate.defaultLang;
		}

		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.currentContext = JSON.parse(localStorage.getItem('context'));


		if (this.currentUser && this.currentContext) {
			this.loggedIn = true;

			if (this.currentContext.userRole == UserRole.SUPERADMIN) {
				this.showSettings = true;

			}
			else {
				this.showSettings = false;
			}
		}
		else {
			this.loggedIn = false;
		}
	}

	public onNavItemClick(routerLink: string, itemId: string) {
		$('.navbar-nav li img').each(function (index) {
			$(this).css('-webkit-filter', 'grayscale(100%)');
			$(this).css('filter', 'grayscale(100%)');
		});

		$('#' + itemId).find('img').css('-webkit-filter', '');
		$('#' + itemId).find('img').css('filter', '');
		this.router.navigate([routerLink]);
	}

	ngAfterViewInit() {

		$('.navbar-nav li img').each(function (index) {
			$(this).css('-webkit-filter', 'grayscale(100%)');
			$(this).css('filter', 'grayscale(100%)');
		});
	}

	someMethod() {
		this.trigger.openMenu();
	}

	public setLocale(lang: string) {
		this.translate.use(lang);
	}

	changeContext() {
		// if number of contexts is greater than 1 open dialog to change context
		this.getContexts(this.currentUser.userID);
	}

	private getContexts(userId: string) {
		this.httpService.get(environment.apiEndpoint + 'context/' + userId)
			.subscribe(
				data => {
					this.openSelectContextModal(data);
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

	openSelectContextModal(contexts: ContextDTO[]) {
		// If size of the contexts is greater than 0 open dialog
		if (contexts.length > 1) {
			const dialogConfig = new MatDialogConfig();

			dialogConfig.disableClose = true;
			dialogConfig.autoFocus = true;
			dialogConfig.width = '450px';

			dialogConfig.data = {
				id: 1,
				title: this.translate.instant('change.context'),
				content: this.translate.instant('message.contextDialogDashboard'),
				selectMessage: this.translate.instant('message.contextDialog.select'),
				contextList: contexts
			};

			const dialogRef = this.dialog.open(SelectContextDialog, dialogConfig);

			dialogRef.afterClosed().subscribe(result => {
				if (result == true) {
					this.router.navigate([this.returnUrl]);
				}
				else {
					this.authenticationService.logout();
				}
			});
		}
		// If size of the contexts is equal to 1, set currentContext automatically
		else if (contexts.length == 1) {
			this.alertService.error(this.translate.instant('message.contextChangeError'));
		}

		// In this case some error occured and user needs to be redirect again to login page, call logout function
		else {
			this.alertService.error(this.translate.instant('server.notresponding'));
			this.authenticationService.logout();
		}
	}


}
