package com.simple2secure.portal.rules.conditions;

import java.util.List;

import org.bson.types.ObjectId;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;

import com.simple2secure.api.model.TriggeredRule;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.TriggeredRuleHistoryRepository;


public class ConditionManagerWithLimit extends ConditionManager{

	private final ObjectId ruleId;
	private final int limit;
	
	public ConditionManagerWithLimit(ObjectId ruleId, int limit, String conditionExpression, List<Condition> conditions)
	{
		super(conditions, conditionExpression);
		this.ruleId = ruleId;
		this.limit = limit;
	}

	@Autowired
	TriggeredRuleHistoryRepository triggeredRuleHistoryRepository;

	
	@Override
	public boolean evaluate(Facts facts)
	{
		boolean result = super.evaluate(facts);
		if (!result) {
			return false;			
		}
		else {
			if (limit == 1)
				return result;
			
			TriggeredRule triggeredRule = triggeredRuleHistoryRepository.findByRuleId(ruleId);
			
			if(triggeredRule != null) {
				triggeredRule.increaseCountByOne();
				try {
					triggeredRuleHistoryRepository.update(triggeredRule);
				} catch (ItemNotFoundRepositoryException e) {
					e.printStackTrace();
				}
			}
			else {
				triggeredRule = new TriggeredRule(ruleId, 1);
				triggeredRuleHistoryRepository.save(triggeredRule);
			}
			
			if (triggeredRule.getCount() >= limit)
			{
				triggeredRuleHistoryRepository.delete(triggeredRule);
				return true;
			}
			else
				return false;
		}
	}
}
