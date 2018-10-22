import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  moduleId: module.id,
  templateUrl: 'osqueryOverview.component.html'
})

export class OsqueryOverviewComponent {

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  public editConfiguration(type: string) {
      this.router.navigate([type], {relativeTo: this.route});
  }
}
