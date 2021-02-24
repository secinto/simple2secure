/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */

import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { StockChart } from 'angular-highcharts';
import { environment } from '../../../environments/environment';
import { NgxSpinnerService } from 'ngx-spinner';
import { AddQueryDialog } from './addQueryDialog';
import { HttpParams } from '@angular/common/http';
import { HttpService } from '../../_services/http.service';
import { GraphReport } from '../../_models/graphReport';
import { Device } from '../../_models/device';
import { DeviceType } from '../../_models/deviceType';


@Component({
    moduleId: module.id,
    styleUrls: ['analysis.component.css'],
    selector: 'analysis',
    templateUrl: 'analysis.component.html'
})

export class AnalysisComponent implements OnInit {

    reports: any[];
    graphReports: GraphReport[];
    queries: any[];
    probes: Device[] = [];
    selectedQuery: any;
    selectedProbe: Device;
    chart: StockChart;
    chartOptions: any;
    seriesOption: any;
    loading = false;

    constructor(
        private httpService: HttpService,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private translate: TranslateService) {
    }

    ngOnInit() {

        this.loadAllProbes(true);
        // this.loadAllQueries(true);
    }

    loadAllProbes(defaultValue: boolean) {
        const params = new HttpParams()
            .set('active', String(true))
            .set('page', String(0))
            .set('size', String(10000))
            .set('filter', '');

        const apiUrl = environment.apiDevicesByTypePagination.replace('{deviceType}', DeviceType.PROBE);

        this.httpService.getWithParams(apiUrl, params)
            .subscribe(
                data => {
                    this.probes = data.devices;
                    this.selectedProbe = this.probes[0];
                    if (defaultValue) {
                        if (this.probes.length > 0) {
                            this.loadQueriesByProbe(this.selectedProbe.info.id);
                        }
                    }

                });
    }

    loadQueriesByProbe(probeId: string) {
        const apiUrl = environment.apiQueriesByProbe.replace('{probeId}', probeId);
        const apiUrlFull = apiUrl.replace('{osinfo}', 'UNKNOWN');

        this.httpService.get(apiUrlFull)
            .subscribe(
                data => {
                    this.queries = data;
                    if (this.probes.length > 0) {
                        this.selectedQuery = this.queries[0];
                        this.loadReportsByNameAndDevice(probeId, this.selectedQuery.name);
                    }
                });
    }

    loadReportsByNameAndDevice(probeId: string, name: string) {
        const apiUrl = environment.apiReportsByDeviceIdName.replace('{deviceId}', probeId);
        const apiUrlFull = apiUrl.replace('{name}', name);
        this.httpService.get(apiUrlFull)
            .subscribe(
                data => {
                    this.graphReports = data;
                    this.createChart(name, 'line');
                    for (const report of this.graphReports) {
                        const date = new Date(report.timestamp);
                        this.chartOptions.series[0].data.push({
                            x: Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds()),
                            y: report.numberOfReports,
                            id: report.reportId
                        });
                    }
                });
    }

    onQueryChange(value: any) {
        this.loadReportsByNameAndDevice(this.selectedProbe.info.id, value.name);
    }

    onProbeChange(value: any) {
        this.loadQueriesByProbe(value.info.id);
    }

    public openDialogShowReportDetails(event: any): void {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.width = '450px';
        this.spinner.show();

        // TODO
    }

    public openDialogAddQuery() {
        const dialogConfig = new MatDialogConfig();
        dialogConfig.autoFocus = true;
        dialogConfig.width = '450px';

        dialogConfig.data = {
            id: 1,
            title: this.translate.instant('chart.query.select'),
            content: this.translate.instant('chart.additional.query'),
            selectMessage: this.translate.instant('chart.query.select'),
            queryList: this.queries,
            button: this.translate.instant('button.select'),
            deviceId: this.selectedProbe.info.id
        };

        const dialogRef = this.dialog.open(AddQueryDialog, dialogConfig);

        dialogRef.afterClosed().subscribe(result => {
            this.addSeriesToChart(result);
        });
    }

    addSeriesToChart(reportAPI: any[]) {

        if (reportAPI) {
            this.seriesOption = {
                name: reportAPI[0].reportName,
                data: []
            };

            for (const report of reportAPI) {
                const date = new Date(report.timestamp);
                this.seriesOption.data.push({
                    x: Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds()),
                    y: report.numberOfReports,
                    id: report.reportId
                });
            }

            this.chart.ref.addSeries(this.seriesOption);

            this.chart.ref.redraw(true);
        }

    }

    createChart(name: string, type: string) {
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
                        hover: {},
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
}


