package com.simple2secure.portal.providers;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.simple2secure.portal.repository.RuleUserPairsRepository;
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

public class BaseRepositoryProvider {
	/*
	 * Repositories
	 */
	@Autowired
	public UserRepository userRepository;

	@Autowired
	public GroupRepository groupRepository;

	@Autowired
	public ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	public LicensePlanRepository licensePlanRepository;

	@Autowired
	public CurrentContextRepository currentContextRepository;

	@Autowired
	public ContextRepository contextRepository;

	@Autowired
	public LicenseRepository licenseRepository;

	@Autowired
	public DeviceInfoRepository deviceInfoRepository;

	@Autowired
	public TestRepository testRepository;

	@Autowired
	public EmailConfigurationRepository emailConfigRepository;

	@Autowired
	public EmailRepository emailRepository;

	@Autowired
	public GroupAccesRightRepository groupAccessRightRepository;

	@Autowired
	public TokenRepository tokenRepository;

	@Autowired
	public StepRepository stepRepository;

	@Autowired
	public SettingsRepository settingsRepository;

	@Autowired
	public ContextUserAuthRepository contextUserRepository;

	@Autowired
	public NotificationRepository notificationRepository;

	@Autowired
	public ProcessorRepository processorRepository;

	@Autowired
	public OsQueryRepository queryRepository;

	@Autowired
	public OsQueryCategoryRepository queryCategoryRepository;

	@Autowired
	public OsQueryGroupMappingRepository queryGroupMappingRepository;

	@Autowired
	public OsQueryReportRepository reportsRepository;

	@Autowired
	public NetworkReportRepository networkReportRepository;

	@Autowired
	public RuleWithSourcecodeRepository ruleWithSourcecodeRepository;

	@Autowired
	public RuleConditionsRepository ruleConditionsRepository;

	@Autowired
	public RuleActionsRepository ruleActionsRepository;

	@Autowired
	public TemplateRuleRepository templateRuleRepository;
	
	@Autowired
	public RuleUserPairsRepository ruleUserPairsRepository;

	@Autowired
	public ServiceLibraryRepository serviceLibraryRepository;

	@Autowired
	public TestMacroRepository testMacroRepository;

	@Autowired
	public WidgetRepository widgetRepository;

	@Autowired
	public SystemUnderTestRepository sutRepository;

	@Autowired
	public TestResultRepository testResultRepository;

	@Autowired
	public TestRunRepository testRunRepository;

	@Autowired
	public TestSequenceRepository testSequenceRepository;

	@Autowired
	public SequenceRunRepository sequenceRunrepository;

	@Autowired
	public TestSequenceResultRepository testSequenceResultRepository;

	@Autowired
	public UserInvitationRepository userInvitationRepository;

	@Autowired
	public UserInfoRepository userInfoRepository;

	@Autowired
	public WidgetPropertiesRepository widgetPropertiesRepository;

	@Autowired
	public SequenceRunRepository sequenceRunRepository;

}
