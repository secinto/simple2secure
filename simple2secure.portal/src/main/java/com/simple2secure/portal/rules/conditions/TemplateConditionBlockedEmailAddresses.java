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

package com.simple2secure.portal.rules.conditions;

import java.util.Collection;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.Email;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;

@AnnotationCondition(name = "blocked email addresses", description_de = "Regel wird ausgelöst wenn die EMail von einer der angegeben Adressen kam.", description_en = "Rule will be triggerd if the mail was send by one of the given addresses")
public class TemplateConditionBlockedEmailAddresses extends AbtractEmailCondition {

	@AnnotationRuleParamArray(name = "email addresses", description_de = "EMail-Addressen welche die Regel auslösen.", description_en = "Mail addresses which will trigger the rule.", type = DataType._STRING)
	private Collection<String> addresses;

	@Override
	protected boolean condition(Email email) {
		if (addresses.contains(email.getFrom()))
			return true;
		return false;
	}
}
