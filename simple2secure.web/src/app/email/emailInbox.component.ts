import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatSort, MatPaginator} from '@angular/material';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Email} from '../_models/index';
import {AlertService, HttpService} from '../_services/index';
import {Router, ActivatedRoute} from '@angular/router';
import {environment} from '../../environments/environment';
import {DataSource} from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import {TranslateService} from '@ngx-translate/core';
import 'rxjs/add/observable/of';

@Component({
  moduleId: module.id,
  styleUrls: ['email.component.css'],
  templateUrl: 'emailInbox.component.html',
  selector: 'emailInbox',
  animations: [
   trigger('detailExpand', [
     state('collapsed', style({ height: '0px', minHeight: '0', visibility: 'hidden' })),
     state('expanded', style({ height: '*', visibility: 'visible' })),
     transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
   ]),
 ],
})
export class EmailInboxComponent {

    mails: Email[];
    tempMails: Email;
    loading = false;
    id: string;

    displayedColumns = ['from', 'subject', 'time'];
    dataSource = new MatTableDataSource();
    isExpansionDetailRow = (i: number, row: Object) => row.hasOwnProperty('detailRow');
    expandedElement: any;

    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

    constructor(
            private route: ActivatedRoute,
            private router: Router,
            private httpService: HttpService,
            private alertService: AlertService,
            private translate: TranslateService) {}

    ngOnInit() {
      this.id = this.route.snapshot.paramMap.get('id');
      this.loadAllEmails();
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      }

    applyFilter(filterValue: string) {
        filterValue = filterValue.trim(); // Remove whitespace
        filterValue = filterValue.toLowerCase(); // MatTableDataSource defaults to lowercase matches
        this.dataSource.filter = filterValue;
    }

    /**
     * Dummy configuration in case that we are using the mock environment
     */
    private createTempEmail(){
        this.tempMails = new Email();
        this.tempMails.configID = 'test';
        this.tempMails.from = 'fakeMail@secinto.com';
        this.tempMails.id = '123123';
        this.tempMails.number = 121321;
        this.tempMails.subject = 'test subject';
        this.tempMails.text = 'This is only a test mail';
        this.tempMails.userUUID = 'ksoadkosakdoakdoskaodkoadsa';
    }

    private loadAllEmails() {
        if (environment.envName === 'mock'){
            this.mails = [];
            this.createTempEmail();
            this.mails.push(this.tempMails);
            // this.dataSource.data = this.mails;
            this.loading = false;
            this.alertService.success(this.translate.instant('message.email.load'));
        }
        else{
            this.loading = true;
            this.httpService.get(environment.apiEndpoint + 'email/inbox/' + this.id )
              .subscribe(
              data => {
                this.mails = data;
                this.dataSource.data = this.mails;
                if (data.length > 0) {
                  this.alertService.success(this.translate.instant('message.email.load'));
                }
                else {
                  this.alertService.error(this.translate.instant('message.email.notProvided'));
                }
                this.loading = false;
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
      }

}
