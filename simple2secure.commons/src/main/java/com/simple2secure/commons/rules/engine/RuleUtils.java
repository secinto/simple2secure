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
import com.simple2secure.commons.exceptions.InconsistentDataException;
import com.simple2secure.commons.messages.Message;
import com.simple2secure.commons.rules.annotations.AnnotationAction;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;
import com.simple2secure.commons.rules.annotations.RuleName;

import groovy.lang.GroovyClassLoader;

/**
 * 
 * @author Richard Heinz
 * 
 *         This class should provide all functionalities which are needed to create a rule for the rule engine.
 *
 */
public class RuleUtils {

	/**
	 * Method to save data from annoations of membervariables which are annotatied as RuleParam orRuleParamArray into collections.
	 * 
	 * @param clazz
	 *          which should be checked
	 * @param params
	 *          collection where the data of the RuleParam annotations should be saved
	 * @param paramArrays
	 *          collection where the data RuleParamArray annotations should be saved.
	 *          
	 * @throws UnsupportedOperationException when param has datatype which is non of the DataType Enum
	 */
	private void saveParamsFromClass(Class clazz, Collection<RuleParam<?>> params, Collection<RuleParamArray<?>> paramArrays) {
		Field fs[] = clazz.getDeclaredFields();

		for (Field f : fs) {
			f.setAccessible(true);

			AnnotationRuleParamArray annotationParamArray = (AnnotationRuleParamArray) f.getAnnotation(AnnotationRuleParamArray.class);
			if (annotationParamArray != null) {
				RuleParamArray<?> paramArray = null;
				switch (annotationParamArray.type()) {
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
					throw new UnsupportedOperationException("The given data type: \"" + annotationParamArray.type().toString() + "\"is not implemented");
				}

				if (paramArray != null) {
					paramArray.setNameTag(annotationParamArray.name_tag());
					paramArray.setDescriptionTag(annotationParamArray.description_tag());
					paramArray.setValues(null);
					paramArrays.add(paramArray);
				}

				continue;
			}

			AnnotationRuleParam annotationParam = (AnnotationRuleParam) f.getAnnotation(AnnotationRuleParam.class);
			if (annotationParam != null) {

				RuleParam<?> param = null;
				switch (annotationParam.type()) {
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
					throw new UnsupportedOperationException("The given data type: \"" + annotationParamArray.type().toString() + "\"is not implemented");
				}

				if (param != null) {
					param.setNameTag(annotationParam.name_tag());
					param.setDescriptionTag(annotationParam.description_tag());
					param.setValue(null);
					params.add(param);
				}

				continue;
			}
		}
		
		// searching for annotation RuleParam with name "typeLimit" in the super class
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			Field superClassFields[] = superClazz.getDeclaredFields();

			for (Field f : superClassFields) {
				f.setAccessible(true);
				AnnotationRuleParam annotationParam = f.getAnnotation(AnnotationRuleParam.class);
				
				if(annotationParam != null) {
					if (annotationParam.name_tag().equals(AnnotationRuleParam.TYPE_LIMIT)) {
						if (annotationParam.type() == DataType._INT) {
							
							RuleParam<?> param = new RuleParam<Integer>();
							param.setType(DataType._INT);
							param.setNameTag(annotationParam.name_tag());
							param.setDescriptionTag(annotationParam.description_tag());
							param.setValue(null);
							params.add(param);
							break;
						}
					}
				}
			}	
		}
	}
	

	/**
	 * Method to fetch all classes in given package which has specific annotation.
	 * 
	 * @param packageName
	 *          where to search
	 * @param annotation
	 *          which the class must have
	 * 
	 * @return clazzes collection where all found classes are saved
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Collection<Class<?>> getAnnotatedClass(String packageName, Class<?> annotation) throws IOException, ClassNotFoundException {
		Collection<Class<?>> clazzes = new ArrayList<Class<?>>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ClassPath classPath = ClassPath.from(loader);

		for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(packageName)) {
			Class clazz = Class.forName(info.getName(), true, loader);

			if (clazz.getAnnotation(annotation) != null)
				clazzes.add(clazz);
		}

		return clazzes;
	}

	/**
	 * Method to load all predefined Conditons from package and saved the information into a Collection. Only the annotations (name,
	 * description, params, ...) will be saved
	 * 
	 * 
	 * @param packageName
	 *          where the Condition classe are saved
	 * 
	 * @return conditions data which are saved into a collection
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Collection<TemplateCondition> loadTemplateConditions(String packageName) throws IOException, ClassNotFoundException {
		Collection<TemplateCondition> conditions = new ArrayList<TemplateCondition>();

		Collection<Class<?>> conditionsClazzes = getAnnotatedClass(packageName, AnnotationCondition.class);

		for (Class<?> conditionClazz : conditionsClazzes) {
			AnnotationCondition annotationCondition = (AnnotationCondition) conditionClazz.getAnnotation(AnnotationCondition.class);

			List<RuleParam<?>> conditionParams = new ArrayList<RuleParam<?>>();
			List<RuleParamArray<?>> conditionParamArrays = new ArrayList<RuleParamArray<?>>();

			saveParamsFromClass(conditionClazz, conditionParams, conditionParamArrays);

			// encapsulates the information from one predefined Action
			// into a TemplateAction object.
			TemplateCondition conditionObj = new TemplateCondition(annotationCondition.name_tag(), annotationCondition.description_tag(),
					conditionParams, conditionParamArrays);

			conditions.add(conditionObj);
		}

		return conditions;
	}

	/**
	 * Method to load all predefined Actions from package and saved the information into a Collection. Only the annotations (name,
	 * description, params, ...) will be saved
	 * 
	 * 
	 * @param packageName
	 *          where the Action classe are saved
	 * 
	 * @return actions data which are saved into a collection
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Collection<TemplateAction> loadTemplateActions(String packageName) throws IOException, ClassNotFoundException {
		Collection<TemplateAction> actions = new ArrayList<TemplateAction>();

		Collection<Class<?>> actionClazzes = getAnnotatedClass(packageName, AnnotationAction.class);

		for (Class<?> actionClazz : actionClazzes) {
			AnnotationAction annotationAction = (AnnotationAction) actionClazz.getAnnotation(AnnotationAction.class);

			List<RuleParam<?>> actionParams = new ArrayList<RuleParam<?>>();
			List<RuleParamArray<?>> actionParamArrays = new ArrayList<RuleParamArray<?>>();

			saveParamsFromClass(actionClazz, actionParams, actionParamArrays);

			// encapsulates the information from one predefined Action
			// into a TemplateAction object.
			TemplateAction actionObj = new TemplateAction(annotationAction.name_tag(), annotationAction.description_tag(),
					actionParams, actionParamArrays);

			actions.add(actionObj);
		}

		return actions;
	}

	/**
	 * Method to build an Condition from the information which is saved inside the TemplateAction object.
	 * 
	 * @param conditionData
	 *          holds the information which will be loaded into the predefined Condition
	 * @param packageName
	 *          where the TemplateContitions are saved
	 * 
	 * @return condition which can be used for a rule
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throw InconsistentDataException
	 */
	public Condition buildConditionFromTemplateCondition(TemplateCondition conditionData, String packageName, String ruleName)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InconsistentDataException {
		Class<?> conditionClass = null;
		Condition condition = null;

		// fetches all classes which are annotatied as AnnotationAction.class
		// from the given class
		Collection<Class<?>> clazzes = getAnnotatedClass(packageName, AnnotationCondition.class);

		// takes required class
		for (Class<?> clazz : clazzes) {
			if (clazz.getAnnotation(AnnotationCondition.class).name_tag().equalsIgnoreCase(conditionData.getNameTag())) {
				conditionClass = clazz;
				break;
			}
		}

		if (conditionClass == null) {
			throw new InconsistentDataException(new Message("","Condition-classname saved in database which can´t be found in source folder."));
		} else {
			condition = (Condition) conditionClass.newInstance();			
		}

		Field fs[] = conditionClass.getDeclaredFields();

		// saves param data from conditionData into the Condition object
		for (Field f : fs) {
			
			f.setAccessible(true);
			AnnotationRuleParam param = f.getAnnotation(AnnotationRuleParam.class);

			if (param != null) 
			{
				if (conditionData.getParams() == null)
					throw new InconsistentDataException(new Message("","Needed param for conditionclass is not given."));
					
				boolean foundParam = false;
				for (RuleParam<?> paramData : conditionData.getParams()) {
					if (paramData.getNameTag().equals(param.name_tag())) {
						foundParam = true;
						f.set(condition, paramData.getValue());
						break;
					}
				}
				
				if (!foundParam)
				  throw new InconsistentDataException(new Message("","Needed param for conditionclass is not given."));
				
				continue;
			}

			AnnotationRuleParamArray paramArray = f.getAnnotation(AnnotationRuleParamArray.class);

			if (paramArray != null) {
				if(conditionData.getParamArrays() == null)
					throw new InconsistentDataException(new Message("","Needed param array for conditionclass is not given."));
				
				boolean foundParam = false;
				for (RuleParamArray<?> paramData : conditionData.getParamArrays()) {
					if (paramData.getNameTag().equals(paramArray.name_tag())) {
						foundParam = true;
						f.set(condition, paramData.getValues());
						break;
					}
				}
				
				if (!foundParam)
					  throw new InconsistentDataException(new Message("","Needed param array for conditionclass is not given."));
				
				continue;
			}
		}
		
		Class<?> conditionSuperClass = conditionClass.getSuperclass();
		if (conditionSuperClass != null && conditionSuperClass != Object.class)
		{
			Field superClassFields[] = conditionSuperClass.getDeclaredFields();

			for (Field f : superClassFields)
			{
				f.setAccessible(true);
				RuleName ruleNameAnno = f.getAnnotation(RuleName.class);
				
				if(ruleNameAnno != null)
				{
					f.set(condition, ruleName);	
					continue;
				}
				
				AnnotationRuleParam param = f.getAnnotation(AnnotationRuleParam.class);
				
				if(param != null) {
					if (param.name_tag().equals(AnnotationRuleParam.TYPE_LIMIT))
					{
						f.set(condition, 1); // default value
						
						List<RuleParam<?>> params = conditionData.getParams();
						
						if(params == null)
							continue;
						
						for (RuleParam<?> paramData : conditionData.getParams()) {
								if (paramData.getNameTag().equals(AnnotationRuleParam.TYPE_LIMIT))
								{
									f.set(condition, paramData.getValue());
									continue;
								}
						}
					}
				}
				
			}
		}
		
		return condition;
	}

	/**
	 * Method to build an Action from the information which is saved inside the TemplateAction object.
	 * 
	 * @param actionData
	 *          holds the information which will be loaded into the predefined Action
	 * @param packageName
	 *          where the Actions are saved
	 * 
	 * @return action which can be used for a rule
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throw InconsistentDataException
	 */
	public Action buildActionFromTemplateAction(TemplateAction actionData, String packageName)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, InconsistentDataException{
		Class<?> actionClass = null;
		Action action = null;

		// fetches all classes which are annotatied as AnnotationAction.class
		// from the given class
		Collection<Class<?>> clazzes = getAnnotatedClass(packageName, AnnotationAction.class);

		// takes required class
		for (Class<?> clazz : clazzes) {
			if (clazz.getAnnotation(AnnotationAction.class).name_tag().equalsIgnoreCase(actionData.getNameTag())) {
				actionClass = clazz;
				break;
			}
		}
		
		if (actionClass == null) {
			throw new InconsistentDataException(new Message("","Action-classname saved in database which can´t be found in source folder."));
		} else {		
			action = (Action) actionClass.newInstance();
		}


		Field fs[] = actionClass.getDeclaredFields();

		// saves param data from actionData into the Action object
		for (Field f : fs) {
			f.setAccessible(true);
			AnnotationRuleParam param = f.getAnnotation(AnnotationRuleParam.class);

			if (param != null) {
				
				if (actionData.getParams() == null) 
					throw new InconsistentDataException(new Message("","Needed param for actionclass is not given."));
				
				boolean foundParam = false;
				for (RuleParam<?> paramData : actionData.getParams()) {
					if (paramData.getNameTag().equals(param.name_tag())) {
						foundParam = true;
						f.set(action, paramData.getValue());
						break;
					}
				}
				
				if (!foundParam)
					  throw new InconsistentDataException(new Message("","Needed param for actionclass is not given."));
				
				continue;
			}

			AnnotationRuleParamArray paramArray = f.getAnnotation(AnnotationRuleParamArray.class);

			if (paramArray != null) {
				
				if(actionData.getParamArrays() == null)
					throw new InconsistentDataException(new Message("","Needed param array for actionclass is not given."));
				
				boolean foundParams = true;
				for (RuleParamArray<?> paramData : actionData.getParamArrays()) {
					if (paramData.getNameTag().equals(paramArray.name_tag())) {
						foundParams = true;
						f.set(action, paramData.getValues());
						break;
					}
				}
				
				if (!foundParams)
					  throw new InconsistentDataException(new Message("","Needed param array for actionclass is not given."));
				
				continue;
			}
		}

		return action;
	}

	/**
	 * Method to create a rule from a TemplateRule class which holds the information (name, description, params,...) about predefined
	 * conditions/ actions.
	 * 
	 * @param ruleData
	 *          TemplateRule object which holds the information about the future rule object.
	 * @param packageNameConditons
	 *          represents where the predefined Conditions are saved.
	 * @param packageNameAction
	 *          represents where the predefined Actions are saved.
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
	public Rule buildRuleFromTemplateRule(TemplateRule ruleData, String packageNameConditons, String packageNameAction)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, IOException {
		return new RuleBuilder().name(ruleData.getName()).description(ruleData.getDescription())
				.when(buildConditionFromTemplateCondition(ruleData.getTemplateCondition(), packageNameConditons, ruleData.getName()))
				.then(buildActionFromTemplateAction(ruleData.getTemplateAction(), packageNameAction)).build();
	}

	/**
	 * Method to load source code from string and creates a object which represents a rule.
	 * 
	 * Attention: Does not support spring framework in source!
	 * 
	 * @param source
	 *          which represents the source code of a rule class
	 * @return rule which can be used for the rule engine
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	public Object createRuleFromSource(String source) throws IOException, InstantiationException, IllegalAccessException {
		try (GroovyClassLoader groovyClassLoader = new GroovyClassLoader()) {
			Class<?> theParsedClass = groovyClassLoader.parseClass(source);

			Object rule = theParsedClass.newInstance();

			return rule;
		}
	}
}
