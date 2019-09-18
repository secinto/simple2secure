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
import java.util.List;

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

import groovy.lang.GroovyClassLoader;


/**
 * 
 * @author Richard Heinz
 * 
 * This class should provide all functionalities which are needed to create a 
 * rule for the rule engine. 
 *
 */
public class RuleUtils {
	
	/**
	 * Method to save data from annoations of membervariables which are
	 *  annotatied as RuleParam orRuleParamArray into collections. 
	 *  
	 * @param clazz which should be checked
	 * @param params collection where the data of the RuleParam annotations
	 *               should be saved  
	 * @param paramArrays collection where the data RuleParamArray annotations
	 *                    should be saved.
	 */
	private void saveParamsFromClass(Class clazz, Collection<RuleParam<?>> params, Collection<RuleParamArray<?>> paramArrays)
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
		    	case _INT:
		    		paramArray = new RuleParamArray<Integer>();
		    		paramArray.setType(DataType._INT);
		    		break;
		    		
		    	case _DOUBLE:
		    		paramArray = new RuleParamArray<Double>();
		    		paramArray.setType(DataType._DOUBLE);
		    		break;
		    		
		    	case _STRING:
		    		paramArray = new RuleParamArray<Double>();
		    		paramArray.setType(DataType._STRING);
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
		    	case _INT:
		    		param = new RuleParam<Integer>();
		    		param.setType(DataType._INT);
		    		break;
		    		
		    	case _DOUBLE:
		    		param = new RuleParam<Double>();
		    		param.setType(DataType._DOUBLE);
		    		break;
		    		
		    	case _STRING:
		    		param = new RuleParam<Double>();
		    		param.setType(DataType._STRING);
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
	
	/**
	 * Method to fetch all classes in given path which has specific annotation.
	 * 
	 * @param path where to search
	 * @param annotation which the class must have
	 * 
	 * @return clazzes collection where all found classes are saved
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Collection<Class<?>> getAnnotatedClass(String path, Class<?> annotation) throws IOException, ClassNotFoundException
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
	

	/**
	 * Method to load all predefined Conditons from path and saved the 
	 * information into a Collection. Only the annotations (name, description,
	 *  params, ...) will be saved
	 * 
	 * 
	 * @param path where the Condition classe are saved
	 * 
	 * @return conditions data which are saved into a collection
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Collection<TemplateCondition> loadTemplateConditions(String path) throws IOException, ClassNotFoundException
	{
		Collection<TemplateCondition> conditions = new ArrayList<TemplateCondition>();
		
		Collection<Class<?>> conditionsClazzes = getAnnotatedClass(path, AnnotationCondition.class);
		
		for(Class<?> conditionClazz : conditionsClazzes)
		{
			AnnotationCondition annotationCondition = (AnnotationCondition) conditionClazz.getAnnotation(AnnotationCondition.class);
			
			List<RuleParam<?>> conditionParams = new ArrayList<RuleParam<?>>();
		    List<RuleParamArray<?>> conditionParamArrays = new ArrayList<RuleParamArray<?>>();
		    
		    saveParamsFromClass(conditionClazz, conditionParams, conditionParamArrays);
			
		    // encapsulates the information from one predefined Action
		    // into a TemplateAction object.
			TemplateCondition conditionObj = new TemplateCondition(
		    		annotationCondition.name(), 
		    		annotationCondition.description_en(), 
		    		annotationCondition.description_de(),
		    		conditionParams, conditionParamArrays);
			
			conditions.add(conditionObj);
	    }
		
		return conditions;	
	}
	
	
	/**
	 * Method to load all predefined Actions from path and saved the 
	 * information into a Collection. Only the annotations (name, description, params,
	 * ...) will be saved
	 * 
	 * 
	 * @param path where the Action classe are saved
	 * 
	 * @return actions data which are saved into a collection
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Collection<TemplateAction> loadTemplateActions(String path) throws IOException, ClassNotFoundException
	{
		Collection<TemplateAction> actions = new ArrayList<TemplateAction>();
		
		Collection<Class<?>> actionClazzes = getAnnotatedClass(path, AnnotationAction.class);
		
		for(Class<?> actionClazz : actionClazzes)
		{
			AnnotationAction annotationAction = (AnnotationAction) actionClazz.getAnnotation(AnnotationAction.class);
			
			List<RuleParam<?>> actionParams = new ArrayList<RuleParam<?>>();
			List<RuleParamArray<?>> actionParamArrays = new ArrayList<RuleParamArray<?>>();
		    
		    saveParamsFromClass(actionClazz, actionParams, actionParamArrays);
			
		    // encapsulates the information from one predefined Action
		    // into a TemplateAction object.
		    TemplateAction actionObj = new TemplateAction(
		    		annotationAction.name(), 
		    		annotationAction.description_en(), 
		    		annotationAction.description_de(),
		    		actionParams, actionParamArrays);
			
		    actions.add(actionObj);
		}
		
		return actions;	
	}

	
	/**
	 * Method to build an Condition from the information which is saved inside 
	 * the TemplateAction object.
	 * 
	 * @param conditionData holds the information which will be loaded into the
	 * 					    predefined Condition 
	 * @param path where the TemplateContitions are saved
	 * 
	 * @return condition which can be used for a rule
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Condition buildConditionFromTemplateCondition(TemplateCondition conditionData, String path) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
	{
		Class<?> conditionClass = null;
		Condition condition = null;
		
		// fetches all classes which are annotatied as AnnotationAction.class
		// from the given class
		Collection<Class<?>> clazzes = getAnnotatedClass(path, AnnotationCondition.class);
		
		// takes required class
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
		
		// saves param data from conditionData into the Condition object
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

	
	/**
	 * Method to build an Action from the information which is saved inside 
	 * the TemplateAction object.
	 * 
	 * @param actionData holds the information which will be loaded into the
	 * 					 predefined Action 
	 * @param path where the Actions are saved
	 * 
	 * @return action which can be used for a rule
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Action buildActionFromTemplateAction(TemplateAction actionData,
			String path) 
					throws IllegalArgumentException, IllegalAccessException,
					InstantiationException, ClassNotFoundException, IOException
	{
		Class<?> actionClass = null;
		Action action = null;
		
		// fetches all classes which are annotatied as AnnotationAction.class
		// from the given class
		Collection<Class<?>> clazzes = getAnnotatedClass(path, AnnotationAction.class);
		
		// takes required class
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
		
		// saves param data from actionData into the Action object
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
	
	
	/**
	 * Method to create a rule from a TemplateRule class which holds the 
	 * information (name, description, params,...) about predefined conditions/
	 * actions. 
	 * 
	 * @param ruleData TemplateRule object which holds the information about 
	 *                 the future rule object.
	 * @param pathConditonsTempates represents where the predefined 
	 * 								Conditions are saved.
	 * @param pathActionTemplates represents where the predefined 
	 * 								Actions are saved.
	 * 
	 * @return a Rule object which can be used for the rule engine
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * 
	 */
	public Rule buildRuleFromTemplateRule(TemplateRule ruleData, 
			String pathConditonsTempates, String pathActionTemplates) 
					throws ClassNotFoundException, InstantiationException,
					IllegalAccessException, IllegalArgumentException, 
					IOException
	{
		return new RuleBuilder().
				name(ruleData.getName()).
				description(ruleData.getDescription()).
				when(buildConditionFromTemplateCondition(ruleData.getTemplateCondition(), pathConditonsTempates)).
				then(buildActionFromTemplateAction(ruleData.getTemplateAction(), pathActionTemplates)).
				build();
	}
	
	
	/**
	 * Method to load source code from string and creates a object which 
	 * represents a rule.
	 * 
	 * Attention: Does not support spring framework in source!
	 * 
	 * @param source which represents the source code of a rule class
	 * @return rule which can be used for the rule engine
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	public Object createRuleFromSource(String source)
			throws IOException, InstantiationException, IllegalAccessException
	{
		try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader()) {
			Class<?> theParsedClass = groovyClassLoader.parseClass(source);

			Object rule = theParsedClass.newInstance();
			
			return rule;
		}
	}
}
