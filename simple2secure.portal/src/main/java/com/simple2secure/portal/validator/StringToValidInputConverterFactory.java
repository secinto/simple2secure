package com.simple2secure.portal.validator;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.simple2secure.api.model.validation.ValidInputDestGroup;
import com.simple2secure.api.model.validation.ValidInputDevice;
import com.simple2secure.api.model.validation.ValidInputEmailConfig;
import com.simple2secure.api.model.validation.ValidInputGroup;
import com.simple2secure.api.model.validation.ValidInputHostname;
import com.simple2secure.api.model.validation.ValidInputPage;
import com.simple2secure.api.model.validation.ValidInputProcessor;
import com.simple2secure.api.model.validation.ValidInputQuery;
import com.simple2secure.api.model.validation.ValidInputReport;
import com.simple2secure.api.model.validation.ValidInputRule;
import com.simple2secure.api.model.validation.ValidInputSize;
import com.simple2secure.api.model.validation.ValidatedInput;

public class StringToValidInputConverterFactory implements ConverterFactory<String, ValidatedInput<?>> {

	@Override
	public <T extends ValidatedInput<?>> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToValidInputConverter<>(targetType);
	}

	private static class StringToValidInputConverter<T extends ValidatedInput<?>> implements Converter<String, T> {

		private Class<T> targetClass;

		public StringToValidInputConverter(Class<T> targetClass) {
			this.targetClass = targetClass;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T convert(String source) {
			if (this.targetClass == ValidInputSize.class) {
				int id = Integer.parseInt(source);
				return (T) new ValidInputSize(id);
			} else if (this.targetClass == ValidInputPage.class) {
				int id = Integer.parseInt(source);
				return (T) new ValidInputPage(id);
			} else if (this.targetClass == ValidInputDevice.class) {
				return (T) new ValidInputDevice(source);
			} else if (this.targetClass == ValidInputHostname.class) {
				return (T) new ValidInputHostname(source);
			} else if (this.targetClass == ValidInputEmailConfig.class) {
				return (T) new ValidInputEmailConfig(source);
			} else if (this.targetClass == ValidInputGroup.class) {
				return (T) new ValidInputGroup(source);
			} else if (this.targetClass == ValidInputDestGroup.class) {
				return (T) new ValidInputDestGroup(source);
			} else if (this.targetClass == ValidInputProcessor.class) {
				return (T) new ValidInputProcessor(source);
			} else if (this.targetClass == ValidInputQuery.class) {
				return (T) new ValidInputQuery(source);
			} else if (this.targetClass == ValidInputReport.class) {
				return (T) new ValidInputReport(source);
			} else if (this.targetClass == ValidInputRule.class) {
				return (T) new ValidInputRule(source);
			} else {
				return null;
			}
		}
	}
}
