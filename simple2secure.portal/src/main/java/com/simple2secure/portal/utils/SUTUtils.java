package com.simple2secure.portal.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SUTUtils extends BaseServiceProvider {

	public List<String> getSUTTypes() {
		String[] sutTypeArray = { "{ldc.sut}", "{sdc.sut}" };
		return Arrays.asList(sutTypeArray);
	}

	public String getSUTBase(String sutValue) {
		String sanitizedValue = sutValue.substring(1, sutValue.length() - 1);
		String[] splittedValue = sanitizedValue.split("\\.");
		if (!(splittedValue.length == 2)) {
			return "{" + splittedValue[0] + "." + splittedValue[1] + "}";
		}
		return sutValue;
	}

	public <T> Map<String, Class<T>> getAnnotatedClassesMap() {
		// Reflections reflections = new Reflections("com.simple2secure.api.model");
		// Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(S2SDSL.class);
		// Map<String, Class<T>> resultMap = new HashedMap();
		// for (Class clazz : annotated) {
		// S2SDSL annotation = (S2SDSL) clazz.getAnnotation(S2SDSL.class);
		// String annotationString = annotation.value();
		// resultMap.put("{" + annotationString + "}", clazz);
		// }
		// return resultMap;
		return null;
	}

}
