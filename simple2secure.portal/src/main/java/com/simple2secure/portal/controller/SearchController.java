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

package com.simple2secure.portal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.SearchResult;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputSearchQuery;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;

@RestController
@RequestMapping(StaticConfigItems.SEARCH_API)
@Slf4j
public class SearchController extends BaseUtilsProvider {

	@ValidRequestMapping
	public ResponseEntity<List<SearchResult>> getSearchResult(@PathVariable ValidInputSearchQuery searchQuery,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(searchQuery.getValue()) && contextId.getValue() != null) {

			Context context = contextRepository.find(contextId.getValue());

			if (context != null) {
				List<SearchResult> srList;
				try {
					srList = searchUtils.getAllSearchResults(searchQuery.getValue(), context);
					log.debug("Found {} search results for search query {}", srList.size(), searchQuery);
					if (srList != null) {
						return new ResponseEntity<>(srList, HttpStatus.OK);
					}
				} catch (ItemNotFoundRepositoryException e) {
					log.error("Error occured while retrieving results for query {}", searchQuery);
				}

			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("service_not_found", locale.getValue()));
	}

}
