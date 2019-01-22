import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Chart, StockChart} from 'angular-highcharts';
import {environment} from '../../environments/environment';
import {ContextDTO, GraphReport} from '../_models';
import {HttpService} from '../_services';

@Component({
	moduleId: module.id,
	styleUrls: ['analysis.component.css'],
	selector: 'analysis',
	templateUrl: 'analysis.component.html'
})

export class AnalysisComponent implements OnInit{

	reports: any[];
	graphReports: GraphReport[];
	queries: any[];
	context: ContextDTO;
	currentUser: any;
	selectedQuery: any;
	private chart: Chart;
	chartOptions: any;


	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService)
	{}



	ngOnInit() {
		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadAllQueries(true);
	}


	loadAllQueries(defaultValue: boolean) {
		this.httpService.get(environment.apiEndpoint + 'config/query/context/' + this.context.context.id + '/true')
			.subscribe(
				data => {
					this.queries = data;
					if (defaultValue){
						this.selectedQuery = this.queries[2];
						this.loadReportsByName(this.selectedQuery.sqlQuery);
					}

				});
	}

	loadReportsByName(name: string){
		this.httpService.post(name, environment.apiEndpoint + 'reports/report/name')
			.subscribe(
				data => {
					this.graphReports = data;
					this.createChart(name, 'line');
					for (const report of this.graphReports) {
						const date = new Date(report.timestamp);
						this.chart.addPoint([Date.UTC(date.getUTCFullYear(), date.getUTCMonth(),
							date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds()), report.numberOfReports]);
					}
				});
	}

	onQueryChange(value: any){
		this.loadReportsByName(value.sqlQuery);
	}

	createChart(name: string, type: string){
		this.chartOptions = {
			chart: {
				type: type
			},
			title: {
				text: name
			},
			credits: {
				enabled: false
			},
			series: [{
				name: name,
				data: []
			}],
			xAxis: {
				type: 'datetime',
				dateTimeLabelFormats: {
					second: '%H:%M:%S',
					minute: '%H:%M',
					hour: '%H:%M',
					day: '%e. %b',
					month: '%b \'%y',
					year: '%Y'
				}
			},
			time: {
				useUTC: false
			},
			exporting: {
				enabled: true
			},
			tooltip: {
				animation: true
			},
			noData: true,
			plotOptions: {
				series: {
					cursor: 'pointer',
					point: {
						events: {
							click: function () {
								alert('Category: ' + this.category + ', value: ' + this.y);
							}
						}
					}
				}
			},
			rangeSelector: {
				enabled: true,
				inputEnabled: false,
				selected: 'all',
				allButtonsEnabled: false,

				buttons: [{
					type: 'month',
					count: 1,
					text: '1m'
				}, {
					type: 'month',
					count: 3,
					text: '3m'
				}, {
					type: 'month',
					count: 6,
					text: '6m'
				}, {
					type: 'ytd',
					text: 'YTD'
				}, {
					type: 'year',
					count: 1,
					text: '1y'
				}, {
					type: 'all',
					text: 'All'
				}]
			},
			navigator: {
				enabled: true
			}
		};
		this.chart = new Chart(this.chartOptions);
	}

}
