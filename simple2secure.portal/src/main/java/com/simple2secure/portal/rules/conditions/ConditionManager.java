package com.simple2secure.portal.rules.conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import com.simple2secure.commons.exceptions.InconsistentDataException;
import com.simple2secure.commons.messages.Message;
import com.simple2secure.portal.controller.QueryController;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * The condition manager encapsulates multiple conditions to one big with a logic expression
 * which takes the results from every condition evaluation as input. The output of the logic
 * expression is then the result for the whole condition.
 *
 */
@Slf4j
public class ConditionManager implements Condition
{
	
	private final List<Condition> conditions;
	private Expression<String> parsedLogicalExpression;
	
	/**
	 *  CAUTION: THE BOOLEAN VARIABLES MUST BE BIG LETTERS OTHERWISE THE PARSER 
	 *  WILL NOT BE ABLE TO EVALUATE THE EXPRESSEION AND A EXCEPTION WILL BE THROWN!
	 *  
	 * @param conditions
	 * @param conditionExpression
	 * @throws InconsistentDataException
	 */
	public ConditionManager(List<Condition> conditions, String conditionExpression)
	throws InconsistentDataException
	{
		this.conditions = conditions;
		
		try 
		{
			this.parsedLogicalExpression = ExprParser.parse(conditionExpression);	
		}
		catch (Exception e)
		{
			log.debug("Error the given condition expreesion is not parseable: {}", conditionExpression);
			throw new InconsistentDataException(new Message("error_while_evaluating_condition_expression"), e.getCause());
		}
	}


	/**
	 * 	 * Method to evaluate all conditions and than use there output as input for the logic expression.
	 * 
	 * @param facts which will be used for every condition
	 * @return boolen result of the logic expression with the values of the individual conditions 
	 * @throws InconsistentDataException
	 */
	protected boolean evaluateConditionExpression(Facts facts)
	throws InconsistentDataException
	{
		ArrayList<Boolean> results = new ArrayList<Boolean>();
		
		for(final Condition condition : conditions)
		{
			results.add(condition.evaluate(facts));			
		}

	   	for (int i = 0; i < results.size(); i++)
	   	{
	   		parsedLogicalExpression = RuleSet.assign(
	   				parsedLogicalExpression,
	   				Collections.singletonMap(String.valueOf((char)(i+65)), results.get(i)));	   		
	   	}
	   	
		if(parsedLogicalExpression.equals(Literal.getTrue()))
		{
			return true;			
		}
		else if (parsedLogicalExpression.equals(Literal.getFalse()))
		{
			return false;			
		}
		else
		{
			log.debug("Error the given condition expression is with the given variables not parsable: {}", parsedLogicalExpression.toString());
			throw new InconsistentDataException(new Message("error_while_evaluating_condition_expression"));
		}
	}
	
	
	@Override
	public boolean evaluate(Facts facts)
	{
		return evaluateConditionExpression(facts);
	}		

}
