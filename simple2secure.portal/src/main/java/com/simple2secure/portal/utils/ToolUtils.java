package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.simple2secure.api.dto.TestDTO;
import com.simple2secure.api.dto.ToolDTO;
import com.simple2secure.api.model.Command;
import com.simple2secure.api.model.TestCase;
import com.simple2secure.api.model.TestCaseTemplate;
import com.simple2secure.api.model.Tool;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestTemplateRepository;
import com.simple2secure.portal.repository.ToolRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
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
	TestTemplateRepository testTemplateRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	TestResultRepository testResultRepository;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	TestUtils testUtils;

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
	public ResponseEntity<List<ToolDTO>> getKubernetesTools(String locale, String contextId) {
		CoreV1Api api = new CoreV1Api();
		try {
			// Retrieve the list from the pods from the provided namespace
			V1PodList list = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
			if (list != null) {
				for (V1Pod pod : list.getItems()) {
					// If tool does not exist in the database add new entry
					if (!checkIfToolExistsInTheDatabase(pod.getMetadata().getName(), contextId)) {
						addNewToolToTheDatabase(pod, contextId);
					}
				}
				// Return all tool from the repository
				List<ToolDTO> tools = getToolsAndGenerateDTOs(contextId);
				if (tools != null) {
					log.debug("Found {} tools in the database", tools.size());
					return new ResponseEntity<List<ToolDTO>>(tools, HttpStatus.OK);
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
	 * This function generates a ToolDTO object for the current context. Object contains all tools, tests pro tool and test results pro test
	 * for the specified context
	 *
	 * @param contextId
	 * @return
	 */
	public List<ToolDTO> getToolsAndGenerateDTOs(String contextId) {
		List<ToolDTO> toolDTOList = new ArrayList<>();

		List<Tool> tools = toolRepository.getToolsByContextId(contextId);

		if (tools != null) {
			for (Tool tool : tools) {

				// Retrieve all test templates for this tool
				List<TestCaseTemplate> templates = testTemplateRepository.getByToolId(tool.getId());
				ToolDTO toolDto = new ToolDTO(tool, templates);

				// Get all tests by this toolId
				List<TestCase> tests = testRepository.getByToolId(tool.getId());

				if (tests != null) {
					List<TestDTO> testDtoList = new ArrayList<>();
					for (TestCase test : tests) {
						if (test != null) {

							// Create new TestDTO object with test and nested testResult objects
							TestDTO testDTO = new TestDTO(test, testUtils.getAllTestResultsByTestId(test));
							testDtoList.add(testDTO);
						}
					}
					toolDto.setTests(testDtoList);
				}
				// Add toolDTO object to the list
				toolDTOList.add(toolDto);
			}
		}

		return toolDTOList;
	}

	/**
	 * This function checks if the tool with the provided name exists in the database
	 *
	 * @param toolName
	 * @return
	 */
	private boolean checkIfToolExistsInTheDatabase(String toolName, String contextId) {
		Tool tool = toolRepository.getToolByNameAndContextId(toolName, contextId);
		if (tool == null) {
			return false;
		}
		return true;
	}

	/**
	 * This function adds new tool with default test template to the database
	 *
	 * @param pod
	 */
	private void addNewToolToTheDatabase(V1Pod pod, String contextId) {

		Tool tool = new Tool(pod.getMetadata().getName(), pod.getMetadata().getName(), contextId, true);
		ObjectId toolId = toolRepository.saveAndReturnId(tool);

		// Create default test/template for the tool
		// TODO: check - how to add default command
		List<Command> commands = new ArrayList<Command>();
		commands.add(new Command("nmap"));
		TestCaseTemplate testTemplate = new TestCaseTemplate("nmap_simple_test", toolId.toString(), commands);
		testTemplateRepository.save(testTemplate);
	}

}
