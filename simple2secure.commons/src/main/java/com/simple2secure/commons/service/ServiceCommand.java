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
