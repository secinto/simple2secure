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
import {NetworkReportDetailsComponent, NetworkReportOverviewComponent,} from './network/index';
import {AuthGuard} from './_guards/index';
import {UpdatePasswordComponent} from './updatePassword';
import {
	OrbiterComponent,
	OrbiterToolTestComponent, OrbiterToolTemplateComponent
} from './orbiter/index';
import {EmailOverviewComponent} from './email/index';
import {SettingsComponent} from './settings/index';
import {NotificationComponent, NotificationOverviewComponent} from './notification/index';
import {ActivationComponent, ActivatedComponent} from './activation/index';
import {RuleOverviewComponent, RuleAddComponent} from './rule/index';
import {UserGroupComponent} from './user/userGroup.component';
import {RoleGuard} from './_guards/role.guard';
import {UserRole} from './_models';
import {OsQueryReportDetailsComponent, OsQueryReportOverviewComponent} from './osquery';
import {ReportComponent, ReportOverviewComponent} from './report';
import {UserInvitationComponent} from './invitation/userInvitation.component';
import {TestResultComponent} from './orbiter/testResult.component';

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
				]
			},
			{
				path: 'orbiter',
				component: OrbiterComponent,
				data: {title: 'menu.orbiter'},
				children: [
					{path: '', component: OrbiterOverviewComponent, data: {title: 'menu.orbiter'}},
					{path: 'testResult', component: TestResultComponent, data: {title: 'button.testresults'}},
					{path: 'test', component: OrbiterToolTestComponent, data: {title: 'menu.tests'}},
					{path: 'template', component: OrbiterToolTemplateComponent, data: {title: 'menu.templates'}},
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
				component: EmailOverviewComponent,
				data: {title: 'menu.email'},
				children: [
					{path: ':id/inbox/rule/overview', component: RuleOverviewComponent, data: {title: 'menu.rules'}},
					{path: ':id/inbox/rule/add', component: RuleAddComponent, data: {title: 'button.addRule'}},
					{path: ':id/inbox/rule/edit', component: RuleAddComponent, data: {title: 'menu.editRule'}}
				]
			},
			{
				path: 'settings',
				component: SettingsComponent,
				canActivate: [AuthGuard, RoleGuard],
				data: {title: 'menu.settings', expectedRole: UserRole.SUPERADMIN},
			},
			{
				path: 'notification',
				component: NotificationComponent,
				data: {title: 'menu.notifications'},
				children: [
					{path: '', component: NotificationOverviewComponent},
				]
			},
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
