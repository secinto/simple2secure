package com.simple2secure.probe.config;

import com.google.common.base.Strings;

public enum ProbeConfigurationTags {
	config_supplier, task_supplier, stylesheet, capture_filter, interface_number, connection_timeout, use_configured_iface, show_interfaces, external_address, external_port, processors, processing_factory, port, height(
			"buttons.size.height"), width("buttons.size.width"), location("db_config.location"), dbURI("db_config.dbURI"), write_user(
					"db_config.write_user"), write_password("db_config.write_password"), read_user("db_config.read_user"), read_password(
							"db_config.read_password"), time_slot_size("db_config.time_slot_size"), properties("properties");

	private String name;

	private ProbeConfigurationTags() {
		name = name();
	}

	private ProbeConfigurationTags(String name) {
		this.name = Strings.isNullOrEmpty(name) ? name() : name;
	}

	public String getTag() {
		return name;
	}

}
