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

package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.dbo.GenericDBObject;
import com.simple2secure.api.model.FactToCheckByRuleEngine;
import com.simple2secure.api.model.OsQueryReport;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.portal.dao.MongoRepository;
import com.simple2secure.portal.repository.EmailRepository;
import com.simple2secure.portal.repository.FactsToCheckRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.OsQueryReportRepository;
import com.simple2secure.portal.repository.TestResultRepository;
import com.simple2secure.portal.repository.TestSequenceResultRepository;

@Repository
@Transactional
public class FactsToCheckRepositoryImpl extends FactsToCheckRepository {

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	OsQueryReportRepository osQueryReportRepository;

	@Autowired
	TestResultRepository testResultRepository;

	@Autowired
	TestSequenceResultRepository testSequenceResultRepository;

	@PostConstruct
	public void init() {
		super.collectionName = "factToCheckByRuleEngine";
		super.className = FactToCheckByRuleEngine.class;
	}

	@Override
	public List<OsQueryReport> findAndRemoveAllUnchecked() {
		List<FactToCheckByRuleEngine> listOfUncheckedReportsIds = new ArrayList<>();
		Query query = new Query(Criteria.where("IsChecked").is(false));
		listOfUncheckedReportsIds = mongoTemplate.find(query, FactToCheckByRuleEngine.class, collectionName);

		List<OsQueryReport> reports = new ArrayList<>();

		for (FactToCheckByRuleEngine unchecked : listOfUncheckedReportsIds) {
			reports.add(osQueryReportRepository.find(unchecked.id));
			delete(unchecked);
		}

		return reports;
	}

	@Override
	public <T extends GenericDBObject> List<T> findAndRemoveAllUncheckedByFact(RuleFactType factType, Class<T> classType) {
		List<FactToCheckByRuleEngine> listOfUncheckedFactIds;
		Query query = new Query(Criteria.where("IsChecked").is(false).and("factType").is(factType));
		listOfUncheckedFactIds = mongoTemplate.find(query, FactToCheckByRuleEngine.class, collectionName);

		List<T> facts = new ArrayList<>();

		MongoRepository<?> properRepository = null;

		switch (factType) {
		case EMAIL:
			properRepository = emailRepository;
			break;
		case GENERAL:
			return facts;
		case NETWORKREPORT:
			properRepository = networkReportRepository;
			break;
		case OSQUERYREPORT:
			properRepository = osQueryReportRepository;
			break;
		case TESTSEQUENCERESULT:
			properRepository = testSequenceResultRepository;
			break;
		case TESTRESULT:
			properRepository = testResultRepository;
			break;
		default:
			return facts;
		}

		for (FactToCheckByRuleEngine unchecked : listOfUncheckedFactIds) {
			try {
				GenericDBObject genericDBObject = properRepository.find(unchecked.getFactId());
				T fact = classType.cast(genericDBObject);
				facts.add(fact);
				delete(unchecked);
			} catch (Exception e) {
			}
		}

		return facts;
	}
}
