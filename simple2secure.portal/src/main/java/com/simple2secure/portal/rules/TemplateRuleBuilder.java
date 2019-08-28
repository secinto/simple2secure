package com.simple2secure.portal.rules;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import com.google.common.reflect.ClassPath;
import com.simple2secure.api.model.ConditionParam;
import com.simple2secure.api.model.ConditionParamArray;
import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationConditionParam;
import com.simple2secure.commons.rules.annotations.AnnotationConditionParamArray;

@Service
public class TemplateRuleBuilder {
	
	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
	
	public Rule build(TemplateRule templateRule)
	{
		Condition condition;
		Action action;
		
		switch(templateRule.getConditionTemplate())
		{
		case "find_words":
			condition = new TemplateConditionFindWords(templateRule.getConditionParams());
			break;
			
		case "specific_domains":
			condition = new TemplateConditionSpecificDomains(templateRule.getConditionParams());
			break;
		    
		default: 
			condition = null;
		}
		
		switch(templateRule.getActionTemplate())
		{
		case "send_notification":
			action = new TemplateActionSendNotification(templateRule.getActionParams()[0]);
			break;
			
		case "send_email":
			action = new TemplateActionSendEmail(templateRule.getActionParams()[0], templateRule.getActionParams()[1]);
			break;
			
		default: 
			action = null;
		}
		
		
		autowireCapableBeanFactory.autowireBean(action);
		
		Rule rule = new RuleBuilder()
                .name(templateRule.getName())
                .description(templateRule.getDescription())
                .priority(Rule.DEFAULT_PRIORITY) // maybe add priority in TemplateRule model 
                .when(condition)
                .then(action)
                .build();
		return rule; 
	}
	
	public static Collection<TemplateCondition> loadTemplatesConditions(String path) throws IOException, ClassNotFoundException
	{
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Collection<TemplateCondition> conditions = new ArrayList<TemplateCondition>();
		
		ClassPath classPath = ClassPath.from(loader);
		
		for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(path))
		{
			Class clazz = Class.forName(info.getName(), true, loader);
			
			AnnotationCondition annotatioCondition = (AnnotationCondition) clazz.getAnnotation(AnnotationCondition.class);

			if(annotatioCondition != null)
			    {	
				    Collection<ConditionParam<?>> conditionParams = new ArrayList<ConditionParam<?>>();
				    Collection<ConditionParamArray<?>> conditionParamArrays = new ArrayList<ConditionParamArray<?>>();
				    
			    	Field fs[] = clazz.getDeclaredFields();

					for (Field f : fs) 
				    {
						f.setAccessible(true);

						AnnotationConditionParamArray annotationParamArray = (AnnotationConditionParamArray) f.getAnnotation(AnnotationConditionParamArray.class);
					    if(annotationParamArray != null)
					    {			
					    	ConditionParamArray<?> paramArray = null;
					    	switch(annotationParamArray.type())
					    	{
					    	case _int:
					    		paramArray = new ConditionParamArray<Integer>();
					    		paramArray.setType(DataType._int);
					    		break;
					    		
					    	case _double:
					    		paramArray = new ConditionParamArray<Double>();
					    		paramArray.setType(DataType._double);
					    		break;
					    		
					    	case _String:
					    		paramArray = new ConditionParamArray<Double>();
					    		paramArray.setType(DataType._String);
					    		break;
					    		
					    	default:
					    		break;
					    	}
					    	
					    	if(paramArray != null)
					    	{
					    		paramArray.setName(annotationParamArray.name());
						    	paramArray.setDescription_de(annotationParamArray.description_de());
						    	paramArray.setDescription_en(annotationParamArray.description_en());
						    	paramArray.setValues(null);
						    	conditionParamArrays.add(paramArray);
					    	}
					    	
					    	continue;
					    }
					    
					    AnnotationConditionParam annotationParam = (AnnotationConditionParam) f.getAnnotation(AnnotationConditionParam.class);
					    if(annotationParam != null)
					    {
							
					    	ConditionParam<?> param = null;
					    	switch(annotationParam.type())
					    	{
					    	case _int:
					    		param = new ConditionParam<Integer>();
					    		param.setType(DataType._int);
					    		break;
					    		
					    	case _double:
					    		param = new ConditionParam<Double>();
					    		param.setType(DataType._double);
					    		break;
					    		
					    	case _String:
					    		param = new ConditionParam<Double>();
					    		param.setType(DataType._String);
					    		break;
					    		
					    	default:
					    		break;
					    	}
					    	
					    	if(param != null)
					    	{
					    		param.setName(annotationParam.name());
						    	param.setDescription_de(annotationParam.description_de());
						    	param.setDescription_en(annotationParam.description_en());
						    	param.setValue(null);
						    	conditionParams.add(param);	
					    	}
					    	
					    	continue;
					   }					    
				    }
					
					TemplateCondition conditionObj = new TemplateCondition(
				    		annotatioCondition.name(), 
				    		annotatioCondition.description_en(), 
				    		annotatioCondition.description_de(),
				    		conditionParams, conditionParamArrays);
					
					conditions.add(conditionObj);
			    }			 
	    }
		return conditions;
	}

}
