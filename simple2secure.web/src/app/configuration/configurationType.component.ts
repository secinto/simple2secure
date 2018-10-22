import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    moduleId: module.id,
    templateUrl: 'configurationType.component.html',
	selector: 'configuration'
})

export class ConfigurationTypeComponent {

    constructor(
        private route: ActivatedRoute,
        private router: Router
    ) {}

    public editConfiguration(type: string) {
        this.router.navigate([type], {relativeTo: this.route});
    }
}
