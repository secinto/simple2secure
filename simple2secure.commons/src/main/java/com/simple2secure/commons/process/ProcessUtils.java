package com.simple2secure.commons.process;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessUtils {
	private static Logger log = LoggerFactory.getLogger(ProcessUtils.class);

	private static boolean isWindows = true;

	static {
		isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
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
		try {
			ProcessBuilder builder = new ProcessBuilder();
			if (isWindows) {
				builder.command("cmd.exe", "/c", executable);
			} else {
				builder.command("sh", "-c", "ls");
			}
			builder.directory(new File(System.getProperty("user.home")));
			Process process;
			process = builder.start();
			ProcessStreamConsumer streamConsumer = new ProcessStreamConsumer(process.getInputStream(), System.out::println);
			Executors.newSingleThreadExecutor().submit(streamConsumer);

			ProcessContainer procContainer = new ProcessContainer(process, streamConsumer);
			return procContainer;
		} catch (IOException e) {
			log.error("Couldn't create process for executable {} for reason {}", executable, e);
			throw new IllegalStateException("Couldn't create process for executable " + executable);
		}
	}
}
