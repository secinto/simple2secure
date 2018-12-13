package com.simple2secure.service.tasks;

import java.io.FileNotFoundException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.process.ProcessContainer;
import com.simple2secure.service.test.utils.TestLoggingObserver;

public class ProbeMonitor extends TimerTask {

	private Logger log = LoggerFactory.getLogger(ProbeMonitor.class);

	private ProcessContainer container;
	private TestLoggingObserver observer;

	public ProbeMonitor() throws FileNotFoundException {
		// ProcessUtils.invokeJavaProcess(null, false, "-cp", "../../release/simple2secure.probe-0.1.0.jar",
		// "com.simple2secure.probe.cli.ProbeCLI", "-l", "../../release/license.zip");
		//
		// observer = new TestLoggingObserver();
		// container.getObservable().addObserver(observer);
		// container.startObserving();
	}

	@Override
	public void run() {
		log.debug("Executing {}", ProbeUpdater.class);

	}

}
