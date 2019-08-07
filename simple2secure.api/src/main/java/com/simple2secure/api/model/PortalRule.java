package com.simple2secure.api.model;

public class PortalRule extends FrontendRule {

	/**
	 *
	 */
	private static final long serialVersionUID = -6811188688302072478L;

	private ExtendedRule rule;

	public PortalRule() {
	}

	public PortalRule(String toolId, String contextId, String clazz, String name, String description, int priority, ExtendedRule rule,
			long timestamp, boolean active) {
		super(toolId, contextId, clazz, name, description, priority, timestamp, active);
		this.rule = rule;
	}

	public ExtendedRule getRule() {
		return rule;
	}

	public void setRule(ExtendedRule rule) {
		this.rule = rule;
	}
}
