/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
