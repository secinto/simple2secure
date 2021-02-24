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

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.simple2secure.api.dbo.GenericDBObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * Class to save how often the condition of the same rule has been
 * evaluated true with independent inputs.
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TriggeredRule extends GenericDBObject {
	
	private static final long serialVersionUID = -5472051873781398191L;
	
	/**
	 * Id of the rule is saved in the database.
	 */
	@JsonSerialize(using=ToStringSerializer.class)
	private ObjectId ruleId;
	
	/**
	 * Count how often the condition of the same rule has been
	 * evaluated true with independent inputs.
	 */
	private int count;
	
	/**
	 * Method to reset the counter.
	 */
	public void setCountToZero()
	{
		count = 0;
	}
	
	/**
	 * Method to increase the counter by one.
	 *  
	 * @return the new counter value.
	 */
	public int increaseCountByOne()
	{
		count++;
		return count;
	}
}
