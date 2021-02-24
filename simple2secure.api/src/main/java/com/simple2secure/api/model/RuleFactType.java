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

package com.simple2secure.api.model;

import com.simple2secure.api.dbo.GenericDBObject;

/**
 * 
 * Enum to distinguish between different conditions/actions for the different types of fact
 * objects.
 * 
 * note: GENERAL is for conditions/actions which do not care which fact type is given,
 * so a GENERAL action can be used for every type of fact.
 *
 */
public enum RuleFactType 
{
	
	/*
	 * Adapt the method getEnumFromObject when the enum extends by an element.
	 */
	GENERAL, EMAIL, OSQUERYREPORT, NETWORKREPORT, TESTRESULT, TESTSEQUENCERESULT;
	
	/**
	 *  All objects which can be used as input for the rule engine are mapped to a RuleFactType enum element.
	 *  If the given object is not mapped to an element the method returns a null.
	 *
	 * @param <T>
	 * @param object
	 * @return
	 */
	public static <T extends GenericDBObject> RuleFactType getEnumFromObject(T object)
	{
		RuleFactType type = null;
		
		if(object instanceof Email)
		{
			type = EMAIL;
		}
		else if (object instanceof OsQueryReport)
		{
			type = OSQUERYREPORT;
		}
		else if (object instanceof NetworkReport)
		{
			type = NETWORKREPORT;
		}
		else if (object instanceof TestResult)
		{
			type = TESTRESULT;
		}
		else if (object instanceof TestSequenceResult)
		{
			type = TESTSEQUENCERESULT;
		}
		else 
		{
			// unknown object type
			type = null;
		}
		
		return type;
	}
}
