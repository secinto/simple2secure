import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  moduleId: module.id,
  templateUrl: 'osqueryConfiguration.component.html'
})

export class OsqueryConfigurationComponent {

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  public editConfiguration(type: string) {
    if (type === 'general') {
      this.router.navigate(['group'], {relativeTo: this.route});
    }
    else {
      this.router.navigate(['devices'], {relativeTo: this.route});
    }

  }
}
