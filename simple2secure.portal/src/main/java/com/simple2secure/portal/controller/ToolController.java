package com.simple2secure.portal.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.simple2secure.api.model.Command;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestResult;
import com.simple2secure.api.model.Tool;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
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

@RestController
public class ToolController {

	@Autowired
	ToolRepository repository;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@RequestMapping(value = "/api/tool/{user_id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Tool>> getPacketByUserID(@PathVariable("user_id") String user_id,
			@RequestHeader("Accept-Language") String locale) {
		return new ResponseEntity<List<Tool>>(this.repository.getToolsByUserID(user_id), HttpStatus.OK);
	}

	/*
	 * private Config initializeConnection() { Config config = new ConfigBuilder()
	 * .withMasterUrl(ConfigItems.KUBERNETES_BASE_URL) .withTrustCerts(true)
	 * .withOauthToken(ConfigItems.kubernetes_token.replaceAll("(\\r|\\n)", ""))
	 * .build(); return config; }
	 */

	private ApiClient initialize() {
		ApiClient client = Config.fromUserPassword(loadedConfigItems.getBaseKubernetesURL(), "admin",
				"PxELDtfxo5p9jWWK", false);
		client.getHttpClient().setReadTimeout(35, TimeUnit.SECONDS);
		// ApiClient client = Config.fromToken(ConfigItems.KUBERNETES_BASE_URL,
		// ConfigItems.kubernetes_token, false);
		return client;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/tools", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Tool>> getAllTools(@RequestHeader("Accept-Language") String locale) {

		/*
		 * KubernetesClient client = new
		 * DefaultKubernetesClient(initializeConnection());
		 * 
		 * PodList pods = client.pods().inAnyNamespace().list();
		 */

		Configuration.setDefaultApiClient(initialize());

		CoreV1Api api = new CoreV1Api();

		try {
			// V1PodList list = api.listNamespacedPod("default", null, null, null, null,
			// null, null, null, null, null);
			V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);

			if (list != null) {
				for (V1Pod pod : list.getItems()) {
					Tool queryTool = this.repository.getToolByName(pod.getMetadata().getName());
					if (queryTool == null) {
						List<Command> commands = new ArrayList<Command>();
						commands.add(new Command("nmap"));
						List<Test> tests = new ArrayList<>();
						Test test = new Test("nmap_simple_test", commands, true, false, null, false);
						tests.add(test);

						Tool tool = new Tool(pod.getMetadata().getName(), pod.getMetadata().getGenerateName(), null,
								tests, true);
						this.repository.save(tool);
					}

				}

				List<Tool> tools = this.repository.findAll();

				return new ResponseEntity<List<Tool>>(tools, HttpStatus.OK);
			} else {
				List<Tool> tools = this.repository.findAll();
				if (tools == null) {
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService
									.getMessage("problem_occured_while_getting_retrieving_pods", locale)),
							HttpStatus.NOT_FOUND);
				} else {
					return new ResponseEntity<List<Tool>>(tools, HttpStatus.OK);
				}

			}

		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Create own namespace in kubernetes and add only those pods which are needed
		 * So that pods which are automatically installed on the kubernetes are not
		 * shown in the view
		 */

		return new ResponseEntity(
				new CustomErrorType(
						messageByLocaleService.getMessage("problem_occured_while_getting_retrieving_pods", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/tool/{name}/run", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestResult> runCommand(@RequestBody Test test, @PathVariable("name") String podName,
			@RequestHeader("Accept-Language") String locale) throws InterruptedException, ApiException, IOException {

		String namespace = "default";
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		List<String> commands = new ArrayList<>();

		for (Command cmd : test.getCommands()) {
			commands.add(cmd.getContent());
		}

		Configuration.setDefaultApiClient(initialize());
		Exec exec = new Exec();

		boolean tty = System.console() != null;

		final Process proc = exec.exec(namespace, podName,
				commands.isEmpty() ? new String[] { "sh" } : commands.toArray(new String[commands.size()]), true, tty);

		Thread in = new Thread(new Runnable() {
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
		String returnMessage = output.toString();
		Date date = new Date();
		String timestamp = new Timestamp(date.getTime()).toString();
		TestResult cmdResponse = new TestResult("nmap", returnMessage, timestamp);
		List<TestResult> testResults = test.getTestResult();
		if (testResults != null) {
			test.getTestResult().add(cmdResponse);
		}

		else {
			List<TestResult> tempResult = new ArrayList<>();
			tempResult.add(cmdResponse);
			test.setTestResult(tempResult);
		}

		Tool tool = this.repository.getToolByName(podName);

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
		}

		try {
			this.repository.update(tool);
		} catch (ItemNotFoundRepositoryException e) {
			return new ResponseEntity(new CustomErrorType(e.getMessage()), HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<TestResult>(cmdResponse, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/tool/{userID}", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Tool> saveTool(@RequestBody Tool tool, @PathVariable("userID") String userID,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (tool.getId() != null) {
			if (!Strings.isNullOrEmpty(userID)) {
				tool.setUserUUID(userID);

				List<Test> tests = new ArrayList<>();
				for (int i = 1; i < 3; i++) {
					Test test = new Test("Test " + i, null, true, false, null, false);
					tests.add(test);
				}

				tool.setTests(tests);
				this.repository.save(tool);
				return new ResponseEntity<Tool>(tool, HttpStatus.OK);
			} else {
				return new ResponseEntity(
						new CustomErrorType(messageByLocaleService.getMessage("userId_not_provided", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			if (!Strings.isNullOrEmpty(tool.getUserUUID())) {
				this.repository.update(tool);
				return new ResponseEntity<Tool>(tool, HttpStatus.OK);
			} else {
				return new ResponseEntity(
						new CustomErrorType(messageByLocaleService.getMessage("userId_not_provided", locale)),
						HttpStatus.NOT_FOUND);
			}
		}
	}
}

/*
 * class SimpleListener implements ExecListener {
 * 
 * @Override public void onOpen(Response response) {
 * System.out.println(response.message()); }
 * 
 * @Override public void onFailure(Throwable t, Response response) {
 * System.err.println(response.message()); }
 * 
 * @Override public void onClose(int code, String reason) {
 * System.out.println(reason); } }
 */
