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
package com.simple2secure.commons.service;

import com.google.common.base.Strings;

public enum ServiceCommands {

	START("START", "OK", "NOK"),
	STOP("STOP", "OK", "NOK"),
	RESET("RESET", "OK", "NOK"),
	GET_VERSION("GET_VERSION", "OK %REPLACE%", "NOK"),
	TERMINATE("TERMINATE", "OK", "NOK"),
	CHECK_STATUS("CHECK_STATUS", "OK", "NOK"),
	OTHER("OTHER", "OK", "NOK");

	public static String REPLACE_TAG = "%REPLACE%";
	public static String SEPARATION_TAG = " : ";
	public final String name;
	public final String positiveResponse;
	public final String negativeResponse;

	private ServiceCommands(String name, String positiveResponse, String negativeResponse) {
		this.name = name;
		this.positiveResponse = positiveResponse;
		this.negativeResponse = negativeResponse;
	}

	public String getName() {
		return name;
	}

	public String getPositiveResponse() {
		return positiveResponse;
	}

	public String getNegativeResponse() {
		return negativeResponse;
	}

	/**
	 * Returns the positive response of this service command as string. If a replaceable part is contained it is just removed. This response
	 * is the expected response for the positive case.
	 *
	 * @return The response as string.
	 */
	public String getPositiveCommandResponse() {
		return name + SEPARATION_TAG + positiveResponse;
	}

	/**
	 * Returns the positive response of this service command as string. If a replaceable part is contained it is just replaced with the
	 * provided replaceString provided as input. This response is the expected response for the positive case.
	 *
	 * @param replaceString
	 *          The string which should be used instead of the placeholder
	 * @return The response as string.
	 */
	public String getPositiveCommandResponse(String replaceString) {
		return name + SEPARATION_TAG + positiveResponse.replace(REPLACE_TAG, replaceString);
	}

	/**
	 * Returns the negative response of this service command as string. If a replaceable part is contained it is just removed. This response
	 * is the expected response for the negative case.
	 *
	 * @return The response as string.
	 */
	public String getNegativeCommandResponse() {
		return name + " : " + negativeResponse;
	}

	/**
	 * Returns the negative response of this service command as string. If a replaceable part is contained it is just replaced with the
	 * provided replaceString provided as input. This response is the expected response for the negative case.
	 *
	 * @param replaceString
	 *          The string which should be used instead of the placeholder
	 * @return The response as string.
	 */
	public String getNegativeCommandResponse(String replaceString) {
		return name + SEPARATION_TAG + negativeResponse.replace(REPLACE_TAG, replaceString);
	}

	/**
	 * Checks if the positive response for this service command is contained in the provided commandResponse string.
	 *
	 * @param commandResponse
	 *          The command response obtained as response to the sent service command.
	 * @return
	 */
	public boolean checkResponsePositive(String commandResponse) {
		if (!Strings.isNullOrEmpty(commandResponse)) {
			String parts[] = commandResponse.split(ServiceCommands.SEPARATION_TAG);
			if (parts != null && parts.length == 2) {
				if (name.equals(parts[0])) {
					if (positiveResponse.equals(parts[1])) {
						return true;
					}
				}
			}
		}
		return false;
	}

}