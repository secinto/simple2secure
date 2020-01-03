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

package com.simple2secure.portal.rules.actions;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;

import com.simple2secure.api.model.Email;

public abstract class AbtractEmailAction implements Action {

	/**
	 * Method to fetch the email object from the facts. There will only be one object with the name "com.simple2secure.api.model.Email" (Map
	 * in the background)
	 *
	 * @param facts
	 *          which hold all saved facts
	 */
	@Override
	public void execute(Facts facts) throws Exception {
		action(facts.get("com.simple2secure.api.model.Email"));
	}

	/**
	 * Method which has to be implemented in the specific action class
	 *
	 * @param email
	 *          which has triggered the rule
	 * @throws Exception
	 *           of any type can be thrown
	 */
	protected abstract void action(Email email) throws Exception;
}
