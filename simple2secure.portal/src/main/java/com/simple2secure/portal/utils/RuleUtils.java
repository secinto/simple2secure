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

package com.simple2secure.portal.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.core.RuleBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.google.common.reflect.ClassPath;
import com.simple2secure.api.model.DataType;
import com.simple2secure.api.model.RuleDeviceMapping;
import com.simple2secure.api.model.RuleEmailConfigMapping;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.RuleParam;
import com.simple2secure.api.model.RuleParamArray;
import com.simple2secure.api.model.TemplateAction;
import com.simple2secure.api.model.TemplateActionFactTypePair;
import com.simple2secure.api.model.TemplateCondition;
import com.simple2secure.api.model.TemplateConditionFactTypePair;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.exceptions.InconsistentDataException;
import com.simple2secure.commons.messages.Message;
import com.simple2secure.commons.rules.annotations.AnnotationAction;
import com.simple2secure.commons.rules.annotations.AnnotationCondition;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParam;
import com.simple2secure.commons.rules.annotations.AnnotationRuleParamArray;
import com.simple2secure.portal.providers.BaseServiceProvider;
import com.simple2secure.portal.rules.conditions.ConditionManager;
import com.simple2secure.portal.rules.conditions.ConditionManagerWithLimit;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import lombok.extern.slf4j.Slf4j;


/**
*
* This class should provide all functionalities which are needed for the the rule engine.
* This includes creating actions, conditions, rules for executing in the portal. Also 
* functionality for displaying and adding rules in the web.  
*
*/
@Configuration
@Component
@Slf4j
public class RuleUtils extends BaseServiceProvider
{

	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;


	/**
	 * Method to save data from the annotations of the member variables which are annotated as RuleParam or RuleParamArray into collections.
	 *
	 * @param clazz
	 *          which should be checked
	 * @param params
	 *          collection where the data of the RuleParam annotations should be saved
	 * @param paramArrays
	 *          collection where the data RuleParamArray annotations should be saved.
	 *
	 * @throws UnsupportedOperationException
	 *           when param has datatype which is non of the {@link DataType} Enum
	 */
	private void saveParamsFromClass(Class<?> clazz, Collection<RuleParam<?>> params, Collection<RuleParamArray<?>> paramArrays) 
	{

		Field fs[] = clazz.getDeclaredFields();

		for (Field f : fs) {

			f.setAccessible(true);

			AnnotationRuleParamArray annotationParamArray = f.getAnnotation(AnnotationRuleParamArray.class);

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
					paramArray = new RuleParamArray<String>();
					paramArray.setType(DataType._STRING);
					break;

				default:
					log.debug("The given data type of the rule param annotation is not implemented.");
					throw new UnsupportedOperationException(
							"The given data type: \"" + annotationParamArray.type().toString() + "\"is not implemented");
				}

				if (paramArray != null) {
					paramArray.setNameTag(annotationParamArray.name_tag());
					paramArray.setDescriptionTag(annotationParamArray.description_tag());
					paramArray.setValues(null);
					paramArrays.add(paramArray);
				}

				continue;
			}

			AnnotationRuleParam annotationParam = f.getAnnotation(AnnotationRuleParam.class);
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
					param = new RuleParam<String>();
					param.setType(DataType._STRING);
					break;

				default:
					log.debug("The given data type of the rule param array annotation is not implemented.");
					throw new UnsupportedOperationException("The given data type: \"" + annotationParam.type().toString() + "\"is not implemented");
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
	@SuppressWarnings("unchecked")
	private Collection<Class<?>> getAnnotatedClass(String packageName, Class<?> annotation) 
			throws IOException, ClassNotFoundException
	{
		Collection<Class<?>> clazzes = new ArrayList<>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ClassPath classPath = ClassPath.from(loader);

		for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(packageName)) {

			@SuppressWarnings("rawtypes")
			Class clazz = Class.forName(info.getName(), true, loader);

			if (clazz.getAnnotation(annotation) != null) {
				clazzes.add(clazz);
			}
		}

		return clazzes;
	}

	
	/**
	 * Method to load all predefined conditions from package and saved the information into a collection. Only the annotations (name,
	 * description, params, paramArrays) will be saved.
	 *
	 *
	 * @param packageName
	 *          where the condition classes are saved
	 *
	 * @return conditions data which are saved into a collection
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Collection<TemplateCondition> loadTemplateConditions(String packageName) 
			throws IOException, ClassNotFoundException 
	{
		Collection<TemplateCondition> conditions = new ArrayList<>();

		Collection<Class<?>> conditionsClazzes = getAnnotatedClass(packageName, AnnotationCondition.class);

		for (Class<?> conditionClazz : conditionsClazzes) {
			AnnotationCondition annotationCondition = conditionClazz.getAnnotation(AnnotationCondition.class);

			List<RuleParam<?>> conditionParams = new ArrayList<>();
			List<RuleParamArray<?>> conditionParamArrays = new ArrayList<>();

			saveParamsFromClass(conditionClazz, conditionParams, conditionParamArrays);

			// encapsulates the information from one predefined Action
			// into a TemplateAction object.
			TemplateCondition conditionObj = new TemplateCondition(conditionClazz.getName(), annotationCondition.name_tag(),
					annotationCondition.description_tag(), annotationCondition.fact_type(), conditionParams, conditionParamArrays);

			conditions.add(conditionObj);
		}
		return conditions;
	}

	
	/**
	 * Method to load all predefined actions from package and saved the information into a collection. Only the annotations (name,
	 * description, params, paramArrays) will be saved
	 *
	 *
	 * @param packageName
	 *          where the action classes are saved
	 *
	 * @return actions data which are saved into a collection
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Collection<TemplateAction> loadTemplateActions(String packageName) 
			throws IOException, ClassNotFoundException
	{
		Collection<TemplateAction> actions = new ArrayList<>();

		Collection<Class<?>> actionClazzes = getAnnotatedClass(packageName, AnnotationAction.class);

		for (Class<?> actionClazz : actionClazzes) {
			AnnotationAction annotationAction = actionClazz.getAnnotation(AnnotationAction.class);

			List<RuleParam<?>> actionParams = new ArrayList<>();
			List<RuleParamArray<?>> actionParamArrays = new ArrayList<>();

			saveParamsFromClass(actionClazz, actionParams, actionParamArrays);

			// encapsulates the information from one predefined Action
			// into a TemplateAction object.
			TemplateAction actionObj = new TemplateAction(actionClazz.getName(), annotationAction.name_tag(), annotationAction.description_tag(),
					annotationAction.fact_type(), actionParams, actionParamArrays);

			actions.add(actionObj);
		}

		return actions;
	}
	
	
	/**
	 * Method to reload the template actions and save them into the database.
	 *
	 * CAUTION: Deletes all saved predefined action templates from the database and reloads the data.
	 *
	 * @return loaded actions
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Collection<TemplateAction> loadAndSaveTemplateActions()
			throws ClassNotFoundException, IOException 
	{
		ruleActionsRepository.deleteAll();
		templateActionFactTypeMappingRepository.deleteAll();
		Collection<TemplateAction> actions;
		actions = loadTemplateActions(StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);
		actions.forEach(ruleActionsRepository::save);
		actions = ruleActionsRepository.findAll(); // must be fetched from DB to get the id
		actions.forEach(action -> {
			templateActionFactTypeMappingRepository.save(new TemplateActionFactTypePair(action.getFactType(), action.getId()));
		});
		return actions;
	}

	
	/**
	 * Method to reload the template conditions and save them into the database.
	 *
	 * CAUTION: Deletes all saved predefined conditions templates from the database and reloads the data.
	 *
	 * @return loaded conditions
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Collection<TemplateCondition> loadAndSaveTemplateConditions() 
			throws ClassNotFoundException, IOException 
	{
		ruleConditionsRepository.deleteAll();
		templateConditionFactTypeMappingRepository.deleteAll();
		Collection<TemplateCondition> conditions;
		conditions = loadTemplateConditions(StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH);
		conditions.forEach(ruleConditionsRepository::save);
		conditions = ruleConditionsRepository.findAll(); // must be fetched from DB to get the id
		conditions.forEach(condition -> {
			templateConditionFactTypeMappingRepository.save(new TemplateConditionFactTypePair(condition.getFactType(), condition.getId()));
		});
		return conditions;
	}


	/**
	 * Method to build a condition object from the information which is saved inside the TemplateCondition object.
	 *
	 * @param conditionData
	 *          holds the information which will be loaded into the predefined condition object
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
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InconsistentDataException 
	{
		Class<?> conditionClass = null;
		Condition condition = null;

		// fetches all classes which are annotated as AnnotationCondition.class
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
			String errorMessage = new StringBuilder()
					.append("Error: Classname saved in database which can not be found in source folder: Classname = ")
					.append(conditionData.getClassName())
					.toString();
			
			log.debug(errorMessage);
			throw new ClassNotFoundException(errorMessage);
		} else {
			condition = (Condition) conditionClass.newInstance();
		}

		Field fs[] = conditionClass.getDeclaredFields();

		// saves param data from conditionData into the Condition object
		for (Field f : fs) {

			f.setAccessible(true);
			AnnotationRuleParam param = f.getAnnotation(AnnotationRuleParam.class);

			if (param != null) {
				if (conditionData.getParams() == null) {
					log.debug("Error not all params which are needed for creating this condition are present in the given data.");
					throw new InconsistentDataException(new Message("condition_param_missing"));
				}

				boolean foundParam = false;
				for (RuleParam<?> paramData : conditionData.getParams()) {
					if (paramData.getNameTag().equals(param.name_tag())) {
						foundParam = true;
						f.set(condition, paramData.getValue());
						break;
					}
				}

				if (!foundParam) {
					log.debug("Error not all params which are needed for creating this condition are present in the given data.");
					throw new InconsistentDataException(new Message("condition_param_missing"));
				}

				continue;
			}

			AnnotationRuleParamArray paramArray = f.getAnnotation(AnnotationRuleParamArray.class);

			if (paramArray != null) {
				if (conditionData.getParamArrays() == null) {
					log.debug("Error not all params which are needed for creating this condition are present in the given data.");
					throw new InconsistentDataException(new Message("condition_param_missing"));
				}

				boolean foundParam = false;
				for (RuleParamArray<?> paramData : conditionData.getParamArrays()) {
					if (paramData.getNameTag().equals(paramArray.name_tag())) {
						foundParam = true;
						f.set(condition, paramData.getValues());
						break;
					}
				}

				if (!foundParam) {
					log.debug("Error not all params which are needed for creating this condition are present in the given data.");
					throw new InconsistentDataException(new Message("condition_param_missing"));
				}

				continue;
			}
		}

		return condition;
	}

	
	/**
	 * Method to build an action from the information which is saved inside the TemplateAction object.
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
	public Action buildActionFromTemplateAction(TemplateAction actionData, String packageName) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, InconsistentDataException 
	{
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
			String errorMessage = new StringBuilder()
					.append("Error: Classname saved in database which can not be found in source folder: Classname = ")
					.append(actionData.getClassName())
					.toString();
			
			log.debug(errorMessage);
			throw new ClassNotFoundException(errorMessage);
		} else {
			action = (Action) actionClass.newInstance();
		}

		Field fs[] = actionClass.getDeclaredFields();

		// saves param data from actionData into the Action object
		for (Field f : fs) {
			f.setAccessible(true);
			AnnotationRuleParam param = f.getAnnotation(AnnotationRuleParam.class);

			if (param != null) {

				if (actionData.getParams() == null) {
					log.debug("Error not all params which are needed for creating this action are present in the given data.");
					throw new InconsistentDataException(new Message("action_param_missing"));
				}

				boolean foundParam = false;
				for (RuleParam<?> paramData : actionData.getParams()) {
					if (paramData.getNameTag().equals(param.name_tag())) {
						foundParam = true;
						f.set(action, paramData.getValue());
						break;
					}
				}

				if (!foundParam) {
					log.debug("Error not all params which are needed for creating this action are present in the given data.");
					throw new InconsistentDataException(new Message("action_param_missing"));
				}

				continue;
			}

			AnnotationRuleParamArray paramArray = f.getAnnotation(AnnotationRuleParamArray.class);

			if (paramArray != null) {

				if (actionData.getParamArrays() == null) {
					log.debug("Error not all params which are needed for creating this action are present in the given data.");
					throw new InconsistentDataException(new Message("action_param_missing"));
				}

				boolean foundParams = true;
				for (RuleParamArray<?> paramData : actionData.getParamArrays()) {
					if (paramData.getNameTag().equals(paramArray.name_tag())) {
						foundParams = true;
						f.set(action, paramData.getValues());
						break;
					}
				}

				if (!foundParams) {
					log.debug("Error not all params which are needed for creating this action are present in the given data.");
					throw new InconsistentDataException(new Message("action_param_missing"));
				}

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
	 *          package path of where the predefined conditions should be saved.
	 * @param packageNameAction
	 *          package path of where the predefined actions should be saved.
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
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, IOException 
	{
		RuleBuilder builder = new RuleBuilder().name(ruleData.getName()).description(ruleData.getDescription());

		List<Condition> conditions = new ArrayList<>();
		for (TemplateCondition templateCondition : ruleData.getTemplateConditions()) {
			conditions.add(buildConditionFromTemplateCondition(templateCondition, packageNameConditons, ruleData.getName()));
		}

		ConditionManager conditionManager = new ConditionManager(conditions, ruleData.getConditionExpression());
		builder.when(conditionManager);

		for (TemplateAction action : ruleData.getTemplateActions()) {
			builder.then(buildActionFromTemplateAction(action, packageNameAction));
		}

		return builder.build();
	}
	
	
	/**
	 * Method to create a rule from a TemplateRule class. The Condition and Action will be registered as Beans for Spring
	 *
	 * @param ruleData
	 *          TemplateRule object which holds the information about the future rule object.
	 * @param packageNameConditons
	 *          package path where the predefined Conditions are saved.
	 * @param packageNameAction
	 *          package path where the predefined Actions are saved.
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
	public Rule buildRuleFromTemplateRuleWithBeanAndLimit(TemplateRule ruleData, String packageNameConditonsTempates,
			String packageNameActionTemplates)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, IOException
	{

		RuleBuilder builder = new RuleBuilder().name(ruleData.getName()).description(ruleData.getDescription());

		List<Condition> conditions = new ArrayList<>();
		for (TemplateCondition condtionData : ruleData.getTemplateConditions()) {
			Condition condition = buildConditionFromTemplateCondition(condtionData, packageNameConditonsTempates, ruleData.getName());
			autowireCapableBeanFactory.autowireBean(condition);
			conditions.add(condition);
		}

		ConditionManagerWithLimit conditionManager = new ConditionManagerWithLimit(ruleData.getId(), ruleData.getLimit(),
				ruleData.getConditionExpression(), conditions);
		autowireCapableBeanFactory.autowireBean(conditionManager);
		builder.when(conditionManager);

		for (TemplateAction actionData : ruleData.getTemplateActions()) {
			Action action = buildActionFromTemplateAction(actionData, packageNameActionTemplates);
			autowireCapableBeanFactory.autowireBean(action);
			builder.then(action);
		}

		return builder.build();
	}

	
	/**
	 * Method to create rules by a Set of TemplateRules.
	 *
	 * @param ruleData
	 * @param packageNameConditonsTempates
	 * @param packageNameActionTemplates
	 * @return a Set of Rule objects which can be used for the rule engine
	 */
	public Set<Rule> buildRulesFromTemplateRulesWithBeanAndLimit(Set<TemplateRule> ruleData, String packageNameConditonsTempates, String packageNameActionTemplates)
	{
		Set<Rule> rules = new TreeSet<>();
		ruleData.forEach(ruleInfo -> {
			try {
				Rule ruleObj = buildRuleFromTemplateRuleWithBeanAndLimit(ruleInfo, StaticConfigItems.TEMPLATE_CONDITIONS_PACKAGE_PATH,
						StaticConfigItems.TEMPLATE_ACTIONS_PACKAGE_PATH);

				rules.add(ruleObj);

			} catch (Exception e) {
				log.error("Unable to load the rule {} with the id {}. Exceptionmessage: {}", ruleInfo.getName(), ruleInfo.getId(), e.getMessage());
			}
		});

		return rules;
	}


	/**
	 * Method to check if the given expression can be evaluated by the library. Assumed that there are as many variables as given by
	 * countOfVars. This variables have to be named with the big letter A for the first var, B for second, ...
	 *
	 * @param String
	 *          expression which should be checked
	 * @param int
	 *          countOfVars the number of variables
	 *
	 * @return boolean true => can be evaluated, false => there is a syntax error
	 *
	 */
	public boolean isConditionExpressionParsable(String expression, int countOfVars)
	{
		try {
			Expression<String> resultExpression = ExprParser.parse(expression);
			for (int i = 0; i < countOfVars; i++) {
				resultExpression = RuleSet.assign(resultExpression, Collections.singletonMap(String.valueOf((char) (i + 65)), false));
			}

			if (resultExpression.equals(Literal.getFalse()) == true) {
				return true;
			} else if (resultExpression.equals(Literal.getTrue()) == true) {
				return true;
			} else {
				return false; // if the program gets to this point, there has been more variables than expected
			}
		} catch (Exception e) {
			return false;
		}
	}

	
	/**
	 * Method tries to simplify the condition expression, if the expression is not valid, there will be an exception. So use before the method
	 * {@link#isConditionExpressionParsable(String, int)}.
	 *
	 * @param String
	 *          expression which should be simplified
	 * @return String simplified expression
	 *
	 */
	public String tryToSimplyConditionExpression(String expression)
	{
		Expression<String> simpliefiedExpression = RuleSet.simplify(ExprParser.parse(expression));
		return simpliefiedExpression.toString();
	}
	
	
	/**
	 * Method to fetch TemplateRule from the DB by contextId and factType
	 *
	 * @param contextId
	 * @param factType
	 * @return TemplateRule object
	 */
	public List<TemplateRule> getTemplateRules(ObjectId contextId, RuleFactType factType)
	{
		return templateRuleRepository.findByContextIdAndFactType(contextId, factType);
	}

	
	/**
	 * Method to fetch TemplateRule from the DB by contextId and ruleId
	 *
	 * @param contextId
	 * @param ruleId
	 * @return TemplateRule object
	 */
	public TemplateRule getTemplateRule(ObjectId contextId, ObjectId ruleId)
	{
		return templateRuleRepository.findByContextIdAndRuleId(contextId, ruleId);
	}

	
	/**
	 * Method to fetch rule data from the database by context id and device id.
	 *
	 * @param contextId
	 * @param devideId
	 * @return a Set with the rule data
	 */
	public Set<TemplateRule> getTemplateRules(ObjectId contextId, ObjectId devideId) 
	{
		Set<TemplateRule> templateRules = new TreeSet<>();

		List<RuleDeviceMapping> tuples = ruleDeviceMappingRepository.getByDeviceIdAndContextId(contextId, devideId);

		tuples.forEach(tuple -> {
			TemplateRule templateRule = getTemplateRule(contextId, tuple.getRuleId());
			if (templateRule != null) {
				templateRules.add(getTemplateRule(contextId, tuple.getRuleId()));
			}
		});

		return templateRules;
	}

	
	/**
	 * Method to fetch rule data from the database by context id and email configuration id.
	 *
	 * @param contextId
	 * @param emailConfigurationId
	 * @return a Set with the rule data
	 */
	public Set<TemplateRule> getEmailTemplateRules(ObjectId contextId, ObjectId emailConfigurationId)
	{
		Set<TemplateRule> templateRules = new TreeSet<>();

		List<RuleEmailConfigMapping> tuples = ruleEmailConfigMappingRepository.getByContextIdAndEmailConfigId(contextId, emailConfigurationId);

		tuples.forEach(tuple -> {
			TemplateRule templateRule = getTemplateRule(contextId, tuple.getRuleId());
			if (templateRule != null) {
				templateRules.add(getTemplateRule(contextId, tuple.getRuleId()));
			}
		});

		return templateRules;
	}


	/**
	 * Method to reset the language specific texts with their tags
	 *
	 * @param rule
	 *          where the texts should be reseted
	 */
	public void resetTextTags(TemplateRule rule) 
	{
		ArrayList<TemplateAction> resetedActions = new ArrayList<>();

		for (TemplateAction oldAction : rule.getTemplateActions()) {
			TemplateAction resetedAction = ruleActionsRepository.find(oldAction.getId());

			for (int paramCount = 0; paramCount < oldAction.getParams().size(); paramCount++) {
				resetedAction.getParams().set(paramCount,
						RuleParam.copyAndSetValue(resetedAction.getParams().get(paramCount), oldAction.getParams().get(paramCount).getValue()));
			}

			for (int paramCount = 0; paramCount < oldAction.getParamArrays().size(); paramCount++) {
				resetedAction.getParamArrays().set(paramCount, RuleParamArray.copyAndSetValue(resetedAction.getParamArrays().get(paramCount),
						oldAction.getParamArrays().get(paramCount).getValues()));
			}

			resetedActions.add(resetedAction);
		}

		ArrayList<TemplateCondition> resetedConditions = new ArrayList<>();

		for (TemplateCondition oldCondition : rule.getTemplateConditions()) {
			TemplateCondition resetedCondition = ruleConditionsRepository.find(oldCondition.getId());

			for (int paramCount = 0; paramCount < oldCondition.getParams().size(); paramCount++) {
				resetedCondition.getParams().set(paramCount,
						RuleParam.copyAndSetValue(resetedCondition.getParams().get(paramCount), oldCondition.getParams().get(paramCount).getValue()));
			}

			for (int paramCount = 0; paramCount < oldCondition.getParamArrays().size(); paramCount++) {
				resetedCondition.getParamArrays().set(paramCount, RuleParamArray.copyAndSetValue(resetedCondition.getParamArrays().get(paramCount),
						oldCondition.getParamArrays().get(paramCount).getValues()));
			}

			resetedConditions.add(resetedCondition);
		}

		rule.setTemplateActions(resetedActions);
		rule.setTemplateConditions(resetedConditions);
	}

	
	/**
	 * Method to replace the tags with the specific language text
	 *
	 * @param action
	 * @param locale
	 */
	public void setLocaleTexts(TemplateAction action, ValidInputLocale locale) 
	{
		action.setNameTag(messageByLocaleService.getMessage(action.getNameTag(), locale.getValue()));
		action.setDescriptionTag(messageByLocaleService.getMessage(action.getDescriptionTag(), locale.getValue()));

		action.getParams().forEach(param -> {
			param.setNameTag(messageByLocaleService.getMessage(param.getNameTag(), locale.getValue()));
			param.setDescriptionTag(messageByLocaleService.getMessage(param.getDescriptionTag(), locale.getValue()));
		});

		action.getParamArrays().forEach(paramArray -> {
			paramArray.setNameTag(messageByLocaleService.getMessage(paramArray.getNameTag(), locale.getValue()));
			paramArray.setDescriptionTag(messageByLocaleService.getMessage(paramArray.getDescriptionTag(), locale.getValue()));
		});
	}

	
	/**
	 * Method to replace the tags with the specific language text
	 *
	 * @param condition
	 * @param locale
	 */
	public void setLocaleTexts(TemplateCondition condition, ValidInputLocale locale) 
	{
		condition.setNameTag(messageByLocaleService.getMessage(condition.getNameTag(), locale.getValue()));
		condition.setDescriptionTag(messageByLocaleService.getMessage(condition.getDescriptionTag(), locale.getValue()));

		condition.getParams().forEach(param -> {
			param.setNameTag(messageByLocaleService.getMessage(param.getNameTag(), locale.getValue()));
			param.setDescriptionTag(messageByLocaleService.getMessage(param.getDescriptionTag(), locale.getValue()));
		});

		condition.getParamArrays().forEach(paramArray -> {
			paramArray.setNameTag(messageByLocaleService.getMessage(paramArray.getNameTag(), locale.getValue()));
			paramArray.setDescriptionTag(messageByLocaleService.getMessage(paramArray.getDescriptionTag(), locale.getValue()));
		});
	}

	
	/**
	 * Method to replace the tags with the specific language text
	 *
	 * @param rule
	 * @param locale
	 */
	public void setLocaleTexts(TemplateRule rule, ValidInputLocale locale)
	{
		rule.getTemplateActions().forEach(action -> setLocaleTexts(action, locale));
		rule.getTemplateConditions().forEach(condition -> setLocaleTexts(condition, locale));
	}

}
