import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  moduleId: module.id,
  templateUrl: 'networkOverview.component.html'
})

export class NetworkOverviewComponent {

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  public editConfiguration(type: string) {
      this.router.navigate([type], {relativeTo: this.route});
  }
}
