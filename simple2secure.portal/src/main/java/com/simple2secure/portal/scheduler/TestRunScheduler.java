package com.simple2secure.portal.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestRunRepository;
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.TestUtils;

@Component
public class TestRunScheduler {

	@Autowired
	TestUtils testUtils;

	@Autowired
	PortalUtils portalUtils;

	@Autowired
	TestRepository testRepository;

	@Autowired
	TestRunRepository testRunRepository;

	private static final Logger log = LoggerFactory.getLogger(TestRunScheduler.class);

	/**
	 * This function checks if there are some tests which need to be executed and adds those test to the TestRun table in the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 *
	 */

	@Scheduled(fixedRate = 50000)
	public void checkTests() throws ItemNotFoundRepositoryException {

		List<Test> tests = testRepository.getScheduledTest();

		if (tests != null && !tests.isEmpty()) {

			for (Test test : tests) {
				long currentTimestamp = System.currentTimeMillis();
				// Calculate the difference between last execution time and current timestamp
				TestRun testRun = new TestRun(test.getId(), test.getPodId(), false, TestRunType.AUTOMATIC_PORTAL);

				long millisScheduled = portalUtils.convertTimeUnitsToMilis(test.getScheduledTime(), test.getScheduledTimeUnit());
				long nextExecutionTime = test.getLastExecution() + millisScheduled;
				long executionTimeDifference = nextExecutionTime - currentTimestamp;

				if (test.getLastExecution() == 0 || executionTimeDifference < 0) {
					test.setLastExecution(currentTimestamp);
					testRunRepository.save(testRun);
					testRepository.update(test);
					// TODO: Add notification
				}
			}

		} else {
			log.error("There are no test cases which need to be scheduled");
		}

	}

}
