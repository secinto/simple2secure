/**
 *
 */
package com.simple2secure.portal.messages;

/*@BaseName("clipperMessages")
@LocaleData({ @Locale("en"), @Locale("de") })*/
public enum MessageCodesCore {

	/*
	 * No search results have been returned by the call
	 */
	core_no_search_result,

	/*
	 * A failure during the search process occured.
	 */
	core_search_failed,

	/*
	 * The provided URI was incorrect
	 */
	core_uri_incorrect,

	/*
	 * The selected encoding could not be applied.
	 */
	core_encoding_failed,

	/*
	 * The configuration could not be loaded
	 */
	core_configuration_failed,

	/*
	 * The search results could not be processed
	 */
	core_search_mapping_failed,

	/*
	 * The associated session could not be found
	 */
	core_session_mapping_failed,

	/*
	 * The preview could not be created
	 */
	core_creating_preview_failed,

	/*
	 * An external program could not be executed successfully
	 */
	core_external_execution_failed,

	/*
	 * The temp file could not be created
	 */
	core_saving_temp_file_failed,

	/*
	 * The destination file could not be created
	 */
	core_copy_file_failed,

	/*
	 * No such item was found
	 */
	core_no_such_item,

	core_;

	/*
	 * (non-Javadoc)
	 *
	 * @see at.exthex.bert.model.messages.BertMessage#getValue()
	 */
	/*@Override
	public String getValue() {
		return this.toString();
	}*/

}
