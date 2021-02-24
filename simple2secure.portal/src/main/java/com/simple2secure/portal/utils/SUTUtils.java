package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Parameter;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.api.model.TestContent;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SUTUtils extends BaseServiceProvider {

	@Autowired
	TestUtils testUtils;

	/**
	 * This method retrieves a list of keys from a string containing the USE_SUT_METADATA flag.
	 *
	 * @param sutMetadataKeyString
	 *          The string with the USE_SUT_METADATA flag containing the keys
	 * @return A list of strings(keys) obtained from the flag.
	 */
	public List<String> getSutMetadataKeys(List<Parameter> parameters) {
		List<String> result = new ArrayList<>();

		for (Parameter param : parameters) {

			if (!Strings.isNullOrEmpty(param.getValue())) {
				if (param.getValue().startsWith(StaticConfigItems.SUT_METADATA_FLAG)) {
					String sanitizedValue = param.getValue().substring(StaticConfigItems.SUT_METADATA_FLAG.length() + 1,
							param.getValue().length() - 1);
					List<String> keyList = Arrays.asList(sanitizedValue.split(","));
					for (String key : keyList) {
						result.add(key.trim());
					}
				}
			}
		}

		return result;
	}

	/**
	 * This functions extracts all suts which are applicable for the provided test
	 *
	 * @param test
	 * @return
	 */
	public List<SystemUnderTest> getSutListForTest(TestObjWeb test) {
		List<SystemUnderTest> sutList = new ArrayList<>();
		List<String> sutMetadataKeys = getSutMetadataKeys(test.getTest_content().getTest_definition().getStep().getCommand().getParameter());
		List<String> sanitizedSutMetadataKeys = sanitizedSutMetadataKeyList(sutMetadataKeys);
		if (!sanitizedSutMetadataKeys.isEmpty()) {
			sutList = sutRepository.getApplicableSystemUnderTests(sanitizedSutMetadataKeys);
		}
		return sutList;
	}

	/**
	 * This function merges the test content with the selected SUT before test run.
	 *
	 * @param test
	 * @param sutId
	 * @return
	 */
	public String mergeTestContentWithSut(String testContentString, ObjectId sutId) {
		TestContent testContent = testUtils.getTestContent(testContentString);
		SystemUnderTest sut = sutRepository.find(sutId);
		List<String> sutMetadataKeys = getSutMetadataKeys(testContent.getTest_definition().getStep().getCommand().getParameter());
		String testContentJsonString = testUtils.mergeTestAndSutMetadata(testContent, sut, sutMetadataKeys);
		return testContentJsonString;
	}

	/**
	 * This function imports the provided Suts
	 *
	 * @param sutList
	 * @return
	 */
	public List<SystemUnderTest> importSuts(List<SystemUnderTest> sutList, ObjectId contextId) {
		for (SystemUnderTest sut : sutList) {
			sut.setId(null);
			sut.setContextId(contextId);
			sutRepository.save(sut);

			log.debug("System Under Test: {} has been saved", sut.getName());
		}
		return sutList;
	}

	/**
	 * This method retrieves a sanitized list of keys from a list of keys from the USE_SUT_METADATA flag.
	 *
	 * @param sutMetaKeyList
	 *          The list of strings(keys) from the USE_SUT_METADATA flag to be sanitized
	 * @return A list of sanitized strings(keys) obtained from the USE_SUT_METADATA flag.
	 */
	public List<String> sanitizedSutMetadataKeyList(List<String> sutMetaKeyList) {
		List<String> result = new ArrayList<>();
		List<String> specialCharsList = new ArrayList<String>() {
			{
				add("'");
				add("\"");
				add("\\");
				add(";");
				add("{");
				add("}");
				add("$");
			}
		};
		for (String key : sutMetaKeyList) {
			for (String specChar : specialCharsList) {
				if (key.contains(specChar)) {
					key = key.replace(specChar, "");
				}
			}
			result.add(key.trim());
		}
		return result;
	}
}
