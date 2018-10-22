import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  moduleId: module.id,
  templateUrl: 'orbiterConfiguration.component.html'
})

export class OrbiterConfigurationComponent {

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  public editConfiguration(type: string) {
      this.router.navigate([type], {relativeTo: this.route});
  }
}
