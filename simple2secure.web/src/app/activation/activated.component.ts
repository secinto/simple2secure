import {Component} from '@angular/core';

import {User} from '../_models/index';

@Component({
	moduleId: module.id,
	styleUrls: ['activation.component.css'],
	templateUrl: 'activated.component.html'
})

export class ActivatedComponent {
	user: User;
	loading: boolean;

	constructor() {
		this.user = new User();
		this.loading = false;
	}
}
