package com.simple2secure.commons.service;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.commons.process.ProcessUtils;

public class ServiceUtils {

	private static Logger log = LoggerFactory.getLogger(ServiceUtils.class);

	public static String DEFAULT_LOG_LEVEL = "Debug";
	public static String DEFAULT_STOP_TIMEOUT = "10";
	public static String DEFAULT_STARTUP = "auto";
	public static String DEFAULT_START_STOP_MODE = "jvm";
	public static String DEFAULT_JVMMS = "256";
	public static String DEFAULT_JVMMX = "1024";
	public static String DEFAULT_JVMSS = "4000";
	public static String DEFAULT_STDOUT = "stdout.log";
	public static String DEFAULT_STDERR = "stderr.log";
	public static String DEFAULT_JVM = "%JAVA_HOME%\\bin\\server\\jvm.dll";
	public static String DEFAULT_START_IMAGE = "";
	public static String DEFAULT_STOP_IMAGE = "";
	public static String PROC_SRV_EXE = "prunsrv.exe";
	public static String DEFAULT_JVM_OPTIONS = "";

	private static String JVM_LIBRARY_REL = "bin\\server\\jvm.dll";

	/**
	 * Checks if the used JVM is 64 bit or not.
	 *
	 * @return True if the JVM is 64 bit.
	 */
	public static boolean isJVM_ARCH_64bit() {
		return System.getProperty("sun.arch.data.model") == "64" ? true : false;
	}

	/**
	 * Returns the JAVA installation path which is currently used.
	 *
	 * @return The JAVA home path.
	 */
	public static String getJVMPath() {
		return System.getProperty("java.home");
	}

	/**
	 * Returns the absolute file name of the PRUNSRV.EXE file which needs to be used for this architecture. The files are bundled with the jar
	 * itself.
	 *
	 * @return The absolute filename of the service.
	 */
	public static String getServiceInstaller() {
		ClassLoader classLoader = ServiceUtils.class.getClassLoader();

		File file = new File(classLoader.getResource("daemon/amd64/prunsrv.exe").getFile());
		if (!isJVM_ARCH_64bit()) {
			file = new File(classLoader.getResource("daemon/prunsrv.exe").getFile());
		}
		return file.getAbsolutePath();
	}

	/**
	 * Creates the environment solely for service execution. Only the serviceName must be specified which should be managed. The prunsrv.exe
	 * is used from the package.
	 *
	 * @param serviceName
	 *          The service to be managed.
	 * @return
	 */
	public static Map<String, String> createServiceEnvironment(String serviceName) {
		return createServiceEnvironment(false, null, serviceName);
	}

	/**
	 * Creates the environment solely for service execution. Therefore only the service name and the prunsrv.exe path are provided as
	 * environment variables. If useOwnPrunSrv is true the installPath is used and the prunsrv.exe is expected to be available from there. If
	 * not the own prunsrv.exe is used, depending on the architecture.
	 *
	 * @param useOwnPrunSrv
	 *          If yes the prunsrv.exe needs to exist in the installPath
	 * @param installPath
	 *          The installPath from which the prunsrv.exe should be executed.
	 * @param serviceName
	 *          The service name which needs to be managed.
	 * @return
	 */
	public static Map<String, String> createServiceEnvironment(boolean useOwnPrunSrv, String installPath, String serviceName) {
		Map<String, String> env = new TreeMap<String, String>();

		env.put("SERVICE_NAME", serviceName);

		if (useOwnPrunSrv) {
			if (!installPath.endsWith("\\")) {
				installPath = installPath + "\\";
			}
			env.put("PR_INSTALL", installPath + PROC_SRV_EXE);
		} else {
			env.put("PR_INSTALL", getServiceInstaller());
		}
		return env;
	}

	/**
	 * Creates the environment settings for Windows service creation using procrun, based on the provided input parameters. </br>
	 * The prunsrv.exe is used from the ServiceUtils itself. </br>
	 * The logging output will be created in the installPath and the file names are used from the default settings. All other parameters are
	 * also set using the default settings.
	 *
	 * @param installPath
	 *          The path into which all the relevant files (procsrv, jar file, images) are installed and available from.
	 * @param serviceName
	 *          The name of the service as it will be shown in the windows services panel.
	 * @param serviceDescription
	 *          The description which should be used for the service which is shown in the panel.
	 * @param library
	 *          The jar file which contains the executable code. The jar should be a fat jar containing all dependencies.
	 * @param startClass
	 *          The fully qualified class name which contains the start method.
	 * @param startMethod
	 *          The name of the start method. The method must be static.
	 * @param stopClass
	 *          The fully qualified class name which contains the stop method.
	 * @param stopMethod
	 *          The name of the stop method. The method must be static.
	 * @return A map containing all service relevant environment variables.
	 */
	public static Map<String, String> createInstallEnvironment(String installPath, String serviceName, String serviceDescription,
			String library, String startClass, String startMethod, String stopClass, String stopMethod) {
		return createInstallEnvironment(installPath, serviceName, serviceDescription, library, startClass, startMethod, stopClass, stopMethod,
				installPath);
	}

	/**
	 * Creates the environment settings for Windows service creation using procrun, based on the provided input parameters. </br>
	 * The procsrv.exe is to be expected to exist in the installPath., otherwise the service creation fails. </br>
	 * The logging output will be created in the provided logPath and the file names are used from the default settings. All other parameters
	 * are also set using the default settings.
	 *
	 * @param installPath
	 *          The path into which all the relevant files (procsrv, jar file, images) are installed and available from.
	 * @param serviceName
	 *          The name of the service as it will be shown in the windows services panel.
	 * @param serviceDescription
	 *          The description which should be used for the service which is shown in the panel.
	 * @param library
	 *          The jar file which contains the executable code. The jar should be a fat jar containing all dependencies.
	 * @param startClass
	 *          The fully qualified class name which contains the start method.
	 * @param startMethod
	 *          The name of the start method. The method must be static.
	 * @param stopClass
	 *          The fully qualified class name which contains the stop method.
	 * @param stopMethod
	 *          The name of the stop method. The method must be static.
	 * @param logPath
	 *          The path where the log files should be written to.
	 * @return A map containing all service relevant environment variables.
	 */
	public static Map<String, String> createInstallEnvironment(String installPath, String serviceName, String serviceDescription,
			String library, String startClass, String startMethod, String stopClass, String stopMethod, String logPath) {
		/*
		 * Creates an environment with default settings except the service specific ones.
		 */
		return createInstallEnvironment(false, installPath, serviceName, serviceDescription, library, DEFAULT_STARTUP, startClass, startMethod,
				DEFAULT_START_STOP_MODE, DEFAULT_START_IMAGE, stopClass, stopMethod, DEFAULT_START_STOP_MODE, DEFAULT_STOP_TIMEOUT,
				DEFAULT_STOP_IMAGE, logPath, DEFAULT_LOG_LEVEL, DEFAULT_STDOUT, DEFAULT_STDOUT, DEFAULT_JVM, DEFAULT_JVMMS, DEFAULT_JVMMX,
				DEFAULT_JVMSS, DEFAULT_JVM_OPTIONS);
	}

	/**
	 * Creates the environment settings for Windows service creation using procrun, based on the provided input parameters. For the stdout and
	 * stderr only the file name must be provided since they will be created in the specified logPath directory. For the start and stop image
	 * also only the file name must be provided since they are expected to be available from the installPath. If an empty string is provided
	 * for them they are omitted. </br>
	 * If useOwnPrunSrv is set to true the prunsrv.exe, for the used architecture, is to be expected to exist in the installPath, otherwise
	 * the service creation fails. </br>
	 * If jvmPath is null or empty it is tried to obtain the path automatically from the already available system environment variables. </br>
	 *
	 * @param useOwnPrunSrv
	 *          If set to true the prunsrv.exe is expected to exist in the installPath in the given architecture.
	 * @param installPath
	 *          The path into which all the relevant files (procsrv, jar file, images) are installed and available from.
	 * @param serviceName
	 *          The name of the service as it will be shown in the windows services panel.
	 * @param serviceDescription
	 *          The description which should be used for the service which is shown in the panel.
	 * @param library
	 *          The jar file which contains the executable code. The jar should be a fat jar containing all dependencies.
	 * @param startup
	 *          The type of startup mechanisms which should be used (auto, manual, ...).
	 * @param startClass
	 *          The fully qualified class name which contains the start method.
	 * @param startMethod
	 *          The name of the start method. The method must be static.
	 * @param startMode
	 *          The name of the start mode. Usually this will only be jvm.
	 * @param startImage
	 *          The image (icon - *.ico) which should be used. Needs to be available relatively from the install path.
	 * @param stopClass
	 *          The fully qualified class name which contains the stop method.
	 * @param stopMethod
	 *          The name of the stop method. The method must be static.
	 * @param stopMode
	 *          The name of the stop mode. Usually this will only be jvm.
	 * @param stopTimeout
	 *          The amount of seconds which should be waited before the service is terminated if not finished with stopping.
	 * @param stopImage
	 *          The image (icon - *.ico) which should be used. Needs to be available relatively from the install path.
	 * @param logPath
	 *          The path where the log files should be written to.
	 * @param logLevel
	 *          The log level which should be used (Error, Warning, Info, Debug)
	 * @param stdout
	 *          The name of the file to which the stdout should be written
	 * @param stderr
	 *          The name of the file to which the stderr should be written
	 * @param jvmPath
	 *          The path to the jvm.dll which should be used. If it is empty or null it is tried to obtain the path automatically.
	 * @param jvmms
	 *          Initial memory pool
	 * @param jvmmx
	 *          Maximum memory pool
	 * @param jvmss
	 *          Thread stack size
	 * @param jvmOptions
	 *          JVM options which should be passed to the methods.
	 * @return A map containing all service relevant environment variables.
	 */
	public static Map<String, String> createInstallEnvironment(boolean useOwnPrunSrv, String installPath, String serviceName,
			String serviceDescription, String library, String startup, String startClass, String startMethod, String startMode, String startImage,
			String stopClass, String stopMethod, String stopMode, String stopTimeout, String stopImage, String logPath, String logLevel,
			String stdout, String stderr, String jvmPath, String jvmms, String jvmmx, String jvmss, String jvmOptions) {
		Map<String, String> env = new TreeMap<String, String>();

		if (!installPath.endsWith("\\")) {
			installPath = installPath + "\\";
		}
		if (!logPath.endsWith("\\")) {
			logPath = logPath + "\\";
		}

		env.put("SERVICE_NAME", serviceName);

		if (useOwnPrunSrv) {
			env.put("PR_INSTALL", installPath + PROC_SRV_EXE);
		} else {
			env.put("PR_INSTALL", getServiceInstaller());
		}
		env.put("PR_DESCRIPTION", serviceDescription);

		env.put("PR_LOGPREFIX", serviceName.replaceAll(" ", "").trim());
		env.put("PR_LOGPATH", logPath);
		env.put("PR_STDOUTPUT", logPath + stdout);
		env.put("PR_STDERROR", logPath + stderr);
		env.put("PR_LOGLEVEL", logLevel);

		if (!Strings.isNullOrEmpty(jvmPath)) {
			if (jvmPath.contains("%JAVA_HOME%")) {
				String javaHomePath = getJVMPath();
				jvmPath = jvmPath.replace("%JAVA_HOME%", javaHomePath);
				env.put("PR_JVM", jvmPath);
			} else {
				env.put("PR_JVM", jvmPath);
			}
		} else {
			String javaHome = getJVMPath();
			env.put("PR_JVM", javaHome + JVM_LIBRARY_REL);
		}
		env.put("PR_CLASSPATH", installPath + library);

		env.put("PR_STARTUP", startup);
		env.put("PR_STARTMODE", startMode);
		env.put("PR_STARTCLASS", startClass);
		env.put("PR_STARTMETHOD", startMethod);

		env.put("PR_STOPMODE", stopMode);
		env.put("PR_STOPCLASS", stopClass);
		env.put("PR_STOPMETHOD", stopMethod);
		env.put("PR_STOPTIMEOUT", stopTimeout);

		if (!Strings.isNullOrEmpty(startImage)) {
			env.put("PR_STARTIMAGE", installPath + startImage);
		}

		if (!Strings.isNullOrEmpty(stopImage)) {
			env.put("PR_STOPIMAGE", installPath + stopImage);
		}
		env.put("PR_JVMMS", DEFAULT_JVMMS);
		env.put("PR_JVMMX", DEFAULT_JVMMX);
		env.put("PR_JVMSS", DEFAULT_JVMSS);

		if (!Strings.isNullOrEmpty(jvmOptions)) {
			env.put("PR_JVMOPTIONS", jvmOptions);
		}
		// StringUtils.join(env.entrySet().toArray(), "\n")
		log.debug("The created service install environment variables are {} ", env.toString());
		return env;
	}

	/**
	 * Installs the provided service as Windows service using the provided parameters. The library is expected to exist in the installPath
	 * directory. The logging output will be created also in the installPath directory. The provided class name and methods are used for
	 * starting and stopping the service.
	 *
	 * @param installPath
	 *          The path into which all the relevant files (procsrv, jar file, images) are installed and available from.
	 * @param serviceName
	 *          The name of the service as it will be shown in the windows services panel.
	 * @param serviceDescription
	 *          The description which should be used for the service which is shown in the panel.
	 * @param library
	 *          The jar file which contains the executable code. The jar should be a fat jar containing all dependencies.
	 * @param startClass
	 *          The fully qualified class name which contains the start method.
	 * @param startMethod
	 *          The name of the start method. The method must be static.
	 * @param stopClass
	 *          The fully qualified class name which contains the stop method.
	 * @param stopMethod
	 *          The name of the stop method. The method must be static.
	 * @return
	 */
	public static ProcessContainer installService(String installPath, String serviceName, String serviceDescription, String library,
			String startClass, String startMethod, String stopClass, String stopMethod) {
		StringBuilder serviceString = new StringBuilder();
		serviceString.append("%PR_INSTALL% //IS//%SERVICE_NAME%");
		Map<String, String> environment = createInstallEnvironment(installPath, serviceName, serviceDescription, library, startClass,
				startMethod, stopClass, stopMethod);
		return ProcessUtils.startProcess(serviceString.toString(), false, true, environment);
	}

	/**
	 * Starts the provided service from the Windows services.
	 *
	 * @param serviceName
	 *          The service name to started.
	 * @return
	 */
	public static ProcessContainer startService(String serviceName) {
		StringBuilder serviceString = new StringBuilder();
		serviceString.append("%PR_INSTALL% //ES//%SERVICE_NAME%");
		Map<String, String> environment = createServiceEnvironment(serviceName);

		return ProcessUtils.startProcess(serviceString.toString(), false, false, environment);
	}

	/**
	 * Stops the provided service from the Windows services.
	 *
	 * @param serviceName
	 *          The service name to be stopped
	 * @return
	 */
	public static ProcessContainer stopService(String serviceName) {
		StringBuilder serviceString = new StringBuilder();
		serviceString.append("%PR_INSTALL% //SS//%SERVICE_NAME%");
		Map<String, String> environment = createServiceEnvironment(serviceName);

		return ProcessUtils.startProcess(serviceString.toString(), false, false, environment);
	}

	/**
	 * Deletes the provided service from the Windows services.
	 *
	 * @param serviceName
	 *          The service to be deleted.
	 * @return
	 */
	public static ProcessContainer deleteService(String serviceName) {
		StringBuilder serviceString = new StringBuilder();
		serviceString.append("%PR_INSTALL% //DS//%SERVICE_NAME%");
		Map<String, String> environment = createServiceEnvironment(serviceName);

		return ProcessUtils.startProcess(serviceString.toString(), false, false, environment);
	}

	/**
	 * Converts the created install parameters to be used in command line direct instead as being set as environment variables.
	 *
	 * @param params
	 *          The environment variables which should be converted. Only the names created in createInstallEnvironment can be converted.
	 * @return The converted variables as Map.
	 */
	public static Map<String, String> convertParameters(Map<String, String> params) {
		Map<String, String> convertedParams = new TreeMap<String, String>();
		for (String key : params.keySet()) {
			String value = params.get(key);
			switch (key) {
			case "SERVICE_NAME":
				convertedParams.put("--DisplayName", value);
				break;
			case "PR_STARTUP":
				convertedParams.put("--Startup", value);
				break;
			case "PR_LOGPATH":
				convertedParams.put("--LogPath", value);
				break;
			case "PR_LOGPREFIX":
				convertedParams.put("--LogPrefix", value);
				break;
			case "PR_LOGLEVEL":
				convertedParams.put("--LogLevel", value);
				break;
			case "PR_STDOUTPUT":
				convertedParams.put("--StdOutput", value);
				break;
			case "PR_STDERROR":
				convertedParams.put("--StdError", value);
				break;
			case "JAVA_HOME":
				convertedParams.put("--JavaHome", value);
				break;
			case "PR_JVM":
				convertedParams.put("--Jvm", value);
				break;
			case "PR_JVMMS":
				convertedParams.put("--JvmMs", value);
				break;
			case "PR_JVMMX":
				convertedParams.put("--JvmMx", value);
				break;
			case "PR_JVMSS":
				convertedParams.put("--JvmSs", value);
				break;
			case "PR_JVMOPTIONS":
				convertedParams.put("--JvmOptions", value);
				break;
			case "PR_CLASSPATH":
				convertedParams.put("--Classpath", value);
				break;
			case "PR_STARTMODE":
				convertedParams.put("--StartMode", value);
				break;
			case "PR_STARTCLASS":
				convertedParams.put("--StartClass", value);
				break;
			case "PR_STARTMETHOD":
				convertedParams.put("--StartMethod", value);
				break;
			case "PR_STARTPARAMS":
				convertedParams.put("--StartParams", value);
				break;
			case "PR_STOPMODE":
				convertedParams.put("--StopMode", value);
				break;
			case "PR_STOPCLASS":
				convertedParams.put("--StopClass", value);
				break;
			case "PR_STOPMETHOD":
				convertedParams.put("--StopMethod", value);
				break;
			case "PR_STOPPARAMS":
				convertedParams.put("--StopParams", value);
				break;
			case "PR_STOPTIMEOUT":
				convertedParams.put("--StopTimeout", value);
				break;
			case "PR_STARTIMAGE":
				convertedParams.put("--StartImage", value);
				break;
			case "PR_STOPIMAGE":
				convertedParams.put("--StopImage", value);
				break;

			}
		}
		return convertedParams;
	}
}
