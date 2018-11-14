package com.simple2secure.commons.process;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.service.ServiceUtils;

public class ProcessUtils {
	private static Logger log = LoggerFactory.getLogger(ProcessUtils.class);

	private static boolean isWindows = true;

	static {
		isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	/**
	 * Creates a OS process via the console for Windows and Linux. The provided executable string is used as command in the shell. The
	 * provided environment variables are set to be used in the console. If successful a {@link ProcessContainer} is returned which contains
	 * the process itself and a the associated {@link ProcessStreamConsumer}.
	 *
	 * @param executable
	 *          The string which is used as command in the console.
	 * @param cleanEnv
	 *          If set to true the existing environment variables are deleted.
	 * @param environment
	 *          The environment variables which are to be used in the console.
	 * @return The {@link ProcessContainer} with the created instances.
	 */
	public static ProcessContainer startProcess(String executable, boolean cleanEnv, boolean runAsAdmin, Map<String, String> environment) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			if (isWindows) {
				if (runAsAdmin) {
					String[] execParts = executable.split("//");
					String installProcess = environment.get(execParts[0].replaceAll("%", "").trim());
					String mode = execParts[1];
					String serviceName = environment.get(execParts[2].replaceAll("%", "").trim());

					builder.command("powershell.exe", "-command", "\"Start-Process -FilePath " + installProcess + " -ArgumentList \\\"//" + mode
							+ "//" + serviceName + "\\\", " + createCmdLineArgumentList(environment) + " -verb RunAs\"");
				} else {
					builder.command("cmd.exe", "/c", executable);
				}
			} else {
				builder.command("sh", "-c", "ls");
			}
			builder.directory(new File(System.getProperty("user.dir")));

			if (!runAsAdmin && environment != null && !environment.isEmpty()) {
				setUpEnvironment(builder, false, environment);
			}
			log.debug("ProcessBuilder command {}", builder.command());
			log.debug("ProcessBuilder directory {}", builder.directory());

			Process process;
			process = builder.start();
			ProcessStream streamConsumer = new ProcessStream(process.getInputStream(), new LoggingStringConsumer());
			Executors.newSingleThreadExecutor().submit(streamConsumer);

			ProcessContainer procContainer = new ProcessContainer(process, streamConsumer);
			return procContainer;
		} catch (IOException e) {
			log.error("Couldn't create process for executable {} for reason {}", executable, e);
			throw new IllegalStateException("Couldn't create process for executable " + executable);
		}

	}

	/**
	 * Create a command line argument which contains all environment parameters in a single line for execution in a Powershell ArgumentList.
	 *
	 * @param environment
	 *          The environment map.
	 * @return The respective command line.
	 */
	public static String createCmdLineArgumentList(Map<String, String> environment) {
		StringBuilder cmdLine = new StringBuilder();
		Map<String, String> convertedParams = ServiceUtils.convertParameters(environment);
		for (String key : convertedParams.keySet()) {
			String paramToConvert = convertedParams.get(key);
			if (paramToConvert.endsWith("\\")) {
				paramToConvert = paramToConvert.substring(0, paramToConvert.length() - 1);
			}

			if (StringUtils.containsWhitespace(paramToConvert)) {
				cmdLine.append("\\\"" + key + "=`\\\"" + paramToConvert + "`\\\"\\\", ");
				// cmdLine.append("\"`\"" + key + "=" + paramToConvert + "`\"\", ");
			} else {
				cmdLine.append("\\\"" + key + "=" + paramToConvert + "\\\", ");
				// cmdLine.append("\"" + key + "=" + paramToConvert + "\", ");
			}

		}

		String cmdLineString = cmdLine.toString();
		if (cmdLineString.endsWith(", ")) {
			cmdLineString = cmdLineString.substring(0, cmdLineString.length() - 2);
		}
		return cmdLineString;
	}

	/**
	 * Creates a OS process via the console for Windows and Linux. The provided executable string is used as command in the shell. If
	 * successful a {@link ProcessContainer} is returned which contains the process itself and a the associated {@link ProcessStreamConsumer}.
	 *
	 * @param executable
	 *          The string which is used as command in the shell/terminal.
	 * @return The {@link ProcessContainer} with the created instances.
	 */
	public static ProcessContainer startProcess(String executable) {
		return startProcess(executable, false, false, null);
	}

	/**
	 * Applies the provided environment variable to the provided {@link ProcessBuilder} such that the created processes have them available.
	 * Existing variables are overwritten if they are the same as the provided ones. All others are left as they are.
	 *
	 * @param builder
	 *          The {@link ProcessBuilder} object to which the environment variable should be added.
	 * @param cleanEnv
	 *          If set to true the existing environment variables are deleted.
	 * @param newEnv
	 *          The environment variables which should be added to the process.
	 */
	public static void setUpEnvironment(ProcessBuilder builder, boolean cleanEnv, Map<String, String> newEnv) {
		Map<String, String> env = builder.environment();
		log.debug("Current environment variables {}", env.toString());
		log.debug("Applying new environment variables {}", newEnv.toString());

		if (cleanEnv) {
			env.clear();
		}
		env.putAll(newEnv);
		log.debug("Newly set environment variables {}", env.toString());

	}
}
