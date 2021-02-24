// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

const API_ENDPOINT = 'https://localhost:8443/api/v1/';
export const environment = {
    production: false,
    envName: 'dev',
    apiEndpoint: API_ENDPOINT,
    // Context API
    apiContext: API_ENDPOINT + 'context',
    apiContextAdd: API_ENDPOINT + 'context/add',
    apiContextDelete: API_ENDPOINT + 'context/delete',
    // Devices API
    apiDevicesChangeGroup: API_ENDPOINT + 'devices/changeGroup/{deviceId}',
    apiDevicesWithTestsByTypePagination: API_ENDPOINT + 'devices/test/{deviceType}',
    apiDevicesByGroupIdAndTypePagination: API_ENDPOINT + 'devices/group/{groupId}',
    apiDevicesByTypePagination: API_ENDPOINT + 'devices/{deviceType}',
    apiDevicesByGroup: API_ENDPOINT + 'devices/group',
	apiDevicesUpdateVisiblity: API_ENDPOINT + 'devices/visibility',
    // Download API
    apiDownloadGroupId: API_ENDPOINT + 'download/{groupId}',
    apiDownloadServices: API_ENDPOINT + 'download/services/{deviceId}',
    apiDownloadSuts: API_ENDPOINT + 'download/suts',
    // Email API
    apiEmail: API_ENDPOINT + 'email',
    apiEmailConfigId: API_ENDPOINT + 'email/{emailConfigId}',
    // Group API
    apiGroup: API_ENDPOINT + 'group',
    apiGroupByContext: API_ENDPOINT + 'group/context',
    apiGroupGroupId: API_ENDPOINT + 'group/{groupId}',
    apiGroupCopy: API_ENDPOINT + 'group/copy/{groupId}',
    apiGroupMove: API_ENDPOINT + 'group/move/{groupId}/{destGroupId}',
    // Input Data API
    apiInputData: API_ENDPOINT + 'inputData',
    apiInputDataDelete: API_ENDPOINT + 'inputData/delete/{inputDataId}',
    // License API
    apiLicenseGroupId: API_ENDPOINT + 'license/{groupId}',
    // Notification API
    apiNotificationRead: API_ENDPOINT + 'notification/read',
    apiNotification: API_ENDPOINT + 'notification',
    // Processors API
    apiProcessors: API_ENDPOINT + 'processors',
    apiProcessorsById: API_ENDPOINT + 'processors/{processorId}',
    // Query API
    apiQueries: API_ENDPOINT + 'query',
    apiQueriesAllDto: API_ENDPOINT + 'query/allDto',
    apiQueriesById: API_ENDPOINT + 'query/{queryId}',
    apiQueriesByProbe: API_ENDPOINT + 'query/{probeId}/{osinfo}',
    apiQueriesUnmappedByGroup: API_ENDPOINT + 'query/unmapped/{groupId}',
    apiQueriesCategory: API_ENDPOINT + 'query/category',
    apiQueriesGroup: API_ENDPOINT + 'query/group/{groupId}',
    apiQueriesMapGroup: API_ENDPOINT + 'query/mapping/{groupId}',
    // Reports API
    apiReportsByDeviceIdName: API_ENDPOINT + 'reports/device/{deviceId}/{name}',
    apiReportsGroupsPagination: API_ENDPOINT + 'reports/groups',
    apiReportsDevicesPagination: API_ENDPOINT + 'reports/devices',
    apiNetworkReportsDevicesPagination: API_ENDPOINT + 'reports/network/devices',
    apiNetworkReportsGroupsPagination: API_ENDPOINT + 'reports/network/groups',
    apiReportsDeleteSelected: API_ENDPOINT + 'reports/delete/selected',
    apiNetworkReportsDeleteSelected: API_ENDPOINT + 'reports/network/delete/selected',
    // Rule API
    apiRuleRegexCheckName: API_ENDPOINT + 'rule/regex/check_name_used',
    apiRuleRegexTest: API_ENDPOINT + 'rule/regex/test',
    apiRuleRegex: API_ENDPOINT + 'rule/regex',
    apiRuleRegexById: API_ENDPOINT + 'rule/regex/{ruleRegexId}',
    apiRuleMapping: API_ENDPOINT + 'rule/mapping',
    apiRuleMappingFactType: API_ENDPOINT + 'rule/mapping/{ruleFactType}/{ruleId}',
    apiRuleRules: API_ENDPOINT + 'rule/rules',
    apiRuleRulesFactTypePagination: API_ENDPOINT + 'rule/templaterules',
    apiRuleCheckCondExpr: API_ENDPOINT + 'rule/check_condition_expression',
    apiRuleCheckNameUsed: API_ENDPOINT + 'rule/check_name_used',
    apiRuleTemplateFactType: API_ENDPOINT + 'rule/templaterule/{ruleFactType}',
    apiRuleTemplateId: API_ENDPOINT + 'rule/templaterule/{ruleId}',
    apiRuleTemplateCondActionFactType: API_ENDPOINT + 'rule/template_conditions_actions/{ruleFactType}',
    // Search API
    apiSearchString: API_ENDPOINT + 'search/{searchQuery}',
    // Settings API
    apiSettings: API_ENDPOINT + 'settings',
    apiSettingsLicensePlan: API_ENDPOINT + 'settings/licensePlan',
    apiSettingsLicensePlanById: API_ENDPOINT + 'settings/licensePlan/{licensePlanId}',
    apiSettingsTestMacro: API_ENDPOINT + 'settings/testmacro',
    apiSettingsTestMacroById: API_ENDPOINT + 'settings/testmacro/{testMacroId}',
    // Steps API
    apiSteps: API_ENDPOINT + 'steps',
    apiStepsByStepId: API_ENDPOINT + 'steps/{stepId}',
    // SUT API
    apiSutAdd: API_ENDPOINT + 'sut/addSut',
    apiSutUpdate: API_ENDPOINT + 'sut/updateSut',
    apiSutPagination: API_ENDPOINT + 'sut',
    apiSutById: API_ENDPOINT + 'sut/{sutId}',
    apiSutImport: API_ENDPOINT + 'sut/importSut',
    // Test API
    apiTestSave: API_ENDPOINT + 'test/save',
    apiTestClone: API_ENDPOINT + 'test/clone/{testId}',
    apiTestByDeviceIdPagination: API_ENDPOINT + 'test/{deviceId}',
    apiTestScheduleSut: API_ENDPOINT + 'test/scheduleSutTest',
    apiTestScheduleTest: API_ENDPOINT + 'test/scheduleTest',
    apiTestApplicableSutList: API_ENDPOINT + 'test/applicableSutList',
    apiTestDeleteById: API_ENDPOINT + 'test/delete/{testId}',
    apiTestScheduledTestsPagination: API_ENDPOINT + 'test/getScheduledTests',
    apiTestTestRunDelete: API_ENDPOINT + 'test/testrun/delete/{testRunId}',
    apiTestResultGroups: API_ENDPOINT + 'test/result/groups',
    apiTestResultDevices: API_ENDPOINT + 'test/result/devices',
    apiTestResultDeleteSelected: API_ENDPOINT + 'test/result/delete/selected',
    // TestSequence API
    apiSequence: API_ENDPOINT + 'sequence',
    apiSequenceClone: API_ENDPOINT + 'sequence/clone/{sequenceId}',
    apiSequenceByDeviceIdPagination: API_ENDPOINT + 'sequence/{deviceId}',
    apiSequenceSchedule: API_ENDPOINT + 'sequence/scheduleSequence',
    apiSequenceBySequenceId: API_ENDPOINT + 'sequence/{sequenceId}',
    apiSequenceScheduledPagination: API_ENDPOINT + 'sequence/scheduledSequence',
    apiSequenceResultGroup: API_ENDPOINT + 'sequence/result/groups',
    apiSequenceResultDevices: API_ENDPOINT + 'sequence/result/devices',
    apiSequenceResultDeleteSelected: API_ENDPOINT + 'sequence/result/delete/selected',
    // User API
    apiUser: API_ENDPOINT + 'user',
    apiUserRegister: API_ENDPOINT + 'user/register',
    apiUserForgotPassword: API_ENDPOINT + 'user/forgotPassword',
    apiUserResendActivation: API_ENDPOINT + 'user/resendActivation',
    apiUserLogout: API_ENDPOINT + 'user/logout',
    apiUserLogin: API_ENDPOINT + 'user/login',
    apiUserInvite: API_ENDPOINT + 'user/inviteContext',
    apiUserIsDeviceAdmin: API_ENDPOINT + 'user/isAdminForDevice/{deviceId}',
    apiUserInvitations: API_ENDPOINT + 'user/invitations',
    apiUserAcceptInvitation: API_ENDPOINT + 'user/acceptInvitation',
    // Widget API
    apiWidgetDeleteProp: API_ENDPOINT + 'widget/delete/prop/{widgetPropId}',
    apiWidget: API_ENDPOINT + 'widget',
    apiWidgetDeleteById: API_ENDPOINT + 'widget/delete/{widgetId}',
    apiWidgetAdd: API_ENDPOINT + 'widget/add',
    apiWidgetByLocation: API_ENDPOINT + 'widget/get/{widgetLocation}',
    apiWidgetUpdatePosition: API_ENDPOINT + 'widget/updatePosition',
};
