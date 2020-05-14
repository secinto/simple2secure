package com.simple2secure.portal.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.LDCSystemUnderTest;
import com.simple2secure.api.model.S2SDSL;
import com.simple2secure.api.model.SDCSystemUnderTest;
import com.simple2secure.api.model.SystemUnderTest;
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
		Reflections reflections = new Reflections("com.simple2secure.api.model");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(S2SDSL.class);
		Map<String, Class<T>> resultMap = new HashedMap();
		for (Class clazz : annotated) {
			S2SDSL annotation = (S2SDSL) clazz.getAnnotation(S2SDSL.class);
			String annotationString = annotation.value();
			resultMap.put("{" + annotationString + "}", clazz);
		}
		return resultMap;
	}

	public SystemUnderTest updateSut(SystemUnderTest _updateSut) {
		SystemUnderTest result = null;
		if (_updateSut instanceof LDCSystemUnderTest) {
			LDCSystemUnderTest sutToUpdate = (LDCSystemUnderTest) sutRepository.find(_updateSut.id);
			LDCSystemUnderTest updateSut = (LDCSystemUnderTest) _updateSut;
			sutToUpdate.setContextId(updateSut.getContextId());
			sutToUpdate.setUri(updateSut.getUri());
			sutToUpdate.setName(updateSut.getName());
			sutToUpdate.setMetadata(updateSut.getMetadata());
			sutToUpdate.setIpAddress(updateSut.getIpAddress());
			sutToUpdate.setPort(updateSut.getPort());
			sutToUpdate.setProtocol(updateSut.getProtocol());
			result = sutToUpdate;
		} else if (_updateSut instanceof SDCSystemUnderTest) {
			SDCSystemUnderTest sutToUpdate = (SDCSystemUnderTest) sutRepository.find(_updateSut.id);
			SDCSystemUnderTest updateSut = (SDCSystemUnderTest) _updateSut;
			sutToUpdate.setContextId(updateSut.getContextId());
			sutToUpdate.setUri(updateSut.getUri());
			sutToUpdate.setName(updateSut.getName());
			sutToUpdate.setMetadata(updateSut.getMetadata());
			sutToUpdate.setPort(updateSut.getPort());
			sutToUpdate.setProtocol(updateSut.getProtocol());
			result = sutToUpdate;
		}
		return result;
	}
}
