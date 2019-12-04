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

import {Routes, RouterModule} from '@angular/router';
import {DashboardLayoutComponent} from './_layouts/dashboardLayout';
import {LoginLayoutComponent} from './_layouts/loginLayout';
import {AuthGuard, RoleGuard} from './_guards';
import {UserRole} from './_models';
import {HomeComponent} from './home';
import {LoginComponent} from './login';
import {SettingsComponent} from './settings';
import {ResetComponent} from './resetPassword';
import {ResendComponent} from './resendActivation';
import {UpdatePasswordComponent} from './updatePassword';
import {UserInvitationComponent} from './invitation';
import {RegisterComponent} from './register';
import {ActivationComponent, ActivatedComponent} from './activation';
import {AnalysisComponent} from './analysis';
import {UserGroupComponent, UserComponent, UserDetailsComponent, UserOverviewComponent} from './user';
import {EmailComponent, EmailOverviewComponent, EmailRuleOverviewComponent} from './email';
import {RuleOverviewComponent} from './rule';
import {OrbiterOverviewComponent,
	OrbiterComponent,
	OrbiterToolTestComponent,
	OrbiterToolTestListComponent,
	OrbiterToolTestSequenceListComponent,
	OrbiterToolTestScheduledListComponent} from './orbiter';
import {OsQueryReportDetailsComponent,
	OsQueryReportOverviewComponent,
	ReportComponent,
	ReportOverviewComponent,
	NetworkReportDetailsComponent,
	NetworkReportOverviewComponent} from './report';
import {TestResultComponent} from './report/testResult.component';
import {SearchComponent, SearchResultComponent} from './search';
import { TestSequenceResultComponent } from './report/testSequenceResult.component';
import { OrbiterScheduledSequencesListComponent } from './orbiter/orbiterScheduledSequencesList.component';
import { OrbiterSystemsUnderTestListComponent } from './orbiter/orbiterSystemsUnderTestList.component';
import {DevicesComponent, DevicesOverviewComponent, DevicesListComponent} from './devices';
import {QueriesComponent} from "./queries";
import {QueryOverviewComponent} from "./queries/queryOverview.component";
import {QueryListComponent} from "./queries/queryList.component";

const appRoutes: Routes = [
	{
		path: '', component: DashboardLayoutComponent, canActivate: [AuthGuard],
		children: [
			{
				path: '',
				component: HomeComponent,
			},

			{
				path: 'analysis',
				component: AnalysisComponent,
				data: {title: 'menu.analysis', breadcrumb: 'Analysis'}
			},
			{
				path: 'report',
				component: ReportComponent,
				data: {title: 'menu.reports', breadcrumb: 'Reports'},
				children: [
					{path: '', component: ReportOverviewComponent, data: {title: 'menu.reports', breadcrumb: 'Dashboard'}},
					{path: 'network', component: NetworkReportOverviewComponent, data: {title: 'menu.networkanalysisReports', breadcrumb: 'Network'}},
					{path: 'network/:id', component: NetworkReportDetailsComponent, data: {title: 'networkreport.details'}},
					{path: 'osquery', component: OsQueryReportOverviewComponent, data: {title: 'menu.queryReport', breadcrumb: 'OsQuery'}},
					{path: 'osquery/:id', component: OsQueryReportDetailsComponent, data: {title: 'menu.queryReportDetails'}},
					{path: 'testResult', component: TestResultComponent, data: {title: 'menu.testResults', breadcrumb: 'Test Results'}},
					{path: 'testSequenceResult', component: TestSequenceResultComponent, data: {title: 'menu.testSequenceResults', breadcrumb: 'Sequence Results'}},
				]
			},
			{
				path: 'tests',
				component: OrbiterComponent,
				data: {title: 'menu.tests', breadcrumb: 'Tests'},
				children: [
					{path: '', component: OrbiterOverviewComponent, data: {title: 'menu.orbiter', breadcrumb: 'Dashboard'}},
					{path: 'test', component: OrbiterToolTestComponent, data: {title: 'menu.tests', breadcrumb: 'Test Templates'}},
					{path: 'test/:id', component: OrbiterToolTestListComponent, data: {title: 'menu.tests'}},
					{path: 'scheduledTests', component: OrbiterToolTestScheduledListComponent, data: {title: 'test.scheduled', breadcrumb: 'Scheduled Tests'}},
					{path: 'test/sequences/:id', component: OrbiterToolTestSequenceListComponent, data : {title: 'test.sequences'}},
					{path: 'scheduledSequences', component: OrbiterScheduledSequencesListComponent, data : {title: 'sequence.scheduled', breadcrumb: 'Scheduled Sequences'}},
					{path: 'sut', component: OrbiterSystemsUnderTestListComponent, data : {title: 'orbiter.sut', breadcrumb: 'System Under Test'}},
				]
			},
			{
				path: 'queries',
				component: QueriesComponent,
				data: {title: 'menu.queries', breadcrumb: 'Queries'},
				children: [
					{path: '', component: QueryOverviewComponent, data: {title: 'menu.queries', breadcrumb: 'Dashboard'}},
					{path: 'list', component: QueryListComponent, data: {title: 'menu.queryList', breadcrumb: 'Query List'}},
				]
			},
			{
				path: 'devices',
				component: DevicesComponent,
				data: {title: 'menu.mydevices', breadcrumb: 'Devices'},
				children: [
					{path: '', component: DevicesOverviewComponent, data: {title: 'menu.dashboard', breadcrumb: 'Dashboard'}},
					{path: 'list', component: DevicesListComponent, data: {title: 'menu.deviceList', breadcrumb: 'Device List'}},
					]
			},
			{
				path: 'user',
				component: UserComponent,
				data: {title: 'menu.users', breadcrumb: 'User'},
				children: [
					{path: '', component: UserOverviewComponent},
					{path: ':id', component: UserDetailsComponent},
					{path: 'group/:id', component: UserGroupComponent}
				]
			},
			{
				path: 'email',
				component: EmailComponent,
				data: {title: 'menu.email', breadcrumb: 'Email'},
				children: [
					{path: '', component: EmailRuleOverviewComponent, data: {title: 'menu.email', breadcrumb: 'Dashboard'}},
					{path: 'config', component: EmailOverviewComponent, data: {title: 'menu.emailConfig', breadcrumb: 'Configuration'}},
					{path: 'rules', component: RuleOverviewComponent, data: {title: 'menu.rules', breadcrumb: 'Rules'}},
				]
			},
			{
				path: 'settings',
				component: SettingsComponent,
				canActivate: [AuthGuard, RoleGuard],
				data: {title: 'menu.settings', expectedRole: UserRole.SUPERADMIN, breadcrumb: 'Settings'},
			},
			{
				path: 'search',
				component: SearchComponent,
				data: {title: 'menu.searchResults', breadcrumb: 'Search'},
				children: [
					{path: ':searchquery', component: SearchResultComponent, data: {title: 'menu.searchResults', breadcrumb: 'Results'}},
				]
			}
		]
	},
	{
		path: 'account', component: LoginLayoutComponent,
		children: [
			{
				path: 'activate/:id',
				component: ActivationComponent
			},
			{
				path: 'activated',
				component: ActivatedComponent
			},
			{
				path: 'updatePassword/:id',
				component: UpdatePasswordComponent
			},
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
	{
		path: 'register', component: LoginLayoutComponent,
		children: [
			{
				path: '',
				component: RegisterComponent
			}
		]
	},
	{
		path: 'resendActivation', component: LoginLayoutComponent,
		children: [
			{
				path: '',
				component: ResendComponent
			}
		]
	},
	{
		path: 'reset', component: LoginLayoutComponent,
		children: [
			{
				path: '',
				component: ResetComponent
			}
		]
	},

	{
		path: 'resetPassword/:token', component: LoginLayoutComponent,
		children: [
			{
				path: '',
				component: UpdatePasswordComponent
			}
		]
	},
	{
		path: 'invitation/:id', component: LoginLayoutComponent,
		children: [
			{
				path: '',
				component: UserInvitationComponent
			}
		]
	},
	// otherwise redirect to home
	{path: '**', redirectTo: ''}
];

export const appRoutingProviders: any[] = [];

export const routing = RouterModule.forRoot(appRoutes, {useHash: true});
