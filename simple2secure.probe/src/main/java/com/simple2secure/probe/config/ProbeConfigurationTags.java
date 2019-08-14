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
