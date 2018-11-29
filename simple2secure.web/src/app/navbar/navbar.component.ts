import { ViewChild, Component } from '@angular/core';
import { User } from '../_models/user';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/mergeMap';
import {MatMenuTrigger} from '@angular/material';
import {TranslateService} from '@ngx-translate/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Context, UserRole} from '../_models';
import {environment} from '../../environments/environment';
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
	currentUser: User;
	currentContext: Context;
    loggedIn: boolean;
    currentLang: string;
    showSettings: boolean;


    languages: Language[] = [
                             {value: 'en', viewValue: 'English', localeVal: 'EN'},
                             {value: 'de', viewValue: 'German', localeVal: 'DE'}
                           ];

    constructor(private translate: TranslateService,
                private router: Router){}

    ngDoCheck() {

        this.currentLang = this.translate.currentLang;
        if (!this.currentLang){
            this.currentLang = this.translate.defaultLang;
        }

        this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
        this.currentContext = JSON.parse(localStorage.getItem('context'));


        if (this.currentUser && this.currentContext){
            this.loggedIn = true;

            if (this.currentUser.userRole == UserRole.SUPERADMIN){
                this.showSettings = true;

            }
            else{
                this.showSettings = false;
            }
        }
        else{
            this.loggedIn = false;
        }
    }

    public onNavItemClick(routerLink: string, itemId: string){
        $('.navbar-nav li img').each(function( index ) {
            $(this).css('-webkit-filter', 'grayscale(100%)');
            $(this).css('filter', 'grayscale(100%)');
        });

        $('#' + itemId).find('img').css('-webkit-filter', '');
        $('#' + itemId).find('img').css('filter', '');
        this.router.navigate([routerLink]);
    }

    ngAfterViewInit(){

        $('.navbar-nav li img').each(function( index ) {
            $(this).css('-webkit-filter', 'grayscale(100%)');
            $(this).css('filter', 'grayscale(100%)');
        });
    }

    someMethod() {
        this.trigger.openMenu();
      }

    public setLocale(lang: string){
        this.translate.use(lang);
    }

    changeContext(){
        console.log("CHANGING CONTEXT");
        // if number of contexts is greater than 1 open dialog to change context
    }


}
