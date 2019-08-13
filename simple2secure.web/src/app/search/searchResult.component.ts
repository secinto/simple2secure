import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AlertService, HttpService} from '../_services';
import {environment} from '../../environments/environment';
import {TranslateService} from '@ngx-translate/core';
import {Ng4LoadingSpinnerService} from 'ng4-loading-spinner';
import {SearchResult} from '../_models/searchResult';
import {arrayify} from 'tslint/lib/utils';


@Component({
    moduleId: module.id,
    styleUrls: ['searchResult.component.css'],
    templateUrl: 'searchResult.component.html',
    selector: 'searchResult'
})

export class SearchResultComponent implements OnInit{

    searchString = '';
    public loading = true;
    searchResult: SearchResult[] = [];


    constructor(private router: Router,
                private route: ActivatedRoute,
                private httpService: HttpService,
                private alertService: AlertService,
                private translate: TranslateService,
                private spinnerService: Ng4LoadingSpinnerService){

        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    }

    ngOnInit() {
        this.loading = true;
        this.spinnerService.show();
        this.searchString = this.route.snapshot.paramMap.get('searchquery');
        this.getSearchResults();
    }


    getSearchResults() {
        // Send request only if the search string is not null
        if (this.searchString){
            this.httpService.get(environment.apiEndpoint + 'search/' + this.searchString)
            .subscribe(
                data => {
                    this.searchResult = data;
                    console.log(this.searchResult);
                },
                error => {
                    if (error.status == 0) {
                        this.alertService.error(this.translate.instant('server.notresponding'));
                    }
                    else {
                        this.alertService.error(error.error.errorMessage);
                    }
                });
            this.spinnerService.hide();
            this.loading = false;
        }
    }

    getSearchResultObjectLenght(searchResult: SearchResult){
        if (searchResult){
            if (searchResult.object)
            return searchResult.object.length;
        }
        else{
            return 0;
        }
    }
}
