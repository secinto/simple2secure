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
package com.simple2secure.commons.service;

import java.util.Arrays;

public class ServiceCommand {

	private ServiceCommands command;
	private String[] arguments;

	public ServiceCommand(ServiceCommands command, String[] arguments) {
		this.command = command;
		this.arguments = arguments;
	}

	public static ServiceCommand fromString(String possibleCommand) {
		String[] possibleCommandParts = possibleCommand.split(" ");
		if (possibleCommandParts[0].equalsIgnoreCase("Start")) {
			return new ServiceCommand(ServiceCommands.START, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else if (possibleCommandParts[0].equalsIgnoreCase("Stop")) {
			return new ServiceCommand(ServiceCommands.STOP, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else if (possibleCommandParts[0].equalsIgnoreCase("Reset")) {
			return new ServiceCommand(ServiceCommands.RESET, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else if (possibleCommandParts[0].equalsIgnoreCase("GetVersion")) {
			return new ServiceCommand(ServiceCommands.GET_VERSION, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else if (possibleCommandParts[0].equalsIgnoreCase("Terminate")) {
			return new ServiceCommand(ServiceCommands.TERMINATE, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else {
			return new ServiceCommand(ServiceCommands.OTHER, possibleCommandParts);
		}
	}

	public ServiceCommands getCommand() {
		return command;
	}

	public String[] getArguments() {
		return arguments;
	}

}
