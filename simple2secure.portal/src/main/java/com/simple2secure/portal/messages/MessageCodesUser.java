package com.simple2secure.portal.messages;

/*@BaseName("clipperMessages")
@LocaleData({ @Locale("en"), @Locale("de") })*/
public enum MessageCodesUser {

	/**
	 * Internal User Management failure
	 */
	user_internal_error,

	/**
	 * User already exists
	 */
	user_already_exists,

	/**
	 * User not found
	 */
	user_not_found,

	/*
	 * The given name is missing from the registration data
	 */
	user_given_name_missing,

	/*
	 * The family name is missing from the registration data
	 */
	user_family_name_missing,

	/*
	 * The username must be a valid email address
	 */
	user_valid_email_required,

	/*
	 * The password must be at least 14 characters long
	 */
	user_password_too_short,

	/*
	 * The provided password is not valid
	 */
	user_password_invalid,

	/*
	 * The provided password and passwordconfirmation not wqual
	 */
	user_passwordconfirmation_not_equal,

	/*
	 * Logged out successfully
	 */
	user_logged_out,

	user_other_error;
/*
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
