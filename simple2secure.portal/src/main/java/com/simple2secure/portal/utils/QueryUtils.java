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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.OSInfo;
import com.simple2secure.api.model.QueryGroupMapping;
import com.simple2secure.api.model.QueryRun;
import com.simple2secure.portal.repository.QueryGroupMappingRepository;
import com.simple2secure.portal.repository.QueryRepository;

@Component
public class QueryUtils {

	private static Logger log = LoggerFactory.getLogger(QueryUtils.class);

	@Autowired
	QueryRepository queryRepository;

	@Autowired
	QueryGroupMappingRepository queryGroupMappingsRepository;

	/**
	 * This function returns all unmapped queries according to the group id
	 *
	 * @param groupId
	 * @return
	 */
	public List<QueryRun> getUnmappedQueriesByGroup(String groupId) {
		List<QueryRun> unmappedQueries = new ArrayList<>();

		List<QueryRun> allQueries = queryRepository.findAll();
		List<QueryRun> mappedQueries = getMappedQueriesByGruop(groupId);

		for (QueryRun query : allQueries) {
			if (!mappedQueries.contains(query)) {
				unmappedQueries.add(query);
			}
		}

		log.info("Group {} has {} unmapped queries", groupId, unmappedQueries.size());

		return unmappedQueries;
	}

	/**
	 * This function returns all mapped queries according to the group id
	 *
	 * @param groupId
	 * @return
	 */
	public List<QueryRun> getMappedQueriesByGruop(String groupId) {
		List<QueryRun> queries = new ArrayList<>();
		List<QueryGroupMapping> mappedQueries = queryGroupMappingsRepository.findByGroupId(groupId);

		for (QueryGroupMapping mapping : mappedQueries) {

			QueryRun currentQuery = queryRepository.find(mapping.getQueryId());
			if (currentQuery != null) {
				queries.add(currentQuery);
			}
		}

		log.info("Group {} has {} mapped queries", groupId, queries.size());

		return queries;
	}

	/**
	 * This function updates the Query Group Mappings. All mappings from the provided group will be first deleted, and then the new provided
	 * mappings will be added.
	 *
	 * @param queries
	 * @param groupId
	 */
	public void updateQueryGroupMapping(List<QueryRun> queries, String groupId) {
		if (queries != null) {
			queryGroupMappingsRepository.deleteByGroupId(groupId);
			for (QueryRun query : queries) {
				QueryGroupMapping mapping = new QueryGroupMapping(groupId, query.getId(), query.getAnalysisInterval(),
						query.getAnalysisIntervalUnit(), query.getSystemsAvailable());
				queryGroupMappingsRepository.save(mapping);
			}
		}
	}

	/**
	 * This function returns the Queries according to the provided list of group ids, operation system info, and selectAll flag. If selectAll
	 * is set to true, all mappings(inactive and active) will be returned. In another case only the active mappings will be returned.
	 *
	 * @param groupIds
	 * @param osInfo
	 * @param selectAll
	 * @return
	 */
	public List<QueryRun> findByGroupIdsAndOsInfo(List<String> groupIds, OSInfo osInfo, boolean selectAll) {
		List<Integer> possibleValues = getPossibleValues(osInfo);
		List<QueryRun> queries = new ArrayList<>();
		List<QueryGroupMapping> mappings = queryGroupMappingsRepository.getAllMapingsByGroupIds(groupIds, possibleValues, selectAll);

		if (mappings != null) {
			for (QueryGroupMapping mapping : mappings) {
				QueryRun query = queryRepository.find(mapping.getQueryId());
				if (query != null) {
					query = setValuesFromMapping(query, mapping);
					queries.add(query);
				}
			}
		}

		return queries;
	}

	/**
	 * This function returns the Queries according to the provided groupId, operation system info, and selectAll flag. If selectAll is set to
	 * true, all mappings(inactive and active) will be returned. In another case only the active mappings will be returned.
	 *
	 * @param groupId
	 * @param osInfo
	 * @param selectAll
	 * @return
	 */
	public List<QueryRun> findByGroupIdAndOsInfo(String groupId, OSInfo osInfo, boolean selectAll) {
		List<Integer> possibleValues = getPossibleValues(osInfo);
		List<QueryRun> queries = new ArrayList<>();
		List<QueryGroupMapping> mappings = queryGroupMappingsRepository.findByGroupIdAndOSInfo(groupId, possibleValues, selectAll);

		if (mappings != null) {
			for (QueryGroupMapping mapping : mappings) {
				QueryRun query = queryRepository.find(mapping.getQueryId());
				if (query != null) {
					query = setValuesFromMapping(query, mapping);
					queries.add(query);
				}
			}
		}

		return queries;
	}

	/**
	 * This function sets the custom values which can be defined pro query mapping
	 *
	 * @param query
	 * @param mapping
	 * @return
	 */
	public QueryRun setValuesFromMapping(QueryRun query, QueryGroupMapping mapping) {
		query.setAnalysisInterval(mapping.getAnalysisInterval());
		query.setAnalysisIntervalUnit(mapping.getAnalysisIntervalUnit());
		query.setSystemsAvailable(mapping.getSystemsAvailable());
		return query;
	}

	/**
	 * This function returns possible values according to the provided operating system
	 *
	 * @param osInfo
	 * @return
	 */
	private List<Integer> getPossibleValues(OSInfo osInfo) {
		List<Integer> possibleValues = new ArrayList<>();
		switch (osInfo) {
		case WINDOWS:
			possibleValues.add(1);
			possibleValues.add(3);
			possibleValues.add(5);
			possibleValues.add(7);
			break;
		case LINUX:
			possibleValues.add(2);
			possibleValues.add(3);
			possibleValues.add(6);
			possibleValues.add(7);
			break;
		case OSX:
			possibleValues.add(4);
			possibleValues.add(5);
			possibleValues.add(6);
			possibleValues.add(7);
			break;
		default:
			possibleValues.add(1);
			possibleValues.add(2);
			possibleValues.add(3);
			possibleValues.add(4);
			possibleValues.add(5);
			possibleValues.add(6);
			possibleValues.add(7);
			break;
		}
		return possibleValues;
	}
}
