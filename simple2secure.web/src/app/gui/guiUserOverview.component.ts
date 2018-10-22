import {Component} from '@angular/core';
import {ImportService, AlertService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';

@Component({
  moduleId: module.id,
  templateUrl: 'guiUserOverview.component.html'
})

export class GuiUserOverviewComponent {

  gui: any;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private importService: ImportService,
    private alertService: AlertService,
    public dataService: DataService) {}


  ngOnInit() {
    this.gui = this.dataService.get();
  }
}
