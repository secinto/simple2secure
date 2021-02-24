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
import { environment } from '../environments/environment';
import { NetworkReportOverviewComponent } from './components/report/networkReportOverview.component';
import { NotificationDetailsComponent } from './components/notification/notificationDetails.component';
import { TestSequenceResultComponent } from './components/report/testSequenceResult.component';
import { StatComponent } from './components/widgets/stat.component';
import { HomeComponent } from './components/home/home.component';
import { AddQueryDialog } from './components/analysis/addQueryDialog';
import { QueriesComponent } from './components/queries/queries.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SelectContextDialog } from './components/dialog/select-context';
import { OrbiterToolTestSequenceListComponent } from './components/orbiter/orbiterToolTestSequenceList.component';
import { TestDetailsComponent } from './components/orbiter/testDetails.component';
import { TrafficLightItemComponent } from './components/widgets/traffic-light-item.component';
import { ConfirmationDialog } from './components/dialog/confirmation-dialog';
import { NotificationCardItem } from './components/widgets/notification-card-item.component';
import { UserOverviewComponent } from './components/user/userOverview.component';
import { UserGroupDialogComponent } from './components/user/userGroupDialog.component';
import { MappedQueryEditDialog } from './components/queries/mappedQueryEditDialog.component';
import { PieChartItemComponent } from './components/widgets/pie-chart-item.component';
import { NotificationCardComponent } from './components/widgets/notification-card.component';
import { DevicesOverviewComponent } from './components/devices/devicesOverview.component';
import { NetworkConfigurationStepDetailsComponent } from './components/network/networkConfigurationStepDetails.component';
import { BarChartItemComponent } from './components/widgets/bar-chart-item.component';
import { NotificationComponent } from './components/notification/notification.component';
import { LineChartItemComponent } from './components/widgets/line-chart-item.component';
import { UserDeviceChangeGroupComponent } from './components/user/user-device-change-group.component';
import { SUTDetailsComponent } from './components/orbiter/sutDetails.component';
import { LineChartComponent } from './components/widgets/line-chart.component';
import { NetworkStepConfigurationEditComponent } from './components/network/networkStepConfigurationEdit.component';
import { QueryListComponent } from './components/queries/queryList.component';
import { NetworkConfigurationProcessorDetailsComponent } from './components/network/networkConfigurationProcessorDetails.component';
import { QueryEditDialogComponent } from './components/queries/queryEditDialog.component';
import { UserDetailsComponent } from './components/user/userDetails.component';
import { OsQueryReportDetailsComponent } from './components/report/osqueryReportDetails.component';
import { OrbiterComponent } from './components/orbiter/orbiter.component';
import { DownloadWidgetComponent } from './components/widgets/download-widget.component';
import { TestSequenceResultDetailsComponent } from './components/report/testSequenceResultDetails.component';
import { TestResultComponent } from './components/report/testResult.component';
import { SearchComponent } from './components/search/search.component';
import { NotificationDialog } from './components/dialog/notification-dialog';
import { StatItemComponent } from './components/widgets/stat-item.component';
import { QueryOverviewComponent } from './components/queries/queryOverview.component';
import { QueryCategoryAddDialog } from './components/queries/queryCategoryAddDialog.component';
import { DevicesListComponent } from './components/devices/devicesList.component';
import { OsQueryReportOverviewComponent } from './components/report/osqueryReportOverview.component';
import { BaseComponent } from './components/widgets/base.component';
import { ReportOverviewComponent } from './components/report/reportOverview.component';
import { OrbiterSystemsUnderTestListComponent } from './components/orbiter/orbiterSystemsUnderTestList.component';
import { OrbiterScheduledSequencesListComponent } from './components/orbiter/orbiterScheduledSequencesList.component';
import { TrafficLightComponent } from './components/widgets/traffic-light.component';
import { LoginComponent } from './components/authentication/login/login.component';
import { EmailComponent } from './components/email/email.component';
import { RuleOverviewComponent } from './components/rule/ruleOverview.component';
import { ReportComponent } from './components/report/report.component';
import { OrbiterOverviewComponent } from './components/orbiter/orbiterOverview.component';
import { EmailRuleOverviewComponent } from './components/email/emailRuleOverview.component';
import { DownloadWidgetItemComponent } from './components/widgets/download-item.component';
import { UserGroupEditComponent } from './components/user/userGroupEdit.component';
import { UserContextAddDialogComponent } from './components/user/userContextAddDialog.component';
import { SettingsComponent } from './components/settings/settings.component';
import { QueryAssignComponent } from './components/queries/queryAssign.component';
import { NetworkReportDetailsComponent } from './components/report/networkReportDetails.component';
import { EmailInboxComponent } from './components/email/emailInbox.component';
import { OrbiterToolTestScheduledListComponent } from './components/orbiter/orbiterToolTestScheduledList.component';
import { RuleComponent } from './components/rule/rule.component';
import { EmailOverviewComponent } from './components/email/emailOverview.component';
import { EmailAccountAddComponent } from './components/email/emailAccountAdd.component';
import { OrbiterToolTestListComponent } from './components/orbiter/orbiterToolTestList.component';
import { UserGroupApplyConfigComponent } from './components/user/userGroupApplyConfig.component';
import { BarChartComponent } from './components/widgets/bar-chart.component';
import { NetworkProcessorConfigurationEditComponent } from './components/network/networkProcessorConfigurationEdit.component';
import { AnalysisComponent } from './components/analysis/analysis.component';
import { UserComponent } from './components/user/user.component';
import { TestSequenceDetailsComponent } from './components/orbiter/testSequenceDetails.component';
import { WidgetStoreComponent } from './components/widgets/widgetStore.component';
import { TestResultDetailsComponent } from './components/report/testResultDetails.component';
import { OrbiterToolTestComponent } from './components/orbiter/orbiterToolTest.component';
import { DevicesComponent } from './components/devices/devices.component';
import { SidenavbarComponent } from './components/navbar/sidenavbar.component';
import { SearchResultComponent } from './components/search/searchResult.component';
import { PieChartComponent } from './components/widgets/pie-chart.component';
import { RuleAddComponent } from './components/rule/ruleAdd.component';
import { AlertComponent } from './components/alert/alert.component';
import { RuleAddRegexComponent } from './components/rule/ruleAddRegex.component';
import { RuleMappingComponent } from './components/rule/ruleMapping.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
/**
 * Internal common components
 */


/**
 * Internal framework components
 */
import { routing } from './app.routing';
import { AppComponent } from './app.component';
import { DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DataTableModule } from 'angular2-datatable';
import { Angular2FontawesomeModule } from 'angular2-fontawesome/angular2-fontawesome';
import { TabsModule } from 'ng2-tabs';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { NgMatSearchBarModule } from 'ng-mat-search-bar';
import { FileUploadModule } from 'ng2-file-upload';
import { ModalModule } from 'ngx-modialog';
import { BootstrapModalModule } from 'ngx-modialog/plugins/bootstrap';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Nl2BrPipeModule } from 'nl2br-pipe';
import {
    MatBadgeModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDialogModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatNativeDateModule,
    MatOptionModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatSelectModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatTabsModule,
    MatTooltipModule,
} from '@angular/material';
import { ChartModule, HIGHCHARTS_MODULES } from 'angular-highcharts';
import { TreeModule } from 'angular-tree-component';
import { TreeTableModule } from 'ng-treetable';
import { AuthInterceptor } from './_helpers/auth.interceptor';
import { TreeviewModule } from 'ngx-treeview';
import * as highstock from 'highcharts/modules/stock.src';
import * as exporting from 'highcharts/modules/exporting.src';
import { NgxSpinnerModule } from 'ngx-spinner';
import { Ng4LoadingSpinnerModule } from 'ng4-loading-spinner';
import { Ng2GoogleChartsModule } from 'ng2-google-charts';
import { AgmCoreModule } from '@agm/core';
import { NgxJsonViewModule } from 'ng-json-view';
import { AgmDirectionModule } from 'agm-direction';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { AceEditorModule } from 'ng2-ace-editor';
import { NgxWidgetGridModule } from 'ngx-widget-grid';
import { BoxModule } from 'angular-admin-lte';
import { BreadcrumbsModule } from 'ng6-breadcrumbs';
import { NavbarLoginComponent } from './components/navbar/navbarlogin.component';
import { NgxGraphModule } from '@swimlane/ngx-graph';
import { ChartistModule } from 'ng-chartist';
import { FooterComponent } from './components/footer/footer.component';
import { RoleGuard } from './_guards/role.guard';
import { AuthGuard } from './_guards/auth.guard';
import { TruncatePipe } from './_helpers/truncate.pipe';
import { HttpService } from './_services/http.service';
import { AlertService } from './_services/alert.service';
import { HelperService } from './_services/helper.service';
import { DashboardLayoutComponent } from './_layouts/dashboardLayout/dashboardlayout.component';
import { LoginLayoutComponent } from './_layouts/loginLayout/loginlayout.component';
import { AuthenticationService } from './_services/authentication.service';
import { DataService } from './_services/data.service';
import { InviteUserDialogComponent } from './components/user/inviteUserDialog.component';
import { InvitationContext } from './components/dialog/invitation-context';
import { InvitationContextAccept } from './components/dialog/invitation-context-accept';
import { RegisterComponent } from './components/authentication/register/register.component';
import { ForgotPasswordComponent } from './components/authentication/forgotPassword/forgotPassword.component';
import { RuleRegexListComponent } from './components/rule/ruleRegexList.component';
import { RuleListComponent } from './components/rule/ruleList.component';
import { MatStepperModule } from '@angular/material/stepper';
import { SutInputDataDialogComponent } from './components/orbiter/sutInputDataDialog.component';
import { AddInputDataDialogComponent } from './components/orbiter/addInputDataDialog.component';
import { TestContentDialogComponent } from './components/orbiter/testContentDialog.component';
import { InputDataDialogComponent } from './components/orbiter/inputDataDialog.component';
import { NgxFileDropModule } from 'ngx-file-drop';
import { ImportSutDialogComponent } from './components/orbiter/importSutDialog.component';

/**
 * Third party components
 */

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
        DragDropModule,
        NgxWidgetGridModule,
        BoxModule,
        BreadcrumbsModule,
        NgxGraphModule,
        ChartistModule,
        MatStepperModule,
        MatSlideToggleModule,
        NgxFileDropModule
    ],
    declarations: [
        AppComponent,
        AlertComponent,
        HomeComponent,
        LoginComponent,
        DashboardLayoutComponent,
        LoginLayoutComponent,
        NavbarComponent,
        NavbarLoginComponent,
        SidenavbarComponent,
        NetworkReportOverviewComponent,
        NetworkConfigurationStepDetailsComponent,
        NetworkConfigurationProcessorDetailsComponent,
        NetworkStepConfigurationEditComponent,
        NetworkProcessorConfigurationEditComponent,
        NetworkReportDetailsComponent,
        UserComponent,
        RegisterComponent,
        ForgotPasswordComponent,
        UserDetailsComponent,
        UserOverviewComponent,
        UserGroupDialogComponent,
        UserGroupApplyConfigComponent,
        UserDeviceChangeGroupComponent,
        UserContextAddDialogComponent,
        InviteUserDialogComponent,
        OsQueryReportOverviewComponent,
        OsQueryReportDetailsComponent,
        QueryEditDialogComponent,
        FooterComponent,
        BaseComponent,
        OrbiterComponent,
        OrbiterOverviewComponent,
        TestResultComponent,
        ConfirmationDialog,
        SelectContextDialog,
        InvitationContext,
        InvitationContextAccept,
        NotificationDialog,
        EmailOverviewComponent,
        EmailInboxComponent,
        EmailAccountAddComponent,
        EmailRuleOverviewComponent,
        EmailComponent,
        SettingsComponent,
        NotificationComponent,
        RuleComponent,
        RuleOverviewComponent,
        RuleAddComponent,
        RuleMappingComponent,
        AnalysisComponent,
        AddQueryDialog,
        ReportComponent,
        ReportOverviewComponent,
        TestResultDetailsComponent,
        TestDetailsComponent,
        OrbiterToolTestComponent,
        OrbiterToolTestListComponent,
        OrbiterToolTestScheduledListComponent,
        OrbiterScheduledSequencesListComponent,
        OrbiterToolTestSequenceListComponent,
        SutInputDataDialogComponent,
        AddInputDataDialogComponent,
        SearchComponent,
        SearchResultComponent,
        TruncatePipe,
        NotificationDetailsComponent,
        OrbiterToolTestSequenceListComponent,
        TestSequenceDetailsComponent,
        TestSequenceResultComponent,
        TestSequenceResultDetailsComponent,
        OrbiterSystemsUnderTestListComponent,
        SUTDetailsComponent,
        StatComponent,
        StatItemComponent,
        NotificationCardComponent,
        NotificationCardItem,
        WidgetStoreComponent,
        DownloadWidgetComponent,
        DownloadWidgetItemComponent,
        DevicesComponent,
        DevicesOverviewComponent,
        DevicesListComponent,
        UserGroupEditComponent,
        QueriesComponent,
        QueryOverviewComponent,
        QueryListComponent,
        QueryAssignComponent,
        MappedQueryEditDialog,
        QueryCategoryAddDialog,
        TrafficLightComponent,
        TrafficLightItemComponent,
        BarChartComponent,
        BarChartItemComponent,
        LineChartComponent,
        LineChartItemComponent,
        PieChartComponent,
        PieChartItemComponent,
        RuleAddRegexComponent,
        RuleRegexListComponent,
        RuleListComponent,
        TestContentDialogComponent,
        InputDataDialogComponent,
        ImportSutDialogComponent
    ],
    entryComponents: [
        ConfirmationDialog,
        SelectContextDialog,
        InvitationContext,
        InvitationContextAccept,
        NotificationDialog,
        UserGroupDialogComponent,
        NetworkProcessorConfigurationEditComponent,
        NetworkStepConfigurationEditComponent,
        QueryEditDialogComponent,
        UserGroupApplyConfigComponent,
        UserDeviceChangeGroupComponent,
        UserContextAddDialogComponent,
        RegisterComponent,
        ForgotPasswordComponent,
        InviteUserDialogComponent,
        EmailAccountAddComponent,
        EmailInboxComponent,
        RuleAddComponent,
        RuleMappingComponent,
        OsQueryReportDetailsComponent,
        NetworkReportDetailsComponent,
        UserDetailsComponent,
        AddQueryDialog,
        TestResultDetailsComponent,
        TestDetailsComponent,
        NotificationDetailsComponent,
        TestSequenceDetailsComponent,
        TestSequenceResultDetailsComponent,
        WidgetStoreComponent,
        StatItemComponent,
        StatComponent,
        NotificationCardComponent,
        NotificationCardItem,
        SUTDetailsComponent,
        SutInputDataDialogComponent,
        UserGroupEditComponent,
        MappedQueryEditDialog,
        QueryCategoryAddDialog,
        DownloadWidgetComponent,
        DownloadWidgetItemComponent,
        TrafficLightComponent,
        TrafficLightItemComponent,
        BarChartComponent,
        BarChartItemComponent,
        LineChartComponent,
        LineChartItemComponent,
        PieChartComponent,
        PieChartItemComponent,
        AppComponent,
        RuleAddRegexComponent,
        AddInputDataDialogComponent,
        TestContentDialogComponent,
        InputDataDialogComponent,
        ImportSutDialogComponent
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
        { provide: HIGHCHARTS_MODULES, useFactory: () => [highstock, exporting] },
        DatePipe,
    ],

    bootstrap: [AppComponent]
})

export class AppModule {
}

// required for AOT compilation
export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}
