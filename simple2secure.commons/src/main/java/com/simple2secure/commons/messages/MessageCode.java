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
package com.simple2secure.commons.messages;

import java.io.Serializable;

/**
 * Message interface which is used for the different {@link MessageCode} enums.<br/>
 *
 * Example enums to use:
 * <ul>
 * <li>{@link ClipperMessageCode1Core}</li>
 * </ul>
 *
 * The clipper message util ( {@link MessageCodeUtil} ) contains functions which helps to create localized {@link ClipperMessage}.
 *
 * @author mhiess
 */
public interface MessageCode extends Serializable {

	/**
	 * @return the value of the enum {@link Enum#name()}
	 */
	String getValue();

}
