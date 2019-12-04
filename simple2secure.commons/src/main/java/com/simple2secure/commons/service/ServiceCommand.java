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

import java.util.Arrays;

public class ServiceCommand {

	private ServiceCommands command;
	private String[] arguments;

	public ServiceCommand(ServiceCommands command) {
		this.command = command;
		arguments = new String[0];
	}

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
		} else if (possibleCommandParts[0].equalsIgnoreCase("CheckStatus")) {
			return new ServiceCommand(ServiceCommands.CHECK_STATUS, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
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

	@Override
	public String toString() {
		StringBuilder commandString = new StringBuilder();
		commandString.append("COMMAND: " + command.name());
		commandString.append("ARGUMENTS:");
		for (String argument : arguments) {
			commandString.append(" " + argument);
		}
		return commandString.toString();
	}

}
