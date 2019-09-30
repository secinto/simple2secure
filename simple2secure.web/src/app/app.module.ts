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

/**
 * Internal core components
 */

import {HomeComponent} from './home';
import {LoginComponent} from './login';
import {ActivationComponent, ActivatedComponent} from './activation';
import {UserInvitationComponent} from './invitation';
import {RegisterComponent} from './register';
import {ResetComponent} from './resetPassword';
import {UpdatePasswordComponent} from './updatePassword';

import {AlertComponent, FooterComponent} from './components';

import {SearchComponent, SearchResultComponent} from './search';
import {NotificationComponent, NotificationDetailsComponent} from './notification';
import {ConfigurationDetailsComponent} from './configuration';
import {
	UserComponent, UserDetailsComponent, UserOverviewComponent, UserGroupComponent, UserGroupDialogComponent,
	UserGroupApplyConfigComponent, UserProbeChangeGroupComponent, UserContextAddDialogComponent
} from './user';
import {
	OsqueryConfigurationDetailsComponent, OsqueryConfigurationEditComponent
} from './osquery';
import {
	NetworkConfigurationProcessorDetailsComponent, NetworkConfigurationStepDetailsComponent,
	NetworkStepConfigurationEditComponent, NetworkProcessorConfigurationEditComponent
}
	from './network';
import {
	ReportComponent, NetworkReportOverviewComponent, NetworkReportDetailsComponent, OsQueryReportOverviewComponent,
	OsQueryReportDetailsComponent, ReportOverviewComponent
} from './report';
import {
	EmailOverviewComponent,
	EmailInboxComponent,
	EmailAccountAddComponent,
	EmailRuleOverviewComponent,
	EmailComponent
} from './email';
import {RuleComponent, RuleOverviewComponent, RuleAddComponent} from './rule';
import {SettingsComponent} from './settings';
import {AnalysisComponent, AddQueryDialog} from './analysis';
import {
	OrbiterOverviewComponent,
	OrbiterComponent,
	OrbiterToolTestComponent,
	OrbiterToolTestListComponent,
	OrbiterToolTestScheduledListComponent,
	OrbiterToolTestSequenceListComponent,
	TestDetailsComponent,
	TestResultDetailsComponent,
	TestSequenceDetailsComponent
} from './orbiter';

import {ConfirmationDialog} from './dialog/confirmation-dialog';


/**
 * Internal common components
 */
import {EqualValidator} from './_directives';
import {AuthGuard} from './_guards';
import {TruncatePipe} from './_helpers';
import {AlertService, AuthenticationService, DataService, HelperService, HttpService} from './_services';
import {DashboardLayoutComponent} from './_layouts/dashboardLayout';
import {LoginLayoutComponent} from './_layouts/loginLayout';
import {NavbarComponent} from './navbar';

/**
 * Internal framework components
 */
import {routing} from './app.routing';
import {AppComponent} from './app.component';

/**
 * Third party components
 */

import {DatePipe} from '@angular/common';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DataTableModule} from 'angular2-datatable';
import {Angular2FontawesomeModule} from 'angular2-fontawesome/angular2-fontawesome';
import {TabsModule} from 'ng2-tabs';
import {TranslateModule, TranslateLoader} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {NgMatSearchBarModule} from 'ng-mat-search-bar';
import {FileUploadModule} from 'ng2-file-upload';
import {ModalModule} from 'ngx-modialog';
import {BootstrapModalModule} from 'ngx-modialog/plugins/bootstrap';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {Nl2BrPipeModule} from 'nl2br-pipe';
import {
	MatButtonModule,
	MatCheckboxModule,
	MatInputModule,
	MatFormFieldModule,
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
	MatNativeDateModule,
	MatDatepickerModule,
	MatCardModule,
	MatExpansionModule,
	MatBadgeModule,
	MatListModule,
	MatRadioModule,
	MatTooltipModule,
	MatSnackBarModule,
} from '@angular/material';
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
import { Ng4LoadingSpinnerModule } from 'ng4-loading-spinner';
import { Ng2GoogleChartsModule } from 'ng2-google-charts';
import { AgmCoreModule } from '@agm/core';
import { NgxJsonViewModule } from 'ng-json-view';
import { AgmDirectionModule } from 'agm-direction';
import {TestResultComponent} from './report/testResult.component';
import {DragDropModule} from '@angular/cdk/drag-drop';
import { AceEditorModule } from 'ng2-ace-editor';



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
		MatListModule,
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
		AceEditorModule,
		MatRadioModule,
		MatTooltipModule,
		MatSnackBarModule,
		NgMatSearchBarModule,
		Ng4LoadingSpinnerModule.forRoot(),
		DragDropModule

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
		EmailRuleOverviewComponent,
		EmailComponent,
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
		OrbiterToolTestScheduledListComponent,
		SearchComponent,
		SearchResultComponent,
		TruncatePipe,
		NotificationDetailsComponent,
		OrbiterToolTestSequenceListComponent,
		TestSequenceDetailsComponent,
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
		RuleAddComponent,
		OsQueryReportDetailsComponent,
		NetworkReportDetailsComponent,
		UserDetailsComponent,
		AddQueryDialog,
		TestResultDetailsComponent,
		TestDetailsComponent,
		NotificationDetailsComponent,
		TestSequenceDetailsComponent,
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
