/**
 *
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
