package com.simple2secure.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.CurrentContextRepository;
import com.simple2secure.portal.repository.DeviceInfoRepository;
import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.repository.GroupAccesRightRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicensePlanRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.repository.OsQueryCategoryRepository;
import com.simple2secure.portal.repository.OsQueryGroupMappingRepository;
import com.simple2secure.portal.repository.OsQueryReportRepository;
import com.simple2secure.portal.repository.OsQueryRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.RuleActionsRepository;
import com.simple2secure.portal.repository.RuleConditionsRepository;
import com.simple2secure.portal.repository.RuleWithSourcecodeRepository;
import com.simple2secure.portal.repository.SequenceRunRepository;
import com.simple2secure.portal.repository.ServiceLibraryRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.repository.SystemUnderTestRepository;
import com.simple2secure.portal.repository.TemplateRuleRepository;
import com.simple2secure.portal.repository.TestMacroRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestRunRepository;
import com.simple2secure.portal.repository.TestSequenceRepository;
import com.simple2secure.portal.repository.TestSequenceResultRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.UserInfoRepository;
import com.simple2secure.portal.repository.UserInvitationRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.repository.WidgetPropertiesRepository;
import com.simple2secure.portal.repository.WidgetRepository;
import com.simple2secure.portal.security.PasswordValidator;
import com.simple2secure.portal.security.auth.TokenAuthenticationService;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.ContextUtils;
import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.DeviceUtils;
import com.simple2secure.portal.utils.GroupUtils;
import com.simple2secure.portal.utils.LicenseUtils;
import com.simple2secure.portal.utils.MailUtils;
import com.simple2secure.portal.utils.NotificationUtils;
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.QueryUtils;
import com.simple2secure.portal.utils.ReportUtils;
import com.simple2secure.portal.utils.RuleUtils;
import com.simple2secure.portal.utils.SUTUtils;
import com.simple2secure.portal.utils.SearchUtils;
import com.simple2secure.portal.utils.TestUtils;
import com.simple2secure.portal.utils.UserUtils;
import com.simple2secure.portal.utils.WidgetUtils;

public class BaseController {
	/*
	 * Repositories
	 */
	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	LicensePlanRepository licensePlanRepository;

	@Autowired
	CurrentContextRepository currentContextRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	DeviceInfoRepository deviceInfoRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	EmailConfigurationRepository emailConfigRepository;

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	GroupAccesRightRepository groupAccessRightRepository;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	SettingsRepository settingsRepository;

	@Autowired
	ContextUserAuthRepository contextUserRepository;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	ProcessorRepository processorRepository;

	@Autowired
	OsQueryRepository queryRepository;

	@Autowired
	OsQueryCategoryRepository queryCategoryRepository;

	@Autowired
	OsQueryGroupMappingRepository queryGroupMappingRepository;

	@Autowired
	OsQueryReportRepository reportsRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	RuleWithSourcecodeRepository ruleWithSourcecodeRepository;

	@Autowired
	RuleConditionsRepository ruleConditionsRepository;

	@Autowired
	RuleActionsRepository ruleActionsRepository;

	@Autowired
	TemplateRuleRepository templateRuleRepository;

	@Autowired
	ServiceLibraryRepository serviceLibraryRepository;

	@Autowired
	TestMacroRepository testMacroRepository;

	@Autowired
	WidgetRepository widgetRepository;

	@Autowired
	SystemUnderTestRepository sutRepository;

	@Autowired
	TestResultRepository testResultRepository;

	@Autowired
	TestRunRepository testRunRepository;

	@Autowired
	TestSequenceRepository testSequenceRepository;

	@Autowired
	SequenceRunRepository sequenceRunrepository;

	@Autowired
	TestSequenceResultRepository testSequenceResultRepository;

	@Autowired
	UserInvitationRepository userInvitationRepository;

	@Autowired
	UserInfoRepository userInfoRepository;

	@Autowired
	WidgetPropertiesRepository widgetPropertiesRepository;

	/*
	 * Special services
	 */

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	DataInitialization dataInitialization;

	@Autowired
	ErrorAttributes errorAttributes;

	@Autowired
	TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	PasswordValidator passwordValidator;

	/*
	 * Utils
	 */
	@Autowired
	DeviceUtils deviceUtils;

	@Autowired
	TestUtils testUtils;

	@Autowired
	SUTUtils sutUtils;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	UserUtils userUtils;

	@Autowired
	ContextUtils contextUtils;

	@Autowired
	MailUtils mailUtils;

	@Autowired
	GroupUtils groupUtils;

	@Autowired
	LicenseUtils licenseUtils;

	@Autowired
	NotificationUtils notificationUtils;

	@Autowired
	QueryUtils queryUtils;

	@Autowired
	ReportUtils reportUtils;

	@Autowired
	RuleUtils ruleUtils;

	@Autowired
	SearchUtils searchUtils;

	@Autowired
	WidgetUtils widgetUtils;

}
