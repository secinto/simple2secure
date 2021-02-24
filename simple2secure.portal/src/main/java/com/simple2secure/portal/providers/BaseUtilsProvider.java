package com.simple2secure.portal.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.portal.repository.EmailConfigurationRepository;
import com.simple2secure.portal.repository.FactsToCheckRepository;
import com.simple2secure.portal.repository.RuleDeviceMappingRepository;
import com.simple2secure.portal.repository.RuleEmailConfigMappingRepository;
import com.simple2secure.portal.repository.RuleRegexRepository;
import com.simple2secure.portal.repository.TemplateRuleFactTypeMappingRepository;
import com.simple2secure.portal.repository.TemplateRuleRepository;
import com.simple2secure.portal.utils.ContextUtils;
import com.simple2secure.portal.utils.InputDataUtils;
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
import com.simple2secure.portal.utils.TestSequenceUtils;
import com.simple2secure.portal.utils.TestUtils;
import com.simple2secure.portal.utils.UserUtils;
import com.simple2secure.portal.utils.WidgetUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseUtilsProvider extends BaseServiceProvider {

	/*
	 * Utils
	 */
	@Autowired
	public DeviceUtils deviceUtils;

	@Autowired
	public TestUtils testUtils;

	@Autowired
	public TestSequenceUtils testSequenceUtils;

	@Autowired
	public SUTUtils sutUtils;

	@Autowired
	public PortalUtils portalUtils;

	@Autowired
	public UserUtils userUtils;

	@Autowired
	public ContextUtils contextUtils;

	@Autowired
	public MailUtils mailUtils;

	@Autowired
	public GroupUtils groupUtils;

	@Autowired
	public LicenseUtils licenseUtils;

	@Autowired
	public NotificationUtils notificationUtils;

	@Autowired
	public QueryUtils queryUtils;

	@Autowired
	public ReportUtils reportUtils;

	@Autowired
	public RuleUtils ruleUtils;

	@Autowired
	public SearchUtils searchUtils;

	@Autowired
	public WidgetUtils widgetUtils;

	@Autowired
	public InputDataUtils inputDataUtils;

	@Autowired
	public EmailConfigurationRepository emailConfigurationRepository;

	@Autowired
	public TemplateRuleRepository templateRuleRepository;

	@Autowired
	public RuleRegexRepository ruleRegexRepository;

	@Autowired
	public TemplateRuleFactTypeMappingRepository templateRuleFactTypeMappingRepository;

	@Autowired
	public FactsToCheckRepository factsToCheckRepository;

	@Autowired
	public RuleEmailConfigMappingRepository ruleEmailConfigMappingRepository;

	@Autowired
	public RuleDeviceMappingRepository ruleDeviceMappingRepository;
}
