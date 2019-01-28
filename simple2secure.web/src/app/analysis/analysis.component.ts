import {Component, OnInit} from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {StockChart} from 'angular-highcharts';
import {environment} from '../../environments/environment';
import {ContextDTO, GraphReport, Marker, NetworkReportDTO, Coordinates} from '../_models';
import {HttpService} from '../_services';
import {OsQueryReportDetailsComponent} from '../report';
import {NgxSpinnerService} from 'ngx-spinner';
import {AddQueryDialog} from './addQueryDialog';

@Component({
	moduleId: module.id,
	styleUrls: ['analysis.component.css'],
	selector: 'analysis',
	templateUrl: 'analysis.component.html'
})

export class AnalysisComponent implements OnInit{

	reports: any[];
	graphReports: GraphReport[];
	networkReports: NetworkReportDTO[];
	queries: any[];
	context: ContextDTO;
	currentUser: any;
	selectedQuery: any;
	private chart: StockChart;
	chartOptions: any;
	seriesOption: any;
	loading = false;
	markers: Marker[] = [];
	selectedNetworkReport: NetworkReportDTO;
	private coordinates: Coordinates[];

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private httpService: HttpService,
		private dialog: MatDialog,
		private spinner: NgxSpinnerService,
		private translate: TranslateService)
	{}

	ngOnInit() {
		/*google.charts.load('current', { 'packages': ['map'], 'mapsApiKey': 'AIzaSyCo6SKY-rBYhT-6p1bLCaiH-IdYEi29oKI' });
		google.charts.setOnLoadCallback(this.drawChart);*/

		this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
		this.context = JSON.parse(localStorage.getItem('context'));
		this.loadAllQueries(true);
		this.loadNetworkReports();
	}

	loadNetworkReports(){
		this.httpService.get(environment.apiEndpoint + 'reports/report/network/geo')
			.subscribe(
				data => {
					this.networkReports = data;
				});
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
						this.chartOptions.series[0].data.push({
							x: Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds()),
							y: report.numberOfReports,
							id: report.reportId});
					}
				});
	}

	onQueryChange(value: any){
		this.loadReportsByName(value.sqlQuery);
	}

	onQueryChangeNetworkRep(value: NetworkReportDTO){
		this.selectedNetworkReport = value;
		this.addLabels(this.selectedNetworkReport);
	}

	public openDialogShowReportDetails(event: any): void {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '450px';
		this.spinner.show();
		this.httpService.get(environment.apiEndpoint + 'reports/report/' + event.point.id)
			.subscribe(
				data => {
					dialogConfig.data = {
						report: data,
					};
					this.dialog.open(OsQueryReportDetailsComponent, dialogConfig);
					this.spinner.hide();
				});
		this.spinner.hide();
	}

	public openDialogAddQuery(){
		const dialogConfig = new MatDialogConfig();
		dialogConfig.autoFocus = true;
		dialogConfig.width = '450px';

		dialogConfig.data = {
			id: 1,
			title: this.translate.instant('chart.query.select'),
			content: this.translate.instant('chart.additional.query'),
			selectMessage: this.translate.instant('chart.query.select'),
			queryList: this.queries,
			button: this.translate.instant('button.select')
		};

		const dialogRef = this.dialog.open(AddQueryDialog, dialogConfig);

		dialogRef.afterClosed().subscribe(result => {
			this.addSeriesToChart(result);
		});
	}

	addSeriesToChart(reportAPI: any[]){

		if (reportAPI){
			this.seriesOption = {
				name: reportAPI[0].reportName,
				data: []
			};

			for (const report of reportAPI) {
				const date = new Date(report.timestamp);
				this.seriesOption.data.push({
					x: Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds()),
					y: report.numberOfReports,
					id: report.reportId});
			}

			this.chart.ref.addSeries(this.seriesOption);

			this.chart.ref.redraw(true);
		}

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
			legend: {
				enabled: true
			},
			series: [{
				name: name,
				data: [],
				lineColor: '#A7BD26',
				shadow: true,
				color: '#A7BD26'
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
				enabled: true,
				buttons: {
					contextButton: {
						symbolFill: '#A7BD26',
						symbolStroke: '#A7BD26'
					}
				}
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
							click: this.openDialogShowReportDetails.bind(this)
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
					type: 'hour',
					count: 12,
					text: '12h'
				}, {
					type: 'day',
					count: 1,
					text: '1d'
				}, {
					type: 'day',
					count: 7,
					text: '7d'
				}, {
					type: 'month',
					count: 1,
					text: '1m'
				}, {
					type: 'month',
					count: 6,
					text: '6m'
				}, {
					type: 'year',
					count: 1,
					text: '1y'
				}, {
					type: 'all',
					text: 'All'
				}],

				buttonTheme: {
					fill: 'none',
					stroke: 'none',
					'stroke-width': 0,
					r: 8,
					style: {
						color: '#A7BD26',
						fontWeight: 'bold'
					},
					states: {
						hover: {
						},
						select: {
							fill: '#A7BD26',
							style: {
								color: 'white'
							}
						}
					}
				},
			},
			scrollbar: {
				enabled: false
			},
			navigator: {
				enabled: true,
				maskFill: 'rgba(167, 189, 38, 0.3)'
			}
		};
		this.chart = new StockChart(this.chartOptions);
	}


	/// ###This part is used for maps###

	addLabels(networkReport: NetworkReportDTO){
		this.coordinates = networkReport.coordinates;
		for (const coord of this.coordinates) {
			if (this.markers.length < 5000){
				this.markers.push({
					lat: coord.srclatitude,
					lng: coord.srclongitude,
					latDest: coord.destlatitude,
					lngDest: coord.destlongitude,
					draggable: false
				});
			}

		}
	}

	clickedMarker(label: string, index: number) {
		console.log(`clicked the marker: ${label || index}`);
	}




}


