package com.simple2secure.commons.service;

import java.util.Arrays;

public class ServiceCommand {
	private Commands command;
	private String[] arguments;

	public ServiceCommand(Commands command, String[] arguments) {
		this.command = command;
		this.arguments = arguments;
	}

	public static ServiceCommand fromString(String possibleCommand) {
		String[] possibleCommandParts = possibleCommand.split(" ");
		if (possibleCommandParts[0].equalsIgnoreCase("Start")) {
			return new ServiceCommand(Commands.START, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else if (possibleCommandParts[0].equalsIgnoreCase("Stop")) {
			return new ServiceCommand(Commands.STOP, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else if (possibleCommandParts[0].equalsIgnoreCase("Reset")) {
			return new ServiceCommand(Commands.RESET, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else if (possibleCommandParts[0].equalsIgnoreCase("GetVersion")) {
			return new ServiceCommand(Commands.GET_VERSION, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else if (possibleCommandParts[0].equalsIgnoreCase("Terminate")) {
			return new ServiceCommand(Commands.TERMINATE, Arrays.copyOfRange(possibleCommandParts, 1, possibleCommandParts.length));
		} else {
			return new ServiceCommand(Commands.OTHER, possibleCommandParts);
		}
	}

	public Commands getCommand() {
		return command;
	}

	public String[] getArguments() {
		return arguments;
	}

}
