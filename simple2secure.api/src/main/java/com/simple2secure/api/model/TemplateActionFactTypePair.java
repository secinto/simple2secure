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
 * Class to map a template action to the type of the fact the action has been designed.
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateActionFactTypePair  extends GenericDBObject
{

	private static final long serialVersionUID = -7236624572309167786L;
	
	/**
	 * Type of the fact the action has been designed. 
	 */
	private RuleFactType type;
	
	/**
	 * Id of the template action which is saved in the database.
	 */
	@JsonSerialize(using=ToStringSerializer.class)
	private ObjectId templateActionId;
}
