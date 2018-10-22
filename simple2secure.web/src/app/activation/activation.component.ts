import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {AlertService, HttpService} from '../_services/index';
import {environment} from '../../environments/environment';
import {saveAs as importedSaveAs} from 'file-saver';

@Component({
  moduleId: module.id,
  styleUrls: ['activation.component.css'],
  templateUrl: 'activation.component.html'
})

export class ActivationComponent {
  loading: boolean;
  activationToken: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alertService: AlertService,
    private httpService: HttpService) {
    this.loading = false;
  }

  ngOnInit() {
    this.activationToken = this.route.snapshot.paramMap.get('id');
  }

  public download(){
      this.loading = true;
      this.httpService.getFile(environment.apiEndpoint + 'download')
        .subscribe(
        data  => {
          importedSaveAs(data, 's2s_setup.exe');
          this.loading = false;
        },
        error => {
            this.alertService.error(error.errorMessage);
            this.loading = false;
        });
    }
}
