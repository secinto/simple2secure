package com.simple2secure.portal.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.portal.utils.ContextUtils;
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

public class BaseUtilsProvider extends BaseServiceProvider {

	static final Logger log = LoggerFactory.getLogger(BaseUtilsProvider.class);

	/*
	 * Utils
	 */
	@Autowired
	public DeviceUtils deviceUtils;

	@Autowired
	public TestUtils testUtils;

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
}
