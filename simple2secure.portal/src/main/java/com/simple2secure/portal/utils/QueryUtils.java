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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.OSInfo;
import com.simple2secure.api.model.OsQuery;
import com.simple2secure.api.model.OsQueryGroupMapping;
import com.simple2secure.portal.providers.BaseServiceProvider;

@Component
public class QueryUtils extends BaseServiceProvider {

	private static Logger log = LoggerFactory.getLogger(QueryUtils.class);

	/**
	 * This function returns all unmapped queries according to the group id
	 *
	 * @param groupId
	 * @return
	 */
	public List<OsQuery> getUnmappedQueriesByGroup(String groupId) {
		List<OsQuery> unmappedQueries = new ArrayList<>();

		List<OsQuery> allQueries = queryRepository.findAll();
		List<OsQuery> mappedQueries = getMappedQueriesByGroup(groupId);

		for (OsQuery query : allQueries) {
			if (!mappedQueries.contains(query)) {
				unmappedQueries.add(query);
			}
		}

		log.info("Group {} has {} unmapped queries", groupId, unmappedQueries.size());

		Collections.sort(unmappedQueries, new Comparator<OsQuery>() {
			@Override
			public int compare(OsQuery u1, OsQuery u2) {
				return u1.getName().compareTo(u2.getName());
			}
		});

		return unmappedQueries;
	}

	/**
	 * This function returns all mapped queries according to the group id
	 *
	 * @param groupId
	 * @return
	 */
	public List<OsQuery> getMappedQueriesByGroup(String groupId) {
		List<OsQuery> queries = new ArrayList<>();
		List<OsQueryGroupMapping> mappedQueries = queryGroupMappingRepository.findByGroupId(groupId);

		for (OsQueryGroupMapping mapping : mappedQueries) {

			OsQuery currentQuery = queryRepository.find(mapping.getQueryId());
			if (currentQuery != null) {
				queries.add(currentQuery);
			}
		}

		Collections.sort(queries, new Comparator<OsQuery>() {
			@Override
			public int compare(OsQuery u1, OsQuery u2) {
				return u1.getName().compareTo(u2.getName());
			}
		});

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
	public void updateQueryGroupMapping(List<OsQuery> queries, String groupId) {
		if (queries != null) {
			queryGroupMappingRepository.deleteByGroupId(groupId);
			for (OsQuery query : queries) {
				OsQueryGroupMapping mapping = new OsQueryGroupMapping(groupId, query.getId(), query.getAnalysisInterval(),
						query.getAnalysisIntervalUnit(), query.getSystemsAvailable());
				queryGroupMappingRepository.save(mapping);
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
	public List<OsQuery> findByGroupIdsAndOsInfo(List<String> groupIds, OSInfo osInfo) {
		List<Integer> possibleValues = getPossibleValues(osInfo);
		List<OsQuery> queries = new ArrayList<>();
		List<OsQueryGroupMapping> mappings = queryGroupMappingRepository.getAllMapingsByGroupIds(groupIds, possibleValues);

		if (mappings != null) {
			for (OsQueryGroupMapping mapping : mappings) {
				OsQuery query = queryRepository.find(mapping.getQueryId());
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
	public List<OsQuery> findByGroupIdAndOsInfo(String groupId, OSInfo osInfo) {
		List<Integer> possibleValues = getPossibleValues(osInfo);
		List<OsQuery> queries = new ArrayList<>();
		List<OsQueryGroupMapping> mappings = queryGroupMappingRepository.findByGroupIdAndOSInfo(groupId, possibleValues);

		if (mappings != null) {
			for (OsQueryGroupMapping mapping : mappings) {
				OsQuery query = queryRepository.find(mapping.getQueryId());
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
	public OsQuery setValuesFromMapping(OsQuery query, OsQueryGroupMapping mapping) {
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
