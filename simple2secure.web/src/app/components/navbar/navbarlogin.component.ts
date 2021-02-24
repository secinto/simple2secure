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

import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

declare var $: any;

export interface Language {
    value: string;
    viewValue: string;
    localeVal: string;
}

@Component({
    moduleId: module.id,
    templateUrl: 'navbarlogin.component.html',
    styleUrls: ['navbarlogin.component.css'],
    selector: 'navbar-login'
})

export class NavbarLoginComponent {
    currentLang: string;

    languages: Language[] = [
        { value: 'en', viewValue: 'English', localeVal: 'EN' },
        { value: 'de', viewValue: 'German', localeVal: 'DE' }
    ];

    constructor(private translate: TranslateService) {
    }


    ngDoCheck() {

        this.currentLang = this.translate.currentLang;
        if (!this.currentLang) {
            this.currentLang = this.translate.defaultLang;
        }
    }

    public setLocale(lang: string) {
        this.translate.use(lang);
    }
}
