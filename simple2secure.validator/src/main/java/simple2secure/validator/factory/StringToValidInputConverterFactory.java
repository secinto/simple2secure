package simple2secure.validator.factory;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import simple2secure.validator.model.ValidInputDestGroup;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputEmailConfig;
import simple2secure.validator.model.ValidInputGroup;
import simple2secure.validator.model.ValidInputHostname;
import simple2secure.validator.model.ValidInputLicensePlan;
import simple2secure.validator.model.ValidInputOsinfo;
import simple2secure.validator.model.ValidInputPage;
import simple2secure.validator.model.ValidInputProcessor;
import simple2secure.validator.model.ValidInputQuery;
import simple2secure.validator.model.ValidInputReport;
import simple2secure.validator.model.ValidInputRule;
import simple2secure.validator.model.ValidInputSearchQuery;
import simple2secure.validator.model.ValidInputSequence;
import simple2secure.validator.model.ValidInputSize;
import simple2secure.validator.model.ValidInputStep;
import simple2secure.validator.model.ValidInputSut;
import simple2secure.validator.model.ValidInputTest;
import simple2secure.validator.model.ValidInputTestMacro;
import simple2secure.validator.model.ValidInputTestResult;
import simple2secure.validator.model.ValidInputTestRun;
import simple2secure.validator.model.ValidInputToken;
import simple2secure.validator.model.ValidInputWidget;
import simple2secure.validator.model.ValidInputWidgetProp;
import simple2secure.validator.model.ValidatedInput;

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
			} else if (this.targetClass == ValidInputSearchQuery.class) {
				return (T) new ValidInputSearchQuery(source);
			} else if (this.targetClass == ValidInputLicensePlan.class) {
				return (T) new ValidInputLicensePlan(source);
			} else if (this.targetClass == ValidInputTestMacro.class) {
				return (T) new ValidInputTestMacro(source);
			} else if (this.targetClass == ValidInputStep.class) {
				return (T) new ValidInputStep(source);
			} else if (this.targetClass == ValidInputSut.class) {
				return (T) new ValidInputSut(source);
			} else if (this.targetClass == ValidInputTest.class) {
				return (T) new ValidInputTest(source);
			} else if (this.targetClass == ValidInputTestResult.class) {
				return (T) new ValidInputTestResult(source);
			} else if (this.targetClass == ValidInputTestRun.class) {
				return (T) new ValidInputTestRun(source);
			} else if (this.targetClass == ValidInputSequence.class) {
				return (T) new ValidInputSequence(source);
			} else if (this.targetClass == ValidInputToken.class) {
				return (T) new ValidInputToken(source);
			} else if (this.targetClass == ValidInputWidget.class) {
				return (T) new ValidInputWidget(source);
			} else if (this.targetClass == ValidInputWidgetProp.class) {
				return (T) new ValidInputWidgetProp(source);
			} else if (this.targetClass == ValidInputOsinfo.class) {
				return (T) new ValidInputOsinfo(source);
			} else {
				return null;
			}
		}
	}
}
