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

package com.simple2secure.portal.rules;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;

import com.simple2secure.api.model.Email;

public abstract class TemplateEmailAction implements Action{

	@Override
	public void execute(Facts facts) throws Exception {
		action(facts.get("com.simple2secure.api.model.Email"));
	}
	
	protected abstract void action(Email email) throws Exception;
}
