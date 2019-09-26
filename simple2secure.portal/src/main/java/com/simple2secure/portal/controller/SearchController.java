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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.SearchResult;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.SearchUtils;

@RestController
@RequestMapping("/api/search")
public class SearchController {

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	SearchUtils searchUtils;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/{searchQuery}/{contextId}",
			method = RequestMethod.GET)
	public ResponseEntity<List<SearchResult>> getSearchResult(@PathVariable("searchQuery") String searchQuery,
			@PathVariable("contextId") String contextId, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(searchQuery) && !Strings.isNullOrEmpty(contextId) && !Strings.isNullOrEmpty(locale)) {

			Context context = contextRepository.find(contextId);

			if (context != null) {
				List<SearchResult> srList = searchUtils.getAllSearchResults(searchQuery, contextId);

				if (srList != null) {
					return new ResponseEntity<>(srList, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("service_not_found", locale)), HttpStatus.NOT_FOUND);
	}

}
