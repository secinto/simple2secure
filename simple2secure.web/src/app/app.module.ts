import {DatePipe} from '@angular/common';
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
import {OrbiterOverviewComponent} from './orbiter/orbiterOverview.component';
import {TestDetailsComponent} from './orbiter/testDetails.component';
import {TestResultDetailsComponent} from './report/testResultDetails.component';
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
	MatNativeDateModule, MatDatepickerModule, MatCardModule, MatExpansionModule, MatBadgeModule
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
import {NotificationComponent} from './notification/index';
import {AnalysisComponent, AddQueryDialog} from './analysis/index';
import {EqualValidator} from './_directives/equalValidator';
import {
	OrbiterComponent, OrbiterToolTestComponent, OrbiterToolTestListComponent
} from './orbiter/index';
import {ConfirmationDialog} from './dialog/confirmation-dialog';
import {ChartModule, HIGHCHARTS_MODULES} from 'angular-highcharts';
import {TreeModule} from 'angular-tree-component';
import {TreeTableModule} from 'ng-treetable';
import {RoleGuard} from './_guards/role.guard';
import {AuthInterceptor} from './_helpers/auth.interceptor';
import {TreeviewModule} from 'ngx-treeview';
import {SelectContextDialog} from './dialog/select-context';
import {NotificationDialog} from './dialog/notification-dialog';
import * as highstock from 'highcharts/modules/stock.src';
import * as exporting from 'highcharts/modules/exporting.src';
import { NgxSpinnerModule } from 'ngx-spinner';
import { Ng2GoogleChartsModule } from 'ng2-google-charts';
import { AgmCoreModule } from '@agm/core';
import { NgxJsonViewModule } from 'ng-json-view';
import { AgmDirectionModule } from 'agm-direction';
import {TestResultComponent} from './report/testResult.component';
import {OrbiterToolTestScheduledListComponent} from './orbiter/orbiterToolTestScheduledList.component';
import {HelperService} from './_services/helper.service';

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
		MatBadgeModule,
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
		MatExpansionModule,
		HttpClientModule,
		ChartModule,
		TreeTableModule,
		NgxSpinnerModule,
		Ng2GoogleChartsModule,
		TreeviewModule.forRoot(),
		TreeModule.forRoot(),
		NgxJsonViewModule,
		TranslateModule.forRoot({
			loader: {
				provide: TranslateLoader,
				useFactory: HttpLoaderFactory,
				deps: [HttpClient]
			}
		}),
		AgmCoreModule.forRoot({
			// please get your own API key here:
			// https://developers.google.com/maps/documentation/javascript/get-api-key?hl=en
			apiKey: 'AIzaSyCo6SKY-rBYhT-6p1bLCaiH-IdYEi29oKI'
		}),
		AgmDirectionModule,

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
		OrbiterOverviewComponent,
		TestResultComponent,
		ConfirmationDialog,
		SelectContextDialog,
		NotificationDialog,
		EmailOverviewComponent,
		EmailInboxComponent,
		EmailAccountAddComponent,
		SettingsComponent,
		ActivationComponent,
		ActivatedComponent,
		NotificationComponent,
		RuleComponent,
		RuleOverviewComponent,
		RuleAddComponent,
		AnalysisComponent,
		AddQueryDialog,
		ReportComponent,
		ReportOverviewComponent,
		TestResultDetailsComponent,
		TestDetailsComponent,
		OrbiterToolTestComponent,
		OrbiterToolTestListComponent,
		OrbiterToolTestScheduledListComponent
	],
	entryComponents: [
		ConfirmationDialog,
		SelectContextDialog,
		NotificationDialog,
		UserGroupDialogComponent,
		NetworkProcessorConfigurationEditComponent,
		NetworkStepConfigurationEditComponent,
		OsqueryConfigurationEditComponent,
		UserGroupApplyConfigComponent,
		UserProbeChangeGroupComponent,
		UserContextAddDialogComponent,
		EmailAccountAddComponent,
		EmailInboxComponent,
		RuleOverviewComponent,
		RuleAddComponent,
		OsQueryReportDetailsComponent,
		NetworkReportDetailsComponent,
		UserDetailsComponent,
		AddQueryDialog,
		TestResultDetailsComponent,
		TestDetailsComponent
	],
	providers: [
		AuthGuard,
		RoleGuard,
		AlertService,
		AuthenticationService,
		HttpService,
		DataService,
		HelperService,
		httpInterceptorProviders,
		{ provide: HIGHCHARTS_MODULES, useFactory: () => [ highstock, exporting ]},
		DatePipe
	],
	bootstrap: [AppComponent]
})

export class AppModule {

}

// required for AOT compilation
export function HttpLoaderFactory(http: HttpClient) {
	return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}
