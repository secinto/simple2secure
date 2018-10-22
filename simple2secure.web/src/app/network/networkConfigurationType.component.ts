import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  moduleId: module.id,
  templateUrl: 'networkConfigurationType.component.html'
})

export class NetworkConfigurationTypeComponent {

    type: number;
    probeId: string;
    groupId: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {

      this.route.queryParams.subscribe(params => {
          this.type = params['type'];
          if (this.type == 3){
              this.groupId = params['groupId'];
          }
          else{
              this.probeId = params['probeId'];
          }

      });
  }

  public selectType(typeName: string) {
    if (typeName === 'steps') {

        if (this.type == 3){
            this.router.navigate(['../../config/details/step'], {relativeTo: this.route, queryParams: {type: this.type, groupId: this.groupId}});
        }
        else{
            this.router.navigate(['../../config/details/step'], {relativeTo: this.route, queryParams: {type: this.type, probeId: this.probeId}});
        }
    }
    else {
          if (this.type == 3){
              this.router.navigate(['../../config/details/processor'], {relativeTo: this.route, queryParams: {type: this.type, groupId: this.groupId}});
          }
          else{
              this.router.navigate(['../../config/details/processor'], {relativeTo: this.route, queryParams: {type: this.type, probeId: this.probeId}});
          }
    }
  }
}
