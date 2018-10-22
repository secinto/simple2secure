import {Component} from '@angular/core';
import {ImportService, AlertService, DataService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

@Component({
  moduleId: module.id,
  templateUrl: 'guiOverview.component.html'
})

export class GuiOverviewComponent {

  gui: any[];
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private importService: ImportService,
    private translate: TranslateService,
    private alertService: AlertService,
    public dataService: DataService) {}


  ngOnInit() {
    this.loadAllGUIs();
  }

  private loadAllGUIs() {
    this.loading = true;
    this.importService.getAll()
      .subscribe(
      data => {
        this.gui = data;
        if (data.length > 0) {
          this.alertService.success('Data loaded successfully!');
        }
        else {
          this.alertService.error('Data not provided!');
        }

      },
      error => {
          if (error.status == 0){
              this.alertService.error(this.translate.instant('server.notresponding'));
          }
          else{
              this.alertService.error(error.error.errorMessage);
          }
          this.loading = false;
      });
  }

  editGUI(item: any) {
    this.dataService.set(item);
    this.router.navigate(['edit', ], {relativeTo: this.route});
  }

}
