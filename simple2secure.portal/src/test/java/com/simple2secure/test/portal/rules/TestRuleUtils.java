package com.simple2secure.test.portal.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.RuleParam;
import com.simple2secure.api.model.RuleParamArray;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.utils.RuleUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestRuleUtils {
	
	@Autowired
	private RuleUtils ruleUtils;
	
	private boolean compareRuleParam(RuleParam<?> p1, RuleParam<?> p2)
	{
		if(p1.getType() != p2.getType())
			return false;
		
		if(! p1.getName().equals(p2.getName()))
			return false;	
		
		if(! p1.getDescription_de().equals(p2.getDescription_de()))
			return false;	
		
		if(! p1.getDescription_en().equals(p2.getDescription_en()))
			return false;	
		
		if(p1.getValue() != p2.getValue())
			return false;			
		
		return true;
	}
	
	
	private boolean compareRuleParamArrays(RuleParamArray<?> p1, RuleParamArray<?> p2)
	{
		if(p1.getType() != p2.getType())
			return false;
		
		if(! p1.getName().equals(p2.getName()))
			return false;	
		
		if(! p1.getDescription_de().equals(p2.getDescription_de()))
			return false;	
		
		if(! p1.getDescription_en().equals(p2.getDescription_en()))
			return false;	
		
		if(p1.getValues() != p2.getValues())
			return false;			
		
		return true;
	}
	
	
	/**
	 * Testing Method to load predefined action for the rule engine and save 
	 * data to a TemplateAction object
	 */
	@Test
	public void testFetchingTemplateActions()
	{		
		// Path where the action classes are saved
		String PACKAGE_TEMPLATE_ACTIONS = "com.simple2secure.test.portal.rules";
		
		try {
			Collection<TemplateAction> templateActionData = 
					ruleUtils.loadTemplateActions(PACKAGE_TEMPLATE_ACTIONS);
			
			assertEquals(1, templateActionData.size(), "there should only be one TemplateAction provided for testing");
			
			templateActionData.forEach(action -> {
				
				// Checking class annotation
				assertEquals("test action", action.getName(), "Field name from the Annotation \"AnnotationAction\"n is wrong");
				assertEquals("action description_de", action.getDescription_de(), "Field description_de from the Annotation \"AnnotationAction\"n is wrong");
				assertEquals("action description_en", action.getDescription_en(), "Field description_en from the Annotation \"AnnotationAction\"n is wrong");
				
				
				//Checking field Annotation (RuleParam)
				assertEquals(3, action.getParams().size(), "Number of RuleParams has not been read proply");
				
				RuleParam<?> ruleParam = new RuleParam<Integer>("varInt", "varInt description_en", "varInt description_de", null, DataType._INT);
				
				assertTrue(
						compareRuleParam(action.getParams().get(0), ruleParam),
						"(First) RuleParam has not been read properly from Annotation \"AnnotationRuleParam\"");
				
				ruleParam = new RuleParam<Double>("varDouble", "varDouble description_en", "varDouble description_de", null, DataType._DOUBLE);
				
				assertTrue(
						compareRuleParam(action.getParams().get(1), ruleParam),
						"(Second) RuleParam has not been read properly from Annotation \"AnnotationRuleParam\"");
				
				ruleParam = new RuleParam<String>("varString", "varString description_en", "varString description_de", null, DataType._STRING);
				
				assertTrue(
						compareRuleParam(action.getParams().get(2), ruleParam),
						"(third) RuleParam has not been read properly from Annotation \"AnnotationRuleParam\"");
				
				
				//Checking field Annotation (RuleParamArray)
				assertEquals(3, action.getParamArrays().size(), "Number of RuleParamArrays has not been read proply");
				
				RuleParamArray<?> ruleParamArray = new RuleParamArray<Integer>("arrayInt", "arrayInt description_en", "arrayInt description_de", null, DataType._INT);
				
				assertTrue(
						compareRuleParamArrays(action.getParamArrays().get(0), ruleParamArray),
						"(First) RuleParamArray has not been read properly from Annotation \"AnnotationRuleParamArray\"");
				
				ruleParamArray = new RuleParamArray<Double>("arrayDouble", "arrayDouble description_en", "arrayDouble description_de", null, DataType._DOUBLE);
				
				assertTrue(
						compareRuleParamArrays(action.getParamArrays().get(1), ruleParamArray),
						"(Second) RuleParamArray has not been read properly from Annotation \"AnnotationRuleParamArray\"");
				
				ruleParamArray = new RuleParamArray<String>("arrayString", "arrayString description_en", "arrayString description_de", null, DataType._STRING);
				
				assertTrue(
						compareRuleParamArrays(action.getParamArrays().get(2), ruleParamArray),
						"(third) RuleParamArray has not been read properly from Annotation \"AnnotationRuleParamArray\"");

			});
			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Testing Method to load predefined condition for the rule engine and save 
	 * data to a TemplateAction object
	 */
	@Test
	public void testFetchingTemplateConditions()
	{
		// Path where the condition classes are saved
		String PACKAGE_TEMPLATE_Conditions = "com.simple2secure.test.portal.rules";
		
		try {
			Collection<TemplateCondition> templateCondtionData = 
					ruleUtils.loadTemplateConditions(PACKAGE_TEMPLATE_Conditions);
			
			assertEquals(1, templateCondtionData.size(), "there should only be one TemplateCondition provided for testing");
			
			templateCondtionData.forEach(condition -> {
				
				// Checking class annotation
				assertEquals("test condition", condition.getName(), "Field name from the Annotation \"AnnotationAction\"n is wrong");
				assertEquals("action description_de", condition.getDescription_de(), "Field description_de from the Annotation \"AnnotationAction\"n is wrong");
				assertEquals("action description_en", condition.getDescription_en(), "Field description_en from the Annotation \"AnnotationAction\"n is wrong");
				
				
				//Checking field Annotation (RuleParam)
				assertEquals(3, condition.getParams().size(), "Number of RuleParams has not been read proply");
				
				RuleParam<?> ruleParam = new RuleParam<Integer>("varInt", "varInt description_en", "varInt description_de", null, DataType._INT);
				
				assertTrue(
						compareRuleParam(condition.getParams().get(0), ruleParam),
						"(First) RuleParam has not been read properly from Annotation \"AnnotationRuleParam\"");
				
				ruleParam = new RuleParam<Double>("varDouble", "varDouble description_en", "varDouble description_de", null, DataType._DOUBLE);
				
				assertTrue(
						compareRuleParam(condition.getParams().get(1), ruleParam),
						"(Second) RuleParam has not been read properly from Annotation \"AnnotationRuleParam\"");
				
				ruleParam = new RuleParam<String>("varString", "varString description_en", "varString description_de", null, DataType._STRING);
				
				assertTrue(
						compareRuleParam(condition.getParams().get(2), ruleParam),
						"(third) RuleParam has not been read properly from Annotation \"AnnotationRuleParam\"");
				
				
				//Checking field Annotation (RuleParamArray)
				assertEquals(3, condition.getParamArrays().size(), "Number of RuleParamArrays has not been read proply");
				
				RuleParamArray<?> ruleParamArray = new RuleParamArray<Integer>("arrayInt", "arrayInt description_en", "arrayInt description_de", null, DataType._INT);
				
				assertTrue(
						compareRuleParamArrays(condition.getParamArrays().get(0), ruleParamArray),
						"(First) RuleParamArray has not been read properly from Annotation \"AnnotationRuleParamArray\"");
				
				ruleParamArray = new RuleParamArray<Double>("arrayDouble", "arrayDouble description_en", "arrayDouble description_de", null, DataType._DOUBLE);
				
				assertTrue(
						compareRuleParamArrays(condition.getParamArrays().get(1), ruleParamArray),
						"(Second) RuleParamArray has not been read properly from Annotation \"AnnotationRuleParamArray\"");
				
				ruleParamArray = new RuleParamArray<String>("arrayString", "arrayString description_en", "arrayString description_de", null, DataType._STRING);
				
				assertTrue(
						compareRuleParamArrays(condition.getParamArrays().get(2), ruleParamArray),
						"(third) RuleParamArray has not been read properly from Annotation \"AnnotationRuleParamArray\"");

			});
			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Testing Method to load data from TemplateAction into a predefined Action
	 * class and create an Object.
	 */
	@Test
	public void testCreatingActionObjectFromActionData()
	{
		// Path where the action classes are saved
		String PACKAGE_TEMPLATE_ACTIONS = "com.simple2secure.test.portal.rules";
		
		
		// preparing an TemplateAction object like the user would do it at the 
		// web interface
		List<RuleParam<?>> ruleParams = new ArrayList<RuleParam<?>>();
		
		RuleParam<Integer> paramInt = new RuleParam<Integer>();
		paramInt.setName("varInt");
		paramInt.setValue(42);
		
		ruleParams.add(paramInt);
		
		RuleParam<Double> paramDouble = new RuleParam<Double>();
		paramDouble.setName("varDouble");
		paramDouble.setValue(27.03);
		
		ruleParams.add(paramDouble);
		
		RuleParam<String> paramString = new RuleParam<String>();
		paramString.setName("varString");
		paramString.setValue("some text");
		
		ruleParams.add(paramString);
		
		List<RuleParamArray<?>> ruleParamArrays =
				new ArrayList<RuleParamArray<?>>();
		
		RuleParamArray<Integer> arrayInt = new RuleParamArray<Integer>();
		arrayInt.setName("arrayInt");
		arrayInt.setValues(new ArrayList<Integer>(){
			{add(1); add(2); add(3);}});
		
		ruleParamArrays.add(arrayInt);
		
		RuleParamArray<Double> arrayDouble = new RuleParamArray<Double>();
		arrayDouble.setName("arrayDouble");
		arrayDouble.setValues(new ArrayList<Double>() {
			{add(1.1); add(2.2); add(3.3);}});
		
		ruleParamArrays.add(arrayDouble);
		
		RuleParamArray<String> arrayString = new RuleParamArray<String>();
		arrayString.setName("arrayString");
		arrayString.setValues(new ArrayList<String>() {
			{add("string1"); add("string2"); add("string3");}});
		
		ruleParamArrays.add(arrayString);
		
		TemplateAction templateAction = new TemplateAction(
				"test action", "action description_en", "action description_de",
				ruleParams, ruleParamArrays);
		
		
		try {
			
			// creating an  specific action object from the prepared data
			TestTemplateAction action = (TestTemplateAction) ruleUtils.
					buildActionFromTemplateAction(templateAction,
							PACKAGE_TEMPLATE_ACTIONS);
			
			// looking if everything has been injected properly
			assertEquals(42, action.getVarInt(), "verInt has not been injected right");
			assertEquals(27.03, action.getVarDouble(), "verDouble has not been injected right");
			assertEquals("some text", action.getVarString(), "varString has not been injected right");
			assertEquals(new ArrayList<Integer>(){{add(1); add(2); add(3);}},action.arrayInt, "arrayInt has not been injected right");
			assertEquals(new ArrayList<Double>() {{add(1.1); add(2.2); add(3.3);}},action.arrayDouble, "arrayDouble has not been injected right");
			assertEquals(new ArrayList<String>(){{add("string1"); add("string2"); add("string3");}},action.arrayString, "arrayString has not been injected right");
			
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | ClassNotFoundException
				| IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Testing Method to load data from TemplateCondition into a predefined
	 *  Condition class and create an Object.
	 */
	@Test
	public void testCreatingConditionObjectFromConditionData()
	{
		// Path where the condition classes are saved
		String PACKAGE_TEMPLATE_CONDITIONS = "com.simple2secure.test.portal.rules";
		
		
		// preparing an TemplateCondition object like the user would do it at
		// the web interface
		List<RuleParam<?>> ruleParams = new ArrayList<RuleParam<?>>();
		
		RuleParam<Integer> paramInt = new RuleParam<Integer>();
		paramInt.setName("varInt");
		paramInt.setValue(42);
		
		ruleParams.add(paramInt);
		
		RuleParam<Double> paramDouble = new RuleParam<Double>();
		paramDouble.setName("varDouble");
		paramDouble.setValue(27.03);
		
		ruleParams.add(paramDouble);
		
		RuleParam<String> paramString = new RuleParam<String>();
		paramString.setName("varString");
		paramString.setValue("some text");
		
		ruleParams.add(paramString);
		
		List<RuleParamArray<?>> ruleParamArrays =
				new ArrayList<RuleParamArray<?>>();
		
		RuleParamArray<Integer> arrayInt = new RuleParamArray<Integer>();
		arrayInt.setName("arrayInt");
		arrayInt.setValues(new ArrayList<Integer>(){
			{add(1); add(2); add(3);}});
		
		ruleParamArrays.add(arrayInt);
		
		RuleParamArray<Double> arrayDouble = new RuleParamArray<Double>();
		arrayDouble.setName("arrayDouble");
		arrayDouble.setValues(new ArrayList<Double>() {
			{add(1.1); add(2.2); add(3.3);}});
		
		ruleParamArrays.add(arrayDouble);
		
		RuleParamArray<String> arrayString = new RuleParamArray<String>();
		arrayString.setName("arrayString");
		arrayString.setValues(new ArrayList<String>() {
			{add("string1"); add("string2"); add("string3");}});
		
		ruleParamArrays.add(arrayString);
		
		TemplateCondition templateCondition = new TemplateCondition(
				"test condition", "action description_en", "action description_de",
				ruleParams, ruleParamArrays);
		
		try {
			// creating a specific condition object from the prepared data
			TestTemplateCondition condition = (TestTemplateCondition) ruleUtils.
					buildConditionFromTemplateCondition(templateCondition,
							PACKAGE_TEMPLATE_CONDITIONS);
			
			// looking if everything has been injected properly
			assertEquals(42, condition.getVarInt(), "verInt has not been injected right");
			assertEquals(27.03, condition.getVarDouble(), "verDouble has not been injected right");
			assertEquals("some text", condition.getVarString(), "varString has not been injected right");
			assertEquals(new ArrayList<Integer>(){{add(1); add(2); add(3);}},condition.arrayInt, "arrayInt has not been injected right");
			assertEquals(new ArrayList<Double>() {{add(1.1); add(2.2); add(3.3);}},condition.arrayDouble, "arrayDouble has not been injected right");
			assertEquals(new ArrayList<String>(){{add("string1"); add("string2"); add("string3");}},condition.arrayString, "arrayString has not been injected right");
			
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | ClassNotFoundException
				| IOException e) {
			e.printStackTrace();
		}
	}
}
