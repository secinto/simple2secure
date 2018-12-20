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
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {AlertComponent} from './components/index';
import {AuthGuard} from './_guards/index';
import {AlertService, AuthenticationService, DataService, HttpService} from './_services/index';
import {HomeComponent} from './home/index';
import {LoginComponent} from './login/index';
import {ResetComponent} from './resetPassword/index';
import {UpdatePasswordComponent} from './updatePassword/index';
import {UserInvitationComponent} from './invitation/userInvitation.component';
import {RegisterComponent} from './register/index';
import {FooterComponent} from './components/index';
import {FileUploadModule} from 'ng2-file-upload';
import {ModalModule} from 'ngx-modialog';
import {BootstrapModalModule} from 'ngx-modialog/plugins/bootstrap';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {Nl2BrPipeModule} from 'nl2br-pipe';
import {
	MatButtonModule, MatCheckboxModule, MatInputModule, MatFormFieldModule, MatOptionModule, MatSelectModule, MatMenuModule, MatIconModule,
	MatSidenavModule, MatButtonToggleModule, MatTableModule, MatTabsModule, MatPaginatorModule, MatSortModule, MatProgressSpinnerModule, MatDialogModule,
	MatNativeDateModule, MatDatepickerModule, MatCardModule
} from '@angular/material';
import {AppComponent} from './app.component';
import {DashboardLayoutComponent} from './_layouts/dashboardLayout/index';
import {LoginLayoutComponent} from './_layouts/loginLayout/index';
import {NavbarComponent} from './navbar/index';
import {ConfigurationDetailsComponent} from './configuration/index';
import {
	UserComponent, UserDetailsComponent, UserOverviewComponent, UserGroupComponent, UserGroupDialogComponent,
	UserGroupApplyConfigComponent, UserProbeChangeGroupComponent, UserContextAddDialogComponent
} from './user/index';
import {
	OsqueryConfigurationDetailsComponent, OsqueryConfigurationEditComponent
} from './osquery/index';
import {
	NetworkConfigurationProcessorDetailsComponent, NetworkConfigurationStepDetailsComponent,
	NetworkStepConfigurationEditComponent, NetworkProcessorConfigurationEditComponent
}
	from './network/index';

import {
	ReportComponent, NetworkReportOverviewComponent, NetworkReportDetailsComponent, OsQueryReportOverviewComponent,
	OsQueryReportDetailsComponent, ReportOverviewComponent
} from './report/index';

import {EmailOverviewComponent, EmailInboxComponent, EmailAccountAddComponent} from './email/index';
import {RuleComponent, RuleOverviewComponent, RuleAddComponent} from './rule/index';
import {SettingsComponent} from './settings/index';
import {ActivationComponent, ActivatedComponent} from './activation/index';
import {NotificationComponent, NotificationOverviewComponent} from './notification/index';
import {AnalysisComponent} from './analysis/index';
import {EqualValidator} from './_directives/equalValidator';
import {
	OrbiterComponent, OrbiterToolsComponent, OrbiterToolTestRunComponent, OrbiterToolTestComponent,
	OrbiterToolTestResultComponent, OrbiterToolTemplateComponent, OrbiterTestTemplateComponent
} from './orbiter/index';
import {ConfirmationDialog} from './dialog/confirmation-dialog';
import {ChartModule} from 'angular-highcharts';
import {TreeModule} from 'angular-tree-component';
import {TreeTableModule} from 'ng-treetable';
import {RoleGuard} from './_guards/role.guard';
import {AuthInterceptor} from './_helpers/auth.interceptor';
import {TreeviewModule} from 'ngx-treeview';
import {SelectContextDialog} from './dialog/select-context';

export const httpInterceptorProviders = [
	{provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
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
		UserInvitationComponent,
		RegisterComponent,
		DashboardLayoutComponent,
		LoginLayoutComponent,
		NavbarComponent,
		ConfigurationDetailsComponent,
		NetworkReportOverviewComponent,
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
		UserGroupApplyConfigComponent,
		UserProbeChangeGroupComponent,
		UserContextAddDialogComponent,
		OsQueryReportOverviewComponent,
		OsQueryReportDetailsComponent,
		OsqueryConfigurationDetailsComponent,
		OsqueryConfigurationEditComponent,
		FooterComponent,
		EqualValidator,
		OrbiterComponent,
		OrbiterToolsComponent,
		OrbiterToolTestRunComponent,
		OrbiterToolTestComponent,
		OrbiterToolTestResultComponent,
		OrbiterToolTemplateComponent,
		OrbiterTestTemplateComponent,
		ConfirmationDialog,
		SelectContextDialog,
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
		AnalysisComponent,
		ReportComponent,
		ReportOverviewComponent
	],
	entryComponents: [
		ConfirmationDialog,
		SelectContextDialog,
		UserGroupDialogComponent,
		NetworkProcessorConfigurationEditComponent,
		NetworkStepConfigurationEditComponent,
		OsqueryConfigurationEditComponent,
		UserGroupApplyConfigComponent,
		UserProbeChangeGroupComponent,
		UserContextAddDialogComponent,
		OrbiterTestTemplateComponent,
		OrbiterToolTestResultComponent,
		OrbiterToolTestRunComponent,
		EmailAccountAddComponent,
		EmailInboxComponent,
		RuleOverviewComponent,
		RuleAddComponent
	],
	providers: [
		AuthGuard,
		RoleGuard,
		AlertService,
		AuthenticationService,
		HttpService,
		DataService,
		httpInterceptorProviders
	],
	bootstrap: [AppComponent]
})

export class AppModule {

}

// required for AOT compilation
export function HttpLoaderFactory(http: HttpClient) {
	return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}
