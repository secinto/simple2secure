package com.simple2secure.portal.validation;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.simple2secure.portal.validation.model.ValidInputDestGroup;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputDeviceType;
import com.simple2secure.portal.validation.model.ValidInputEmailConfig;
import com.simple2secure.portal.validation.model.ValidInputGroup;
import com.simple2secure.portal.validation.model.ValidInputHostname;
import com.simple2secure.portal.validation.model.ValidInputLicensePlan;
import com.simple2secure.portal.validation.model.ValidInputName;
import com.simple2secure.portal.validation.model.ValidInputOsinfo;
import com.simple2secure.portal.validation.model.ValidInputPage;
import com.simple2secure.portal.validation.model.ValidInputProcessor;
import com.simple2secure.portal.validation.model.ValidInputQuery;
import com.simple2secure.portal.validation.model.ValidInputReport;
import com.simple2secure.portal.validation.model.ValidInputRule;
import com.simple2secure.portal.validation.model.ValidInputSearchQuery;
import com.simple2secure.portal.validation.model.ValidInputSequence;
import com.simple2secure.portal.validation.model.ValidInputSize;
import com.simple2secure.portal.validation.model.ValidInputStep;
import com.simple2secure.portal.validation.model.ValidInputSut;
import com.simple2secure.portal.validation.model.ValidInputTest;
import com.simple2secure.portal.validation.model.ValidInputTestMacro;
import com.simple2secure.portal.validation.model.ValidInputTestResult;
import com.simple2secure.portal.validation.model.ValidInputTestRun;
import com.simple2secure.portal.validation.model.ValidInputToken;
import com.simple2secure.portal.validation.model.ValidInputWidget;
import com.simple2secure.portal.validation.model.ValidInputWidgetLocation;
import com.simple2secure.portal.validation.model.ValidInputWidgetProp;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.model.ValidatedInput;

@Slf4j
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
				return (T) new ValidInputSize().validatePathVariable(Integer.parseInt(source));
			} else if (this.targetClass == ValidInputPage.class) {
				return (T) new ValidInputPage().validatePathVariable(Integer.parseInt(source));
			} else if (this.targetClass == ValidInputDevice.class) {
				return (T) new ValidInputDevice().validatePathVariable(source);
			} else if (this.targetClass == ValidInputHostname.class) {
				return (T) new ValidInputHostname().validatePathVariable(source);
			} else if (this.targetClass == ValidInputEmailConfig.class) {
				return (T) new ValidInputEmailConfig().validatePathVariable(source);
			} else if (this.targetClass == ValidInputGroup.class) {
				return (T) new ValidInputGroup().validatePathVariable(source);
			} else if (this.targetClass == ValidInputDestGroup.class) {
				return (T) new ValidInputDestGroup().validatePathVariable(source);
			} else if (this.targetClass == ValidInputProcessor.class) {
				return (T) new ValidInputProcessor().validatePathVariable(source);
			} else if (this.targetClass == ValidInputQuery.class) {
				return (T) new ValidInputQuery().validatePathVariable(source);
			} else if (this.targetClass == ValidInputReport.class) {
				return (T) new ValidInputReport().validatePathVariable(source);
			} else if (this.targetClass == ValidInputRule.class) {
				return (T) new ValidInputRule().validatePathVariable(source);
			} else if (this.targetClass == ValidInputSearchQuery.class) {
				return (T) new ValidInputSearchQuery().validatePathVariable(source);
			} else if (this.targetClass == ValidInputLicensePlan.class) {
				return (T) new ValidInputLicensePlan().validatePathVariable(source);
			} else if (this.targetClass == ValidInputTestMacro.class) {
				return (T) new ValidInputTestMacro().validatePathVariable(source);
			} else if (this.targetClass == ValidInputStep.class) {
				return (T) new ValidInputStep().validatePathVariable(source);
			} else if (this.targetClass == ValidInputSut.class) {
				return (T) new ValidInputSut().validatePathVariable(source);
			} else if (this.targetClass == ValidInputTest.class) {
				return (T) new ValidInputTest().validatePathVariable(source);
			} else if (this.targetClass == ValidInputTestResult.class) {
				return (T) new ValidInputTestResult().validatePathVariable(source);
			} else if (this.targetClass == ValidInputTestRun.class) {
				return (T) new ValidInputTestRun().validatePathVariable(source);
			} else if (this.targetClass == ValidInputSequence.class) {
				return (T) new ValidInputSequence().validatePathVariable(source);
			} else if (this.targetClass == ValidInputToken.class) {
				return (T) new ValidInputToken().validatePathVariable(source);
			} else if (this.targetClass == ValidInputWidget.class) {
				return (T) new ValidInputWidget().validatePathVariable(source);
			} else if (this.targetClass == ValidInputWidgetProp.class) {
				return (T) new ValidInputWidgetProp().validatePathVariable(source);
			} else if (this.targetClass == ValidInputWidgetLocation.class) {
				return (T) new ValidInputWidgetLocation().validatePathVariable(source);
			} else if (this.targetClass == ValidInputOsinfo.class) {
				return (T) new ValidInputOsinfo().validatePathVariable(source);
			} else if (this.targetClass == ValidInputName.class) {
				return (T) new ValidInputName().validatePathVariable(source);
			} else if (this.targetClass == ValidInputDeviceType.class) {
				return (T) new ValidInputDeviceType().validatePathVariable(source);
			} else {
				return null;
			}
		}
	}
}
