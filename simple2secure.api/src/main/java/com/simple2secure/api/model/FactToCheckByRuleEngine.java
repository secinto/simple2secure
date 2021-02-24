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
 * This class is used as model for saving the information if a object should be check by the rule engine
 * or already has been. To interpret the objectId right the factType (RuleFactType enum) has to be saved
 * too. With this information it is possible to distinguish which class belongs to the factId for further 
 * processing.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FactToCheckByRuleEngine extends GenericDBObject
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4215124051090148778L;
	
	/**
	 * The id of the object which is saved in the database and should be checked be the rule engine.
	 */
	@JsonSerialize(using=ToStringSerializer.class)
	private ObjectId factId;
	
	/**
	 * Fact type of the object which is represented with the factId.
	 */
	private RuleFactType factType;
	
	/**
	 * Boolean to see if the fact has been evaluated yet.
	 */
	private boolean isChecked;
}
