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

package com.simple2secure.commons.rules.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.simple2secure.api.model.DataType;

/**
 * 
 * @author Richard Heinz
 * 
 *         Annotation do mark a field as a parameter in a predefined action or condition class for the rule engine. The data will be used to
 *         display in the web simple2secure and to save a rule in the database.
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationRuleParam {

	String name();

	String description_de();

	String description_en();

	DataType type();
}
