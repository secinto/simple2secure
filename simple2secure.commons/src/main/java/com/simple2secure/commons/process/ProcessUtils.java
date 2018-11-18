package com.simple2secure.commons.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.service.ServiceUtils;

public class ProcessUtils {
	private static Logger log = LoggerFactory.getLogger(ProcessUtils.class);

	private static boolean isWindows = true;

	static {
		isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	public static ProcessContainer createProcess(String... executable) {
		return createProcess(null, false, executable);
	}

	/**
	 * Tries to create a process from the provided executable string using
	 * ProcessBuilder. The input, output and error streams a wrapped in a
	 * ProcessStream and Consumer.
	 * 
	 * @param executable The string which should be used to start a process via
	 *                   ProcessBuilder.
	 * @return The {@link ProcessContainer} which wraps the relevant streams,
	 *         processes and consumers.
	 */
	public static ProcessContainer createProcess(Map<String, String> environment, boolean cleanEnvironment,
			String... executable) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.redirectErrorStream(true);
			builder.command(executable);

			builder.directory(new File(System.getProperty("user.dir")));

			log.debug("ProcessBuilder command {}", builder.command());

			log.debug("ProcessBuilder directory {}", builder.directory());

			if (environment != null) {
				setUpEnvironment(builder, cleanEnvironment, environment);
			}

			Process process;
			process = builder.start();
			
//	        BufferedReader lineReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//	        lineReader.lines().forEach(log::debug);

//			new BufferedReader(new InputStreamReader(process.getErrorStream())).lines().forEach(log::debug);


			StreamGobbler inputGobbler = new StreamGobbler(process.getInputStream());
			//StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());

			ProcessGobbler processGobbler = new ProcessGobbler(inputGobbler); 
			processGobbler.startGobbling();

			ProcessContainer procContainer = new ProcessContainer(process, processGobbler);
			return procContainer;
		} catch (IOException e) {
			log.error("Couldn't create process for executable {} for reason {}", executable, e);
			throw new IllegalStateException("Couldn't create process for executable " + executable);
		}
	}

	/**
	 * Starts a process using powershell and Start-Process cmdlet. The provided
	 * executable string is used as command in the shell. </br>
	 * The provided executable string must be of the form
	 * <code>%PR_INSTALL% //MODE//%SERIVCE_NAME%</code>. </br>
	 * The %PR_INSTALL% must be the fully qualified file name of the prunsrv.exe
	 * file. If the %PR_INSTALL% location is set in the environment variable it is
	 * automatically take from there</br>
	 * The MODE is a string of the ones defined for prunsrv.exe. Usual modes are
	 * </br>
	 * <ul>
	 * <il><code>IS</code> for install service.</il> <il><code>DS</code> for delete
	 * service.</il> <il><code>ES</code> for execute (start) service.</il>
	 * <il><code>SS</code> for stop service.</il>.
	 * </ul>
	 * </br>
	 * </br>
	 * For instance providing an executable string
	 * <code>%PR_INSTALL //IS//%SERVICE_NAME%</code> would install a service which
	 * is defined via the environment variable SERVICE_NAME using the prunsrv.exe
	 * which is identified via PR_INSTALL. </br>
	 * Another way would be to provide the following executable string
	 * <code>D:/daemon-location/prunsrv.exe //DS//My Service</code>. For further
	 * information on the different modes and the usage of prunserv.exe see <a href=
	 * "https://commons.apache.org/proper/commons-daemon/procrun.html">Apache
	 * Commons Daemon</a> The provided environment variables are used to obtain the
	 * arguments for the command. The arguments are supplied using powershell
	 * ArgumentList. If successful a {@link ProcessContainer} is returned which
	 * contains the process itself and a the associated
	 * {@link ProcessStreamConsumer}.
	 *
	 * @param executable  The string which is used as command in the console.
	 * @param runAsAdmin  If set to true the powershell is executed as Administrator
	 *                    rights.
	 * @param environment The environment variables which are to be used in the
	 *                    console.
	 * @return The {@link ProcessContainer} which wraps the relevant streams,
	 *         processes and consumers, if the 3 parts of the executable are
	 *         supplied correctly.
	 */
	public static ProcessContainer manageServiceWindows(String executable, boolean runAsAdmin,
			Map<String, String> environment) {
		String[] execParts = executable.split("//");
		if (execParts.length == 3) {
			String installProcess = execParts[0];
			if (execParts[0].contains("%PR_INSTALL%")) {
				installProcess = environment.get(execParts[0].replaceAll("%", "").trim());
			}
			String mode = execParts[1];

			String serviceName = execParts[2];
			if (execParts[2].contains("%SERVICE_NAME%")) {
				serviceName = environment.get(execParts[2].replaceAll("%", "").trim());
			}
			if (runAsAdmin) {
				return createProcess("powershell.exe",
						"\"Start-Process -FilePath " + installProcess + " -ArgumentList \\\"`\\\"//" + mode + "//"
								+ serviceName + "`\\\"\\\", " + createCmdLineArgumentList(environment)
								+ " -verb RunAs\"");
			} else {
				return createProcess("powershell.exe", "\"Start-Process -FilePath " + installProcess
						+ " -ArgumentList \\\"`\\\"//" + mode + "//" + serviceName + "`\\\"\\\"");
			}
		}
		return null;
	}

	/**
	 * Returns the absolute Java executable file name if available in the system. If
	 * the environment is setup only <code>java</code> is returned since it can be
	 * used to launch a java application.
	 * 
	 * @return The absolute file name of the Java executable.
	 * @throws FileNotFoundException
	 */
	public static String getJavaExecutable() throws FileNotFoundException {

		ProcessContainer container = createConsoleProcess("java.exe -version", null, false);
		if (container.getProcess().exitValue() == 0) {
			/*
			 * Just using the java keyword functions since the environment is set up. Thus
			 * we don't mingle with the path.
			 */
			return "java";
		}

		/*
		 * Unfortunately the environment is not set up, thus we try to find the java
		 * executable using other means.
		 */
		String jreDirectory = System.getProperty("java.home");

		if (jreDirectory == null) {
			throw new IllegalStateException("java.home");
		}

		File executable;
		if (isWindows) {
			executable = new File(jreDirectory, "bin/java.exe");
		} else {
			executable = new File(jreDirectory, "bin/java");
		}
		if (!executable.isFile()) {
			throw new FileNotFoundException(executable.toString());
		}

		return executable.toString();
	}

	/**
	 * Invokes a Java process using the provided parameters. Only the parameters are
	 * required as input, such as the following list of string arguments.
	 * <code>-cp, classpath.lib.jar, -Dsomeparam, startclass.start, input-param1</code>
	 * The Java executable is obtained automatically if available in the system and
	 * supplied to invoke the java process.
	 * 
	 * @param arguments The list of parameters which should be applied as input to
	 *                  the Java process.
	 * @return The {@link ProcessContainer} which wraps the relevant streams,
	 *         processes and consumers.
	 * @throws FileNotFoundException
	 */
	public static ProcessContainer invokeJavaProcess(String... arguments) throws FileNotFoundException {
		return invokeJavaProcess(null, false, arguments);
	}

	/**
	 * Invokes a Java process using the provided parameters. Only the parameters are
	 * required as input, such as the following list of string arguments.
	 * <code>-cp, classpath.lib.jar, -Dsomeparam, startclass.start, input-param1</code>
	 * The Java executable is obtained automatically if available in the system and
	 * supplied to invoke the java process. The provided environment and the
	 * cleanEnv parameters are used to build the execution environment. See
	 * {@link #createProcess(String, Map, boolean)} for further details.
	 * 
	 * @param cmdArray    The list of parameters which should be applied as input to
	 *                    the Java process.
	 * @param environment The environment variables used to setup the Java
	 *                    environment.
	 * @param cleanEnv    True if all existing variables should be deleted.
	 * @return The {@link ProcessContainer} which wraps the relevant streams,
	 *         processes and consumers.
	 * @throws FileNotFoundException
	 */
	public static ProcessContainer invokeJavaProcess(Map<String, String> environment, boolean cleanEnv,
			String... arguments) throws FileNotFoundException {
		/*
		 * Add the java executable path to the command parameters.
		 */
		String[] extraArguments = new String[arguments.length + 1];
		extraArguments[0] = getJavaExecutable();
		System.arraycopy(arguments, 0, extraArguments, 1, arguments.length);

		return createProcess(environment, cleanEnv, extraArguments);
	}

	/**
	 * Creates a process executing the provided executable string using a console,
	 * CMD in Windows and SH in Linux based systems. The provided environment
	 * variables are added to the current environment setting. If the provided map
	 * contains an already existing variable, it is overwritten. If cleanEnv is set
	 * to true, the previously existing environment variables are cleared.
	 * 
	 * @param executable  The executable string which is executed in an console.
	 * @param environment The environment variables to use.
	 * @param cleanEnv    Specifies if the existing environment variables are
	 *                    cleared or not.
	 * @return The {@link ProcessContainer} which wraps the relevant streams,
	 *         processes and consumers.
	 */
	public static ProcessContainer createConsoleProcess(String executable, Map<String, String> environment,
			boolean cleanEnv) {
		if (isWindows) {
			return createProcess(environment, cleanEnv, "cmd.exe", "/c", executable);
		} else {
			return createProcess(environment, cleanEnv, "sh", "-c", executable);
		}
	}

	/**
	 * Create a command line argument which contains all environment parameters in a
	 * single line for execution in a Powershell ArgumentList.
	 *
	 * @param environment The environment map.
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
			cmdLine.append("\\\"`\\\"" + key + "=" + paramToConvert + "`\\\"\\\", ");
		}

		String cmdLineString = cmdLine.toString();
		if (cmdLineString.endsWith(", ")) {
			cmdLineString = cmdLineString.substring(0, cmdLineString.length() - 2);
		}
		return cmdLineString;
	}

	/**
	 * Applies the provided environment variable to the provided
	 * {@link ProcessBuilder} such that the created processes have them available.
	 * Existing variables are overwritten if they are the same as the provided ones.
	 * All others are left as they are.
	 *
	 * @param builder  The {@link ProcessBuilder} object to which the environment
	 *                 variable should be added.
	 * @param cleanEnv If set to true the existing environment variables are
	 *                 deleted.
	 * @param newEnv   The environment variables which should be added to the
	 *                 process.
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
