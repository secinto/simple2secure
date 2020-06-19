package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.simple2secure.portal.providers.BaseServiceProvider;

@Component
public class SUTUtils extends BaseServiceProvider {
	static final String METADATA_FLAG = "USE_SUT_METADATA";

	/**
	 * This method retrieves a list of keys from a string containing the USE_SUT_METADATA flag.
	 *
	 * @param sutMetadataKeyString
	 *          The string with the USE_SUT_METADATA flag containing the keys
	 * @return A list of strings(keys) obtained from the flag.
	 */
	public List<String> getSutMetadataKeys(String sutMetadataKeyString) {
		List<String> result = new ArrayList<>();
		if (sutMetadataKeyString.startsWith(METADATA_FLAG)) {
			String sanitizedValue = sutMetadataKeyString.substring(METADATA_FLAG.length() + 1, sutMetadataKeyString.length() - 1);
			List<String> keyList = Arrays.asList(sanitizedValue.split(","));
			for (String key : keyList) {
				result.add(key.trim());
			}
		}
		return result;
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
