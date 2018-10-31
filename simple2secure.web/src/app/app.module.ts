import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DataTableModule} from 'angular2-datatable';
import {Angular2FontawesomeModule} from 'angular2-fontawesome/angular2-fontawesome';
import {TabsModule} from 'ng2-tabs';
import {routing} from './app.routing';
import {TranslateModule, TranslateLoader} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import {AlertComponent} from './components/index';
import {AuthGuard} from './_guards/index';
import {AlertService, AuthenticationService, ImportService, DataService, HttpService} from './_services/index';
import {HomeComponent} from './home/index';
import {LoginComponent} from './login/index';
import {ResetComponent} from './resetPassword/index';
import {UpdatePasswordComponent} from './updatePassword/index';
import {RegisterComponent, RegisterByEmailComponent} from './register/index';
import {FooterComponent} from './components/index';
import {FileUploadModule} from 'ng2-file-upload';
import {ModalModule} from 'ngx-modialog';
import {BootstrapModalModule} from 'ngx-modialog/plugins/bootstrap';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {Nl2BrPipeModule} from 'nl2br-pipe';
import {MatButtonModule, MatCheckboxModule, MatInputModule, MatFormFieldModule, MatOptionModule, MatSelectModule, MatMenuModule, MatIconModule,
    MatSidenavModule, MatButtonToggleModule, MatTableModule, MatTabsModule, MatPaginatorModule, MatSortModule, MatProgressSpinnerModule, MatDialogModule,
    MatNativeDateModule, MatDatepickerModule, MatCardModule} from '@angular/material';
import {AppComponent} from './app.component';
import {DashboardLayoutComponent} from './_layouts/dashboardLayout/index';
import {LoginLayoutComponent} from './_layouts/loginLayout/index';
import {NavbarComponent} from './navbar/index';
import {ConfigurationComponent, ConfigurationDetailsComponent, ConfigurationOverviewComponent, ConfigurationTypeComponent} from './configuration/index';
import {UserComponent, UserDetailsComponent, UserOverviewComponent, UserGroupComponent, UserGroupDialogComponent} from './user/index';
import {GuiComponent, GuiOverviewComponent, GuiImportComponent, GuiUserOverviewComponent} from './gui/index';
import {
  ReportComponent, ReportDetailsComponent, ReportOverviewComponent, OsqueryConfigurationComponent,
  OsqueryConfigurationDetailsComponent, OsqueryConfigurationEditComponent, OsqueryConfigurationDevicesComponent, OsqueryConfigurationGroupsComponent, OsqueryOverviewComponent
} from './osquery/index';
import {
  NetworkComponent, NetworkConfigurationComponent, NetworkReportOverviewComponent, NetworkConfigurationTypeComponent, NetworkConfigurationDetailsComponent,
  NetworkConfigurationProcessorDetailsComponent, NetworkConfigurationStepDetailsComponent, NetworkStepConfigurationEditComponent, NetworkProcessorConfigurationEditComponent,
  NetworkReportDetailsComponent, NetworkOverviewComponent
}
  from './network/index';

import {EmailComponent, EmailOverviewComponent, EmailInboxComponent, EmailAccountAddComponent} from './email/index';
import {RuleComponent, RuleOverviewComponent, RuleAddComponent} from './rule/index';
import {SettingsComponent} from './settings/index';
import {ActivationComponent, ActivatedComponent} from './activation/index';
import {NotificationComponent, NotificationOverviewComponent} from './notification/index';
import {AnalysisComponent} from './analysis/index';
import {EqualValidator} from './_directives/equalValidator';
import {OrbiterComponent, OrbiterConfigurationComponent, OrbiterToolsComponent, OrbiterToolTestRunComponent, OrbiterToolTestComponent, OrbiterToolTestResultComponent} from './orbiter/index';
import {ConfirmationDialog} from './dialog/confirmation-dialog';
import { ChartModule } from 'angular-highcharts';
import { TreeModule } from 'angular-tree-component';
import {TreeTableModule} from 'ng-treetable';
import {RoleGuard} from './_guards/role.guard';
import {AuthInterceptor} from './_helpers/auth.interceptor';
import { TreeviewModule } from 'ngx-treeview';

export const httpInterceptorProviders = [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
];

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    Angular2FontawesomeModule,
    FormsModule,
    ReactiveFormsModule,
    DataTableModule,
    routing,
    FileUploadModule,
    TabsModule,
    ModalModule.forRoot(),
    BootstrapModalModule,
    NgbModule.forRoot(),
    Nl2BrPipeModule,
    NgxJsonViewerModule,
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatOptionModule,
    MatSelectModule,
    MatMenuModule,
    MatIconModule,
    MatSidenavModule,
    MatButtonToggleModule,
    MatTableModule,
    MatTabsModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCardModule,
    HttpClientModule,
    //NgxDnDModule,
    ChartModule,
    TreeTableModule,
    TreeviewModule.forRoot(),
    TreeModule.forRoot(),
    TranslateModule.forRoot({
        loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
        }
    })

  ],
  declarations: [
    AppComponent,
    AlertComponent,
    HomeComponent,
    LoginComponent,
    ResetComponent,
    UpdatePasswordComponent,
    RegisterComponent,
    RegisterByEmailComponent,
    DashboardLayoutComponent,
    LoginLayoutComponent,
    NavbarComponent,
    ConfigurationComponent,
    ConfigurationDetailsComponent,
    ConfigurationOverviewComponent,
    ConfigurationTypeComponent,
    NetworkComponent,
    NetworkOverviewComponent,
    NetworkConfigurationComponent,
    NetworkConfigurationDetailsComponent,
    NetworkReportOverviewComponent,
    NetworkConfigurationTypeComponent,
    NetworkConfigurationStepDetailsComponent,
    NetworkConfigurationProcessorDetailsComponent,
    NetworkStepConfigurationEditComponent,
    NetworkProcessorConfigurationEditComponent,
    NetworkReportDetailsComponent,
    UserComponent,
    UserDetailsComponent,
    UserOverviewComponent,
    UserGroupComponent,
    UserGroupDialogComponent,
    ReportComponent,
    ReportOverviewComponent,
    ReportDetailsComponent,
    OsqueryConfigurationComponent,
    OsqueryOverviewComponent,
    OsqueryConfigurationDetailsComponent,
    OsqueryConfigurationEditComponent,
    OsqueryConfigurationDevicesComponent,
    OsqueryConfigurationGroupsComponent,
    GuiComponent,
    GuiOverviewComponent,
    GuiImportComponent,
    GuiUserOverviewComponent,
    FooterComponent,
    EqualValidator,
    OrbiterComponent,
    OrbiterConfigurationComponent,
    OrbiterToolsComponent,
    OrbiterToolTestRunComponent,
    OrbiterToolTestComponent,
    OrbiterToolTestResultComponent,
    ConfirmationDialog,
    EmailComponent,
    EmailOverviewComponent,
    EmailInboxComponent,
    EmailAccountAddComponent,
    SettingsComponent,
    ActivationComponent,
    ActivatedComponent,
	NotificationComponent,
	NotificationOverviewComponent,
	RuleComponent,
	RuleOverviewComponent,
	RuleAddComponent,
	AnalysisComponent
  ],
  entryComponents: [
    ConfirmationDialog,
    UserGroupDialogComponent,
    NetworkProcessorConfigurationEditComponent,
    NetworkStepConfigurationEditComponent,
    OsqueryConfigurationEditComponent
  ],
  providers: [
    AuthGuard,
    RoleGuard,
    AlertService,
    ImportService,
    AuthenticationService,
    HttpService,
    DataService,
    httpInterceptorProviders
  ],
  bootstrap: [AppComponent]
})

export class AppModule {

}

//required for AOT compilation
export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}
