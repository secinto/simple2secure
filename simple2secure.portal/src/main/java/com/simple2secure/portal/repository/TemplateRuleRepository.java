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

package com.simple2secure.portal.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.simple2secure.api.dto.TemplateRulesDTO;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.TemplateRule;
import com.simple2secure.portal.dao.MongoRepository;

/**
 * @author Richard Heinz
 *
 */
public abstract class TemplateRuleRepository extends MongoRepository<TemplateRule> {

	public abstract List<TemplateRule> findByContextId(ObjectId contextId);
	
	public abstract TemplateRule findByContextIdAndRuleId(ObjectId contextId, ObjectId ruleId);
	
	public abstract TemplateRule findByContextIdAndRuleName(ObjectId contextId, String name);
	
	public abstract List<TemplateRule> findByContextIdAndFactType(ObjectId contextId, RuleFactType type);
	
	public abstract TemplateRulesDTO findByContextIdAndFactTypeAndPagination(ObjectId contextId, RuleFactType type, int page, int size, String filter);
	
	public abstract void deleteTemplateRuleWithMapping(ObjectId contextId, ObjectId ruleId, RuleFactType type);
}
