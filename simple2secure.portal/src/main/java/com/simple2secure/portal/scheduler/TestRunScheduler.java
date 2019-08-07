package com.simple2secure.portal.scheduler;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.TestCaseSequence;
import com.simple2secure.api.model.Tool;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.TestSequenceRepository;
import com.simple2secure.portal.repository.ToolRepository;
import com.simple2secure.portal.utils.TestUtils;

import io.kubernetes.client.ApiException;

@Component
public class TestRunScheduler {

	@Autowired
	TestSequenceRepository testSequenceRepository;

	@Autowired
	ToolRepository toolRepository;

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
	@Scheduled(fixedRate = 50000)
	public void checkTests() throws ItemNotFoundRepositoryException, ApiException, IOException, InterruptedException {
		List<TestCaseSequence> testSequenceList = testSequenceRepository.getAllIsFinishedAndScheduled(false, false);

		if (testSequenceList != null && !testSequenceList.isEmpty()) {
			log.debug("Preparing {} test cases to run", testSequenceList.size());

			for (TestCaseSequence tcs : testSequenceList) {
				if (tcs != null) {
					Tool tool = toolRepository.find(tcs.getToolId());
					if (tool != null) {
						testUtils.runTestSequence(tcs, tool);
						tcs.setScheduled(true);
						testSequenceRepository.update(tcs);
					}

				}
			}

		} else {
			log.error("There are no provided test cases to run");
		}

	}
}
