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

import { RouterModule, Routes } from '@angular/router';
import { DevicesListComponent } from './components/devices/devicesList.component';
import { OsQueryReportOverviewComponent } from './components/report/osqueryReportOverview.component';
import { NetworkReportOverviewComponent } from './components/report/networkReportOverview.component';
import { TestSequenceResultComponent } from './components/report/testSequenceResult.component';
import { ReportOverviewComponent } from './components/report/reportOverview.component';
import { HomeComponent } from './components/home/home.component';
import { OrbiterSystemsUnderTestListComponent } from './components/orbiter/orbiterSystemsUnderTestList.component';
import { OrbiterScheduledSequencesListComponent } from './components/orbiter/orbiterScheduledSequencesList.component';
import { QueriesComponent } from './components/queries/queries.component';
import { EmailComponent } from './components/email/email.component';
import { RuleOverviewComponent } from './components/rule/ruleOverview.component';
import { LoginComponent } from './components/authentication/login/login.component';
import { ReportComponent } from './components/report/report.component';
import { OrbiterOverviewComponent } from './components/orbiter/orbiterOverview.component';
import { OrbiterToolTestSequenceListComponent } from './components/orbiter/orbiterToolTestSequenceList.component';
import { EmailRuleOverviewComponent } from './components/email/emailRuleOverview.component';
import { UserOverviewComponent } from './components/user/userOverview.component';
import { DevicesOverviewComponent } from './components/devices/devicesOverview.component';
import { QueryAssignComponent } from './components/queries/queryAssign.component';
import { SettingsComponent } from './components/settings/settings.component';
import { NetworkReportDetailsComponent } from './components/report/networkReportDetails.component';
import { OrbiterToolTestScheduledListComponent } from './components/orbiter/orbiterToolTestScheduledList.component';
import { QueryListComponent } from './components/queries/queryList.component';
import { EmailOverviewComponent } from './components/email/emailOverview.component';
import { OsQueryReportDetailsComponent } from './components/report/osqueryReportDetails.component';
import { UserDetailsComponent } from './components/user/userDetails.component';
import { OrbiterToolTestListComponent } from './components/orbiter/orbiterToolTestList.component';
import { OrbiterComponent } from './components/orbiter/orbiter.component';
import { AnalysisComponent } from './components/analysis/analysis.component';
import { UserComponent } from './components/user/user.component';
import { OrbiterToolTestComponent } from './components/orbiter/orbiterToolTest.component';
import { DevicesComponent } from './components/devices/devices.component';
import { TestResultComponent } from './components/report/testResult.component';
import { SearchComponent } from './components/search/search.component';
import { SearchResultComponent } from './components/search/searchResult.component';
import { QueryOverviewComponent } from './components/queries/queryOverview.component';
import { UserRole } from './_models/userRole';
import { RoleGuard } from './_guards/role.guard';
import { DashboardLayoutComponent } from './_layouts/dashboardLayout/dashboardlayout.component';
import { LoginLayoutComponent } from './_layouts/loginLayout/loginlayout.component';
import { AuthGuard } from './_guards/auth.guard';
import { RuleListComponent } from './components/rule/ruleList.component';
import { RuleComponent } from './components/rule/rule.component';
import { RuleRegexListComponent } from './components/rule/ruleRegexList.component';


const appRoutes: Routes = [
    {
        path: '', component: DashboardLayoutComponent, canActivate: [AuthGuard],
        children: [
            {
                path: '',
                component: HomeComponent,
                data: { dashboardName: 'homeDash' }
            },

            {
                path: 'analysis',
                component: AnalysisComponent,
                data: { title: 'menu.analysis', breadcrumb: 'Analysis', dashboardName: 'analysisDash' }
            },
            {
                path: 'report',
                component: ReportComponent,
                data: { title: 'menu.reports', breadcrumb: 'Reports', dashboardName: 'reportsDash' },
                children: [
                    {
                        path: '',
                        component: ReportOverviewComponent,
                        data: { title: 'menu.reports', breadcrumb: 'Dashboard' }
                    },
                    {
                        path: 'network',
                        component: NetworkReportOverviewComponent,
                        data: { title: 'menu.networkanalysisReports', breadcrumb: 'Network' }
                    },
                    {
                        path: 'osquery',
                        component: OsQueryReportOverviewComponent,
                        data: { title: 'menu.queryReport', breadcrumb: 'OsQuery' }
                    },
                    {
                        path: 'testResult',
                        component: TestResultComponent,
                        data: { title: 'menu.testResults', breadcrumb: 'Test Results' }
                    },
                    {
                        path: 'testSequenceResult',
                        component: TestSequenceResultComponent,
                        data: { title: 'menu.testSequenceResults', breadcrumb: 'Sequence Results' }
                    },
                ]
            },
            {
                path: 'tests',
                component: OrbiterComponent,
                data: { title: 'menu.tests', breadcrumb: 'Tests', dashboardName: 'testsDash' },
                children: [
                    {
                        path: '',
                        component: OrbiterOverviewComponent,
                        data: { title: 'menu.orbiter', breadcrumb: 'Dashboard' }
                    },
                    {
                        path: 'test',
                        component: OrbiterToolTestComponent,
                        data: { title: 'menu.tests', breadcrumb: 'Test Templates' }
                    },
                    { path: 'test/:id', component: OrbiterToolTestListComponent, data: { title: 'menu.tests' } },
                    {
                        path: 'scheduledTests',
                        component: OrbiterToolTestScheduledListComponent,
                        data: { title: 'test.scheduled', breadcrumb: 'Scheduled Tests' }
                    },
                    {
                        path: 'test/sequences/:id',
                        component: OrbiterToolTestSequenceListComponent,
                        data: { title: 'test.sequences' }
                    },
                    {
                        path: 'scheduledSequences',
                        component: OrbiterScheduledSequencesListComponent,
                        data: { title: 'sequence.scheduled', breadcrumb: 'Scheduled Sequences' }
                    },
                    {
                        path: 'sut',
                        component: OrbiterSystemsUnderTestListComponent,
                        data: { title: 'orbiter.sut', breadcrumb: 'System Under Test' }
                    },
                ]
            },
            {
                path: 'queries',
                component: QueriesComponent,
                data: { title: 'menu.queries', breadcrumb: 'Queries', dashboardName: 'queriesDash' },
                children: [
                    {
                        path: '',
                        component: QueryOverviewComponent,
                        data: { title: 'menu.queries', breadcrumb: 'Dashboard' }
                    },
                    {
                        path: 'list',
                        component: QueryListComponent,
                        data: { title: 'menu.queryList', breadcrumb: 'Query List' }
                    },
                    {
                        path: 'assign',
                        component: QueryAssignComponent,
                        data: { title: 'menu.mapQuery', breadcrumb: 'Map Queries' }
                    },
                ]
            },
            {
                path: 'devices',
                component: DevicesComponent,
                data: { title: 'menu.mydevices', breadcrumb: 'Devices', dashboardName: 'devicesDash' },
                children: [
                    {
                        path: '',
                        component: DevicesOverviewComponent,
                        data: { title: 'menu.dashboard', breadcrumb: 'Dashboard' }
                    },
                    {
                        path: 'list',
                        component: DevicesListComponent,
                        data: { title: 'menu.deviceList', breadcrumb: 'Device List' }
                    },
                ]
            },
            {
                path: 'user',
                component: UserComponent,
                data: { title: 'menu.users', breadcrumb: 'User' },
                children: [
                    { path: '', component: UserOverviewComponent },
                    { path: ':id', component: UserDetailsComponent }
                ]
            },
            {
                path: 'email',
                component: EmailComponent,
                data: { title: 'menu.email', breadcrumb: 'Email', dashboardName: 'emailDash' },
                children: [
                    { path: '', component: EmailRuleOverviewComponent, data: { title: 'menu.email', breadcrumb: 'Dashboard' } },
                    { path: 'config', component: EmailOverviewComponent, data: { title: 'menu.emailConfig', breadcrumb: 'Configuration' } },
                ]
            },
            {
                path: 'settings',
                component: SettingsComponent,
                canActivate: [AuthGuard, RoleGuard],
                data: { title: 'menu.settings', expectedRole: UserRole.SUPERADMIN, breadcrumb: 'Settings' },
            },
            {
                path: 'search',
                component: SearchComponent,
                data: { title: 'menu.searchResults', breadcrumb: 'Search' },
                children: [
                    {
                        path: ':searchquery',
                        component: SearchResultComponent,
                        data: { title: 'menu.searchResults', breadcrumb: 'Results' }
                    },
                ]
            },
            {
                path: 'rules',
                component: RuleComponent,
                data: { title: 'menu.rules', breadcrumb: 'Rules', dashboardName: 'rulesDash' },
                children: [
                    { path: '', component: RuleOverviewComponent, data: { title: 'menu.rules', breadcrumb: 'Dashboard' } },
                    { path: 'list', component: RuleListComponent, data: { title: 'menu.rulesList', breadcrumb: 'Rule List' } },
                    { path: 'regex', component: RuleRegexListComponent, data: { title: 'menu.ruleRegexList', breadcrumb: 'Regex List' } },
                ]
            }

        ]
    },
    {
        path: 'login', component: LoginLayoutComponent,
        children: [
            {
                path: '',
                component: LoginComponent
            }
        ]
    },
    // otherwise redirect to home
    { path: '**', redirectTo: '' }
];

export const appRoutingProviders: any[] = [];

export const routing = RouterModule.forRoot(appRoutes, { useHash: true });
