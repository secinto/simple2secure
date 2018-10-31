import {Routes, RouterModule} from '@angular/router';
import {HomeComponent} from './home/index';
import {DashboardLayoutComponent} from './_layouts/dashboardLayout/index';
import {LoginLayoutComponent} from './_layouts/loginLayout/index';
import {LoginComponent} from './login/index';
import {ResetComponent} from './resetPassword/index';
import {AnalysisComponent} from './analysis/index';
import {RegisterComponent, RegisterByEmailComponent} from './register/index';
import {ConfigurationComponent, ConfigurationDetailsComponent, ConfigurationOverviewComponent, ConfigurationTypeComponent} from './configuration/index';
import {
  ReportComponent, ReportDetailsComponent, ReportOverviewComponent, OsqueryConfigurationComponent,
  OsqueryConfigurationDetailsComponent, OsqueryConfigurationEditComponent, OsqueryConfigurationDevicesComponent, OsqueryConfigurationGroupsComponent, OsqueryOverviewComponent
} from './osquery/index';
import {GuiComponent, GuiOverviewComponent, GuiImportComponent, GuiUserOverviewComponent} from './gui/index';
import {UserComponent, UserDetailsComponent, UserOverviewComponent} from './user/index';
import {
  NetworkComponent, NetworkConfigurationComponent, NetworkReportOverviewComponent, NetworkConfigurationTypeComponent,
  NetworkConfigurationStepDetailsComponent, NetworkConfigurationProcessorDetailsComponent, NetworkStepConfigurationEditComponent, NetworkProcessorConfigurationEditComponent,
  NetworkReportDetailsComponent, NetworkOverviewComponent
} from './network/index';
import {AuthGuard} from './_guards/index';
import {UpdatePasswordComponent} from './updatePassword';
import {OrbiterComponent, OrbiterConfigurationComponent, OrbiterToolsComponent, OrbiterToolTestRunComponent, OrbiterToolTestComponent, OrbiterToolTestResultComponent} from './orbiter/index';
import {EmailComponent, EmailOverviewComponent, EmailInboxComponent, EmailAccountAddComponent} from './email/index';
import {SettingsComponent} from './settings/index';
import {NotificationComponent, NotificationOverviewComponent} from './notification/index';
import {ActivationComponent, ActivatedComponent} from './activation/index';
import {RuleOverviewComponent, RuleAddComponent} from './rule/index';
import {UserGroupComponent} from './user/userGroup.component';
import {RoleGuard} from './_guards/role.guard';
import {UserRole} from './_models';
const appRoutes: Routes = [
  {
    path: '', component: DashboardLayoutComponent, canActivate: [AuthGuard],
    children: [
      {
        path: '',
        component: HomeComponent,
      },
      {
        path: 'config',
        component: ConfigurationComponent,
        data: {title: 'menu.configuration'},
        children: [
          {path: '', component: ConfigurationTypeComponent, data: {title: 'dashboard.configuration'}},
          {path: ':type', component: ConfigurationOverviewComponent, data: {title: 'menu.generalConfig'}},
          {path: ':type/:id', component: ConfigurationDetailsComponent, data: {title: 'menu.updateConfig'}}
        ]
      },

      {
          path: 'analysis',
          component: AnalysisComponent,
          data: {title: 'menu.analysis'}
      },

      {
        path: 'osquery',
        component: ReportComponent,
        data: {title: 'menu.osquery'},
        children: [
          {path: '', component: OsqueryOverviewComponent, data: {title: 'menu.osquery'}},
          {path: 'config', component: OsqueryConfigurationComponent, data: {title: 'menu.osqueryConfig'}},
          {path: 'config/details', component: OsqueryConfigurationDetailsComponent, data: {title: 'menu.currentQueries'}},
          {path: 'config/devices', component: OsqueryConfigurationDevicesComponent, data: {title: 'table.myprobes'}},
          {path: 'config/group', component: OsqueryConfigurationGroupsComponent, data: {title: 'table.mygroups'}},
          {path: 'report', component: ReportOverviewComponent, data: {title: 'menu.queryReport'}},
          {path: 'report/:id', component: ReportDetailsComponent, data: {title: 'menu.queryReportDetails'}},
        ]
      },
      {
        path: 'network',
        component: NetworkComponent,
        data: {title: 'menu.networkanalysis'},
        children: [
          {path: '', component: NetworkOverviewComponent, data: {title: 'menu.networkanalysis'}},
          {path: 'config', component: NetworkConfigurationComponent, data: {title: 'menu.networkanalysisConfig'}},
          {path: 'config/type', component: NetworkConfigurationTypeComponent, data: {title: 'menu.networkanalysisConfigTypes'}},
          {path: 'config/devices', component: OsqueryConfigurationDevicesComponent, data: {title: 'table.myprobes'}},
          {path: 'config/groups', component: OsqueryConfigurationGroupsComponent, data: {title: 'table.mygroups'}},
          {path: 'config/details/step', component: NetworkConfigurationStepDetailsComponent, data: {title: 'menu.networkanalysisStepConfig'}},
          {path: 'config/details/processor', component: NetworkConfigurationProcessorDetailsComponent, data: {title: 'menu.networkanalysisProcessorConfig'}},
          {path: 'config/details/processor/new', component: NetworkProcessorConfigurationEditComponent, data: {title: 'menu.currentProcessor'}},
          {path: 'report', component: NetworkReportOverviewComponent, data: {title: 'menu.networkanalysisReports'}},
          {path: 'report/:id', component: NetworkReportDetailsComponent, data: {title: 'networkreport.details'}},
        ]
      },
        {
            path: 'orbiter',
            component: OrbiterComponent,
            data: {title: 'menu.orbiter'},
            children: [
                {path: '', component: OrbiterConfigurationComponent, data: {title: 'menu.orbiterConfig'}},
                {path: 'tools', component: OrbiterToolsComponent, data: {title: 'menu.orbiterTools'}},
                {path: 'tools/test/run', component: OrbiterToolTestRunComponent, data: {title: 'button.runtests'}},
                {path: 'tools/test', component: OrbiterToolTestComponent, data: {title: 'menu.tests'}},
                {path: 'tools/test/result', component: OrbiterToolTestResultComponent, data: {title: 'button.testresults'}},
            ]
        },


      {
        path: 'gui',
        component: GuiComponent,
        data: {title: 'GUI'},
        children: [
          {path: '', component: GuiOverviewComponent},
          {path: 'edit', component: GuiUserOverviewComponent},
          {path: 'upload', component: GuiImportComponent}
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
            {path: '', component: EmailOverviewComponent},
            {path: ':id/inbox', component: EmailInboxComponent, data: {title: 'table.inbox'}},
            {path: ':id/inbox/rule/overview', component: RuleOverviewComponent, data: {title: 'menu.rules'}},
            {path: ':id/inbox/rule/add', component: RuleAddComponent, data: {title: 'button.addRule'}},
            {path: ':id/inbox/rule/edit', component: RuleAddComponent, data: {title: 'menu.editRule'}}
          ]
       },
       {
           path: 'settings',
           component: SettingsComponent,
           canActivate: [RoleGuard],
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
  {
    path: 'register', component: LoginLayoutComponent,
    children: [
      {
        path: '',
        component: RegisterComponent
      },
      {
          path: 'email',
          component: RegisterByEmailComponent
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
  // otherwise redirect to home
  {path: '**', redirectTo: ''}
];

export const appRoutingProviders: any[] = [

];

export const routing = RouterModule.forRoot(appRoutes, {useHash: true});
