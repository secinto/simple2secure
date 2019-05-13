package com.simple2secure.portal.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.portal.utils.TestUtils;

@Component
public class TestRunScheduler {

	@Autowired
	TestUtils testUtils;

	private static final Logger log = LoggerFactory.getLogger(TestRunScheduler.class);

	/**
	 * This function retrieves the testSequenceList (only those testCases which are not finished) from the database
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ApiException
	 * @throws ItemNotFoundRepositoryException
	 */
	// @Scheduled(fixedRate = 50000)
	/*
	 * public void checkTests() throws ItemNotFoundRepositoryException, ApiException, IOException, InterruptedException {
	 * List<TestCaseSequence> testSequenceList = testSequenceRepository.getAllIsFinishedAndScheduled(false, false);
	 *
	 * if (testSequenceList != null && !testSequenceList.isEmpty()) { log.debug("Preparing {} test cases to run", testSequenceList.size());
	 *
	 * for (TestCaseSequence tcs : testSequenceList) { if (tcs != null) { Tool tool = toolRepository.find(tcs.getToolId()); if (tool != null)
	 * { testUtils.runTestSequence(tcs, tool); tcs.setScheduled(true); testSequenceRepository.update(tcs); }
	 *
	 * } }
	 *
	 * } else { log.error("There are no provided test cases to run"); }
	 *
	 * }
	 */
}
