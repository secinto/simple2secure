/**
*********************************************************************
*   simple2secure is a cyber risk and information security platform.
*   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
*********************************************************************
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU Affero General Public License as
*   published by the Free Software Foundation, either version 3 of the
*   License, or (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*   GNU Affero General Public License for more details.
*
*   You should have received a copy of the GNU Affero General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>.
*  
 *********************************************************************
*/

package com.simple2secure.commons.rules.engine;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;

import com.google.common.reflect.ClassPath;
import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.RuleParam;
import com.simple2secure.api.model.RuleParamArray;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.rules.annotations.AnnotationAction;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;


public class RuleUtils {
	
	private static void saveParamsFromClass(Class clazz, Collection<RuleParam<?>> params, Collection<RuleParamArray<?>> paramArrays)
	{
		Field fs[] = clazz.getDeclaredFields();

		for (Field f : fs) 
	    {
			f.setAccessible(true);

			AnnotationRuleParamArray annotationParamArray = (AnnotationRuleParamArray) f.getAnnotation(AnnotationRuleParamArray.class);
		    if(annotationParamArray != null)
		    {			
		    	RuleParamArray<?> paramArray = null;
		    	switch(annotationParamArray.type())
		    	{
		    	case _int:
		    		paramArray = new RuleParamArray<Integer>();
		    		paramArray.setType(DataType._int);
		    		break;
		    		
		    	case _double:
		    		paramArray = new RuleParamArray<Double>();
		    		paramArray.setType(DataType._double);
		    		break;
		    		
		    	case _String:
		    		paramArray = new RuleParamArray<Double>();
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
			    	paramArrays.add(paramArray);
		    	}
		    	
		    	continue;
		    }
		    
		    AnnotationRuleParam annotationParam = (AnnotationRuleParam) f.getAnnotation(AnnotationRuleParam.class);
		    if(annotationParam != null)
		    {
				
		    	RuleParam<?> param = null;
		    	switch(annotationParam.type())
		    	{
		    	case _int:
		    		param = new RuleParam<Integer>();
		    		param.setType(DataType._int);
		    		break;
		    		
		    	case _double:
		    		param = new RuleParam<Double>();
		    		param.setType(DataType._double);
		    		break;
		    		
		    	case _String:
		    		param = new RuleParam<Double>();
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
			    	params.add(param);	
		    	}
		    	
		    	continue;
		   }					    
	    }
	}
	
	private static Collection<Class<?>> getAnnotatedClass(String path, Class annotation) throws IOException, ClassNotFoundException
	{
		Collection<Class<?>> clazzes = new ArrayList<Class<?>>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ClassPath classPath = ClassPath.from(loader);
		
		for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(path))
		{
			Class clazz = Class.forName(info.getName(), true, loader);

			if(clazz.getAnnotation(annotation) != null)
				clazzes.add(clazz);		 
	    }
		return clazzes;
	}
	
	public static Collection<TemplateCondition> loadTemplateConditions(String path) throws IOException, ClassNotFoundException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Collection<TemplateCondition> conditions = new ArrayList<TemplateCondition>();
		
		ClassPath classPath = ClassPath.from(loader);
		
		for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(path))
		{
			Class clazz = Class.forName(info.getName(), true, loader);
			
			AnnotationCondition annotationCondition = (AnnotationCondition) clazz.getAnnotation(AnnotationCondition.class);

			if(annotationCondition != null)
			    {	
				    Collection<RuleParam<?>> conditionParams = new ArrayList<RuleParam<?>>();
				    Collection<RuleParamArray<?>> conditionParamArrays = new ArrayList<RuleParamArray<?>>();
				    
				    saveParamsFromClass(clazz, conditionParams, conditionParamArrays);
					
					TemplateCondition conditionObj = new TemplateCondition(
				    		annotationCondition.name(), 
				    		annotationCondition.description_en(), 
				    		annotationCondition.description_de(),
				    		conditionParams, conditionParamArrays);
					
					conditions.add(conditionObj);
			    }			 
	    }
		return conditions;	
	}
	
	public static Collection<TemplateAction> loadTemplateActions(String path) throws IOException, ClassNotFoundException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Collection<TemplateAction> actions = new ArrayList<TemplateAction>();
		
		ClassPath classPath = ClassPath.from(loader);
		
		for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(path))
		{
			Class clazz = Class.forName(info.getName(), true, loader);
			
			AnnotationAction annotationAction = (AnnotationAction) clazz.getAnnotation(AnnotationAction.class);

			if(annotationAction != null)
			    {	
				    Collection<RuleParam<?>> actionParams = new ArrayList<RuleParam<?>>();
				    Collection<RuleParamArray<?>> actionParamArrays = new ArrayList<RuleParamArray<?>>();
				    
				    saveParamsFromClass(clazz, actionParams, actionParamArrays);
					
				    TemplateAction actionObj = new TemplateAction(
				    		annotationAction.name(), 
				    		annotationAction.description_en(), 
				    		annotationAction.description_de(),
				    		actionParams, actionParamArrays);
					
				    actions.add(actionObj);
			    }			 
	    }
		return actions;	
	}

	public static Condition buildConditionFromTemplateCondition(TemplateCondition conditionData, String path) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
	{
		Class<?> conditionClass = null;
		Condition condition = null;
		Collection<Class<?>> clazzes = getAnnotatedClass(path, AnnotationCondition.class);
		
		for(Class<?> clazz: clazzes)
		{
			if(clazz.getAnnotation(AnnotationCondition.class).name().equalsIgnoreCase(conditionData.getName()))
			{
				conditionClass = clazz;
				break;
			}
		}
		
		
		condition = (Condition) conditionClass.newInstance();
		
		Field fs[] = conditionClass.getDeclaredFields();
		
		for(Field f : fs)
		{
			f.setAccessible(true);
			AnnotationRuleParam param = f.getAnnotation(AnnotationRuleParam.class);
			
			if(param != null)
			{
				for(RuleParam<?> paramData : conditionData.getParams())
				{
					if(paramData.getName().equals(param.name()))
					{
						f.set(condition, paramData.getValue());
						break;
					}
				}
				continue;
			}
			
			AnnotationRuleParamArray paramArray = f.getAnnotation(AnnotationRuleParamArray.class);
			
			if(paramArray != null)
			{
				for(RuleParamArray<?> paramData : conditionData.getParamArrays())
				{
					if(paramData.getName().equals(paramArray.name()))
					{
						f.set(condition, paramData.getValues());
						break;
					}
				}
				continue;
			}			
		}		
		
		return condition;
	}

	public static Action buildActionFromTemplateAction(TemplateAction actionData, String path) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, IOException
	{
		Class<?> actionClass = null;
		Action action = null;
		Collection<Class<?>> clazzes = getAnnotatedClass(path, AnnotationAction.class);
		
		for(Class<?> clazz: clazzes)
		{
			if(clazz.getAnnotation(AnnotationAction.class).name().equalsIgnoreCase(actionData.getName()))
			{
				actionClass = clazz;
				break;
			}
		}
		
		
		action = (Action) actionClass.newInstance();
		
		Field fs[] = actionClass.getDeclaredFields();
		
		for(Field f : fs)
		{
			f.setAccessible(true);
			AnnotationRuleParam param = f.getAnnotation(AnnotationRuleParam.class);
			
			if(param != null)
			{
				for(RuleParam<?> paramData : actionData.getParams())
				{
					if(paramData.getName().equals(param.name()))
					{
						f.set(action, paramData.getValue());
						break;
					}
				}
				continue;
			}
			
			AnnotationRuleParamArray paramArray = f.getAnnotation(AnnotationRuleParamArray.class);
			
			if(paramArray != null)
			{
				for(RuleParamArray<?> paramData : actionData.getParamArrays())
				{
					if(paramData.getName().equals(paramArray.name()))
					{
						f.set(action, paramData.getValues());
						break;
					}
				}
				continue;
			}			
		}		
		
		return action;
	}
	
	public static Rule buildRuleFromTemplateRule(TemplateRule ruleData, 
			String pathConditonsTempates, String pathActionTemplates) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, IOException
	{
		return new RuleBuilder().
				name(ruleData.getName()).
				description(ruleData.getDescription()).
				when(buildConditionFromTemplateCondition(ruleData.getTemplateCondition(), pathConditonsTempates)).
				then(buildActionFromTemplateAction(ruleData.getTemplateAction(), pathActionTemplates)).
				build();
	}
	
}
