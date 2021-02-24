package com.simple2secure.portal.utils;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.Parameter;
import com.simple2secure.api.model.TestContent;
import com.simple2secure.api.model.TestInputData;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.providers.BaseServiceProvider;

@Component
public class InputDataUtils extends BaseServiceProvider {

	@Autowired
	TestUtils testUtils;

	/**
	 * This function checks which parameter contains the USE_INPUT_DATA_TAG and replaces it with the provided data content.
	 *
	 * @param testContentString
	 * @param inputData
	 * @return
	 */
	public String mergeDataInputWithContent(String testContentString, TestInputData inputData) {
		TestContent testContent = testUtils.getTestContent(testContentString);

		for (Parameter param : testContent.getTest_definition().getStep().getCommand().getParameter()) {
			if (param.getValue().equals(StaticConfigItems.USE_INPUT_DATA_FLAG)) {
				param.setValue(inputData.getData());
			}
		}

		return testUtils.getJsonStringFromTestContent(testContent);
	}

	/**
	 * This function clones input data after cloning some test
	 *
	 * @param testId
	 * @param clonedTestId
	 */
	public void cloneInputDataByTestId(ObjectId testId, ObjectId clonedTestId) {
		List<TestInputData> inputDataList = testInputDataRepository.getByTestId(testId);

		if (inputDataList != null) {
			for (TestInputData inputData : inputDataList) {
				inputData.setTestId(clonedTestId);
				inputData.setId(null);
				testInputDataRepository.save(inputData);
			}
		}
	}

	/**
	 * This function delete all input data according to the testId
	 * 
	 * @param testId
	 */
	public void deleteInputDataByTestId(ObjectId testId) {
		List<TestInputData> inputDataList = testInputDataRepository.getByTestId(testId);

		if (inputDataList != null) {
			for (TestInputData inputData : inputDataList) {
				testInputDataRepository.delete(inputData);
			}
		}
	}
}
