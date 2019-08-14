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

import {Component, OnInit} from '@angular/core';
import {User} from '../_models/index';

declare var $: any;

@Component({
	styleUrls: ['home.component.css'],
	moduleId: module.id,
	templateUrl: 'home.component.html'
})

export class HomeComponent implements OnInit {
	currentUser: User;
	users: User[] = [];

	constructor() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
	}

	ngOnInit() {
	}

	ngAfterViewInit() {
		/*Set all navigation items to grey*/
		$('.navbar-nav li img').each(function (index) {
			$(this).css('-webkit-filter', 'grayscale(100%)');
			$(this).css('filter', 'grayscale(100%)');
		});


		$('.column').hover(function ()
			{
				/* on hover set all items on grey*/
				$('.navbar-nav li img').each(function (index) {
					$(this).css('-webkit-filter', 'grayscale(100%)');
					$(this).css('filter', 'grayscale(100%)');
				});
				let rotateAngleTop = -60;
				$('.subitems button').each(function (index) {
					let rotator = (2 - index) * 20;
					rotateAngleTop = rotateAngleTop - 30;

				});
				/* before selecting some item from the dashboard, first set all transform styles to empty, to deselect the last selected item*/
				$('.column').each(function (index) {
					$(this).css('-webkit-transform', '');
					$(this).css('-moz-transform', '');
					$(this).css('-ms-transform', '');
					$(this).css('-o-transform', '');
					$(this).css('transform', '');
					$(this).find('.subitems').hide();
				});

				/* Select item on the dashboard and set the color to the normal one*/
				$(this).toggleClass('hover');
				$(this).css('-webkit-transform', 'scale(1.25)');
				$(this).css('-moz-transform', 'scale(1.25)');
				$(this).css('-ms-transform', 'scale(1.25)');
				$(this).css('-o-transform', 'scale(1.25)');
				$(this).css('transform', 'scale(1.25)');
				$(this).find('.subitems').show();
				$('#' + this.id + '-menu').find('img').css('-webkit-filter', '');
				$('#' + this.id + '-menu').find('img').css('filter', '');
			}, function ()
			{
				/*Do not do anything*/
			}
		);
	}
}
