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

import java.lang.reflect.ParameterizedType;

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;

import com.simple2secure.api.dbo.GenericDBObject;

/**
 * 
 * More specific implementation of the Action interface for the portal. 
 *
 * @param <T> generic type as input which has been checked by the conditions before.
 */
public abstract class AbstractPortalAction<T extends GenericDBObject> implements Action 
{

	@Override
	public void execute(Facts facts) throws Exception {
		// fetching the type of the generic T
		@SuppressWarnings("unchecked")
		Class<T> factTypeClass = (Class<T>) ((ParameterizedType) 
				getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		
		T factObject = facts.get(factTypeClass.getName());
		ObjectId contextId = facts.get(ObjectId.class.getName());
		
		action(factObject, contextId);
	}
	
	/**
	 * Method which must be implemented specific for the type of the data.
	 * 
	 * @param data object which should be evaluated.
	 * @param contextId The contextId from where the data came from.
	 */
	protected abstract void action(T fact, ObjectId contextId) throws Exception;
	
}
