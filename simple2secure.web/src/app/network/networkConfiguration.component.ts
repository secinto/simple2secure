import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  moduleId: module.id,
  templateUrl: 'networkConfiguration.component.html'
})

export class NetworkConfigurationComponent {

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  public editConfiguration(type: string) {
    if (type === 'general') {
      this.router.navigate(['groups'], {relativeTo: this.route, queryParams: {type: 3}});
    }
    else {
      this.router.navigate(['devices'], {relativeTo: this.route, queryParams: {type: 4}});
    }

  }
}
