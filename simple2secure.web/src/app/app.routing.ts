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
import {HomeComponent} from './home/index';
import {DashboardLayoutComponent} from './_layouts/dashboardLayout/index';
import {LoginLayoutComponent} from './_layouts/loginLayout/index';
import {LoginComponent} from './login/index';
import {OrbiterOverviewComponent} from './orbiter/orbiterOverview.component';
import {ResetComponent} from './resetPassword/index';
import {AnalysisComponent} from './analysis/index';
import {RegisterComponent} from './register/index';
import {UserComponent, UserDetailsComponent, UserOverviewComponent} from './user/index';
import {NetworkReportDetailsComponent, NetworkReportOverviewComponent} from './network/index';
import {AuthGuard} from './_guards/index';
import {UpdatePasswordComponent} from './updatePassword';
import {OrbiterComponent, OrbiterToolTestComponent, OrbiterToolTestListComponent} from './orbiter/index';
import {EmailComponent, EmailOverviewComponent, EmailRuleOverviewComponent} from './email/index';
import {SettingsComponent} from './settings/index';
import {ActivationComponent, ActivatedComponent} from './activation/index';
import {RuleOverviewComponent, RuleAddComponent} from './rule/index';
import {UserGroupComponent} from './user/userGroup.component';
import {RoleGuard} from './_guards/role.guard';
import {UserRole} from './_models';
import {OsQueryReportDetailsComponent, OsQueryReportOverviewComponent} from './osquery';
import {ReportComponent, ReportOverviewComponent} from './report';
import {UserInvitationComponent} from './invitation/userInvitation.component';
import {TestResultComponent} from './report/testResult.component';
import {OrbiterToolTestScheduledListComponent} from './orbiter/orbiterToolTestScheduledList.component';
import {SearchComponent} from './search/search.component';
import {SearchResultComponent} from './search/searchResult.component';

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
				data: {title: 'menu.analysis'}
			},
			{
				path: 'report',
				component: ReportComponent,
				data: {title: 'menu.reports'},
				children: [
					{path: '', component: ReportOverviewComponent, data: {title: 'menu.reports'}},
					{path: 'network', component: NetworkReportOverviewComponent, data: {title: 'menu.networkanalysisReports'}},
					{path: 'network/:id', component: NetworkReportDetailsComponent, data: {title: 'networkreport.details'}},
					{path: 'osquery', component: OsQueryReportOverviewComponent, data: {title: 'menu.queryReport'}},
					{path: 'osquery/:id', component: OsQueryReportDetailsComponent, data: {title: 'menu.queryReportDetails'}},
					{path: 'testResult', component: TestResultComponent, data: {title: 'button.testresults'}},
				]
			},
			{
				path: 'orbiter',
				component: OrbiterComponent,
				data: {title: 'menu.orbiter'},
				children: [
					{path: '', component: OrbiterOverviewComponent, data: {title: 'menu.orbiter'}},
					{path: 'test', component: OrbiterToolTestComponent, data: {title: 'menu.tests'}},
					{path: 'test/:id', component: OrbiterToolTestListComponent, data: {title: 'menu.tests'}},
					{path: 'scheduledTests', component: OrbiterToolTestScheduledListComponent, data: {title: 'test.scheduled'}},
				]
			},
			{
				path: 'user',
				component: UserComponent,
				data: {title: 'menu.users'},
				children: [
					{path: '', component: UserOverviewComponent},
					{path: ':id', component: UserDetailsComponent},
					{path: 'group/:id', component: UserGroupComponent}
				]
			},
			{
				path: 'email',
				component: EmailComponent,
				data: {title: 'menu.email'},
				children: [
					{path: '', component: EmailRuleOverviewComponent, data: {title: 'menu.email'}},
					{path: 'config', component: EmailOverviewComponent, data: {title: 'menu.emailConfig'}},
					{path: 'rules', component: RuleOverviewComponent, data: {title: 'menu.rules'}},
				]
			},
			{
				path: 'settings',
				component: SettingsComponent,
				canActivate: [AuthGuard, RoleGuard],
				data: {title: 'menu.settings', expectedRole: UserRole.SUPERADMIN},
			},
			{
				path: 'search',
				component: SearchComponent,
				data: {title: 'menu.searchResults'},
				children: [
					{path: ':searchquery', component: SearchResultComponent, data: {title: 'menu.searchResults'}},
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
