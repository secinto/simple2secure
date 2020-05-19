package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.simple2secure.portal.providers.BaseServiceProvider;

@Component
public class SUTUtils extends BaseServiceProvider {
	static final String METADATA_FLAG = "USE_SUT_METADATA";

	public List<String> getSutMetadataKeys(String sutMetadataKeyString) {
		List<String> result = new ArrayList<>();
		if (sutMetadataKeyString.startsWith(METADATA_FLAG)) {
			String sanitizedValue = sutMetadataKeyString.substring(METADATA_FLAG.length() + 1, sutMetadataKeyString.length() - 1);
			result = Arrays.asList(sanitizedValue.split(","));
		}
		return result;
	}

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
