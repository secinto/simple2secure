package com.simple2secure.portal.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.S2SDSL;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SUTUtils extends BaseServiceProvider {
	
	public List<String> getSUTTypes(){
		String[] sutTypeArray = {"{ldc.sut}", "{sdc.sut}"};
		return Arrays.asList(sutTypeArray);
	}
	
	public <T> Map<String, Class<T>> getAnnotatedClassesMap(){
		Reflections reflections = new Reflections("com.simple2secure.api.model");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(S2SDSL.class);
		Map<String, Class<T>> resultMap = new HashedMap();
		for(Class clazz : annotated) {
			S2SDSL annotation = (S2SDSL) clazz.getAnnotation(S2SDSL.class);
			String annotationString = annotation.value();
			resultMap.put("{" + annotationString + "}", clazz);
		}
		return resultMap;
	}
}
