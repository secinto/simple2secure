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

import java.lang.reflect.ParameterizedType;

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;

import com.simple2secure.api.dbo.GenericDBObject;

/**
 * 
 * More specific implementation of the Condition interface for the portal. 
 *
 * @param <T> generic type as input which should be evaluated.
 */
public abstract class AbstractPortalCondition<T extends GenericDBObject> implements Condition 
{
	@Override
	public boolean evaluate(Facts facts) 
	{
		// fetching the type of the generic T
		@SuppressWarnings("unchecked")
		Class<T> factTypeClass = (Class<T>) ((ParameterizedType)
				getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		
		T factObject = facts.get(factTypeClass.getName());
		ObjectId contextId = facts.get(ObjectId.class.getName());
		
		boolean result = condition(factObject, contextId);
		return result;
	}
	
	/**
	 * Method which must be implemented specific for the type of the data.
	 * 
	 * @param data Object which should be evaluated.
	 * @param contextId The contextId from where the data came from.
	 * @return true if condition is fulfilled, false otherwise
	 */
	protected abstract boolean condition(T data, ObjectId contextId);
}
