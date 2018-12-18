package com.simple2secure.portal.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Strings;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;
import com.simple2secure.api.model.Command;
import com.simple2secure.api.model.TestCase;
import com.simple2secure.api.model.TestCaseResult;
import com.simple2secure.api.model.TestCaseSequence;
import com.simple2secure.api.model.TestResultTestMapping;
import com.simple2secure.api.model.Tool;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestResultTestMappingRepository;
import com.simple2secure.portal.repository.TestSequenceRepository;
import com.simple2secure.portal.repository.TestTemplateRepository;
import com.simple2secure.portal.repository.ToolRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.Exec;

@Component
public class TestUtils {

	private static Logger log = LoggerFactory.getLogger(TestUtils.class);

	@Autowired
	TestResultRepository testResultRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	ToolRepository toolRepository;

	@Autowired
	TestSequenceRepository testSequenceRepository;

	@Autowired
	TestTemplateRepository testTemplateRepository;

	@Autowired
	TestResultTestMappingRepository testResultTestMappingRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Value("${kubernetes.namespace}")
	private String namespace;

	/**
	 * This function adds new TestCase to the testSequence list which will be called by the scheduler to execute those tests.
	 *
	 * @param testCase
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<TestCaseSequence> addTestCaseToTheList(TestCase testCase, String locale) throws ItemNotFoundRepositoryException {
		if (testCase != null) {

			if (!Strings.isNullOrEmpty(testCase.getToolId())) {
				Tool tool = toolRepository.find(testCase.getToolId());

				if (tool != null) {

					log.debug("Adding Test case for tool {}", tool.getName());

					testCase.setName(testCase.getName() + "_" + System.currentTimeMillis());
					testRepository.save(testCase);

					List<TestCase> testCaseList = new ArrayList<TestCase>();
					testCaseList.add(testCase);
					TestCaseSequence testCaseSequence = new TestCaseSequence(testCase.getToolId(), testCaseList);
					testSequenceRepository.save(testCaseSequence);

					return new ResponseEntity<TestCaseSequence>(testCaseSequence, HttpStatus.OK);
				}
			}
		}

		log.error("Problem occured while adding test sequence");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_adding_test_sequence", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns the list of the testResult objects which are mapped to the certain test id
	 *
	 * @param test
	 * @return
	 */
	public List<TestCaseResult> getAllTestResultsByTestId(TestCase test) {
		List<TestCaseResult> testResultList = new ArrayList<>();

		// Retrieve all TestResult test mappings
		List<TestResultTestMapping> trtList = testResultTestMappingRepository.getByTestId(test.getId());
		if (trtList != null) {
			for (TestResultTestMapping trt : trtList) {
				TestCaseResult testResult = testResultRepository.find(trt.getTestResultId());
				if (testResult != null) {
					testResultList.add(testResult);
				}
			}
		}
		return testResultList;
	}

	/**
	 * This function maps the test result to the test.
	 *
	 * TODO: This function must be changed completely!!!
	 *
	 * @param result
	 * @param test
	 * @param podName
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	private void addTestResult(String result, TestCase test, Tool tool) throws ItemNotFoundRepositoryException {
		// TODO: user does not need to wait for the test result. Test result must be saved async

		if (!Strings.isNullOrEmpty(result) && test != null && tool != null) {
			TestCaseResult testResult = new TestCaseResult("nmap", result, System.currentTimeMillis());
			ObjectId testResultId = testResultRepository.saveAndReturnId(testResult);

			if (tool != null) {

				test.setName(test.getName() + "_" + System.currentTimeMillis());
				test.setToolId(tool.getId());
				ObjectId testId = testRepository.saveAndReturnId(test);

				TestResultTestMapping testResultTestMapping = new TestResultTestMapping(testResultId.toString(), testId.toString());
				testResultTestMappingRepository.save(testResultTestMapping);

			}
		}

		log.error("Problem occured while running test");
	}

	/**
	 * This function runs test.
	 *
	 * TODO: This function must be changed completely so that run test occurs async
	 *
	 * @param test
	 * @param podName
	 * @param locale
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ItemNotFoundRepositoryException
	 */
	public void runTestSequence(TestCaseSequence testCaseSequence, Tool tool)
			throws ApiException, IOException, InterruptedException, ItemNotFoundRepositoryException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		// TODO: This must be changed in the future to run more tests from the sequence. Currently sequence contains only one test case
		TestCase test = testCaseSequence.getTestCases().get(0);

		List<String> commands = generateCommands(test.getCommands());

		Exec exec = new Exec();

		boolean tty = System.console() != null;

		final Process proc = exec.exec(namespace, tool.getName(),
				commands.isEmpty() ? new String[] { "sh" } : commands.toArray(new String[commands.size()]), true, tty);

		Thread in = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ByteStreams.copy(System.in, proc.getOutputStream());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		in.start();

		Thread out = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ByteStreams.copy(proc.getInputStream(), output);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		out.start();

		// proc.waitFor();

		// wait for any last output; no need to wait for input thread
		out.join();

		proc.destroy();

		addTestResult(output.toString(), test, tool);

	}

	/**
	 * This function iterates over all test commands and generates a list of strings with the command contents
	 *
	 * @param cmdList
	 * @return
	 */
	private List<String> generateCommands(List<Command> cmdList) {
		List<String> commands = new ArrayList<>();
		if (cmdList != null) {
			for (Command cmd : cmdList) {
				commands.add(cmd.getContent());
			}
		}
		return commands;
	}

}
