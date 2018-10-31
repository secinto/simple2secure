import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    moduleId: module.id,
    templateUrl: 'reportOverview.component.html'
})

export class ReportOverviewComponent {

    constructor(
        private route: ActivatedRoute,
        private router: Router) {}

    showReports(path: string){
        this.router.navigate([path], {relativeTo: this.route});
    }
}
