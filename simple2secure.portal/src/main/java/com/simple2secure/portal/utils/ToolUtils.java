package com.simple2secure.portal.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;
import com.simple2secure.api.model.Command;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.Tool;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ToolRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.Exec;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

@Component
public class ToolUtils {
	private static Logger log = LoggerFactory.getLogger(ToolUtils.class);

	@Autowired
	ToolRepository toolRepository;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Value("${kubernetes.token}")
	private String token;

	@Value("${kubernetes.namespace}")
	private String namespace;

	@PostConstruct
	public void initialize() {
		ApiClient client = Config.fromToken(loadedConfigItems.getBaseKubernetesURL(), token, false);
		Configuration.setDefaultApiClient(client);
	}

	/**
	 * This function retrieves all tools from the kubernetes, adds the tool to the database if this entry does not exist.
	 *
	 * @param locale
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<List<Tool>> getKubernetesTools(String locale) {
		CoreV1Api api = new CoreV1Api();
		try {
			// Retrieve the list from the pods from the provided namespace
			V1PodList list = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
			if (list != null) {
				for (V1Pod pod : list.getItems()) {
					// If tool does not exist in the database add new entry
					if (!checkIfToolExistsInTheDatabase(pod.getMetadata().getName())) {
						addNewToolToTheDatabase(pod);
					}
				}
				// Return all tool from the repository
				List<Tool> tools = toolRepository.findAll();
				if (tools != null) {
					log.debug("Found {} tools in the database", tools.size());
					return new ResponseEntity<List<Tool>>(tools, HttpStatus.OK);
				}

			}

		} catch (ApiException e) {
			log.error("Error occured while retrieving pods {}", e.getMessage());
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale)),
				HttpStatus.NOT_FOUND);
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
	public ResponseEntity<TestResult> runTest(Test test, String podName, String locale)
			throws ApiException, IOException, InterruptedException, ItemNotFoundRepositoryException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		List<String> commands = generateCommands(test.getCommands());

		Exec exec = new Exec();

		boolean tty = System.console() != null;

		final Process proc = exec.exec(namespace, podName,
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

		proc.waitFor();

		// wait for any last output; no need to wait for input thread
		out.join();

		proc.destroy();

		return addTestResult(output.toString(), test, podName, locale);

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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ResponseEntity<TestResult> addTestResult(String result, Test test, String podName, String locale)
			throws ItemNotFoundRepositoryException {
		// TODO: user does not need to wait for the test result. Test result must be saved async

		if (!Strings.isNullOrEmpty(result) && test != null && !Strings.isNullOrEmpty(podName)) {
			TestResult testResult = new TestResult("nmap", result, System.currentTimeMillis());

			test.addTestResult(testResult);

			Tool tool = toolRepository.getToolByName(podName);

			if (tool != null) {
				if (test.getCreateInstance()) {
					test.setName(test.getName() + "_" + System.currentTimeMillis());
					tool.getTests().add(test);
				} else {
					List<Test> newTests = new ArrayList<>();

					for (Test tempTest : tool.getTests()) {
						if (tempTest.getName().equals(test.getName())) {
							newTests.add(test);
						} else {
							newTests.add(tempTest);
						}
					}
					tool.setTests(newTests);
				}

				toolRepository.update(tool);
				return new ResponseEntity<TestResult>(testResult, HttpStatus.OK);
			}
		}

		log.error("Problem occured while running test");
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale)),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function checks if the tool with the provided name exists in the database
	 *
	 * @param toolName
	 * @return
	 */
	private boolean checkIfToolExistsInTheDatabase(String toolName) {
		Tool tool = toolRepository.getToolByName(toolName);
		if (tool == null) {
			return false;
		}
		return true;
	}

	/**
	 * This function adds new tool with default command and test to the database
	 *
	 * @param pod
	 */
	private void addNewToolToTheDatabase(V1Pod pod) {
		List<Command> commands = new ArrayList<Command>();
		commands.add(new Command("nmap"));
		List<Test> tests = new ArrayList<>();
		Test test = new Test("nmap_simple_test", commands, true, false, null, false);
		tests.add(test);

		Tool tool = new Tool(pod.getMetadata().getName(), pod.getMetadata().getGenerateName(), null, tests, true);
		toolRepository.save(tool);
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
