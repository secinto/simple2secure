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

package com.simple2secure.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.simple2secure.api.model.EmailConfiguration;
import com.simple2secure.api.model.RuleUserPair;
import com.simple2secure.api.model.TemplateRule;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RuleDTO {
	
	private List<EmailConfiguration> emailConfigurations = new ArrayList<>();
	private List<TemplateRule> templateRules = new ArrayList<>();
	private List<RuleUserPair> ruleUserPairs = new ArrayList<>();
	
	public RuleDTO() {
	}
	
	public RuleDTO(List<EmailConfiguration> emailConfigurations, List<TemplateRule> templateRules,
			List<RuleUserPair> ruleUserPairs) {
		this.emailConfigurations = emailConfigurations;
		this.templateRules = templateRules;
		this.ruleUserPairs = ruleUserPairs;
	}
}
