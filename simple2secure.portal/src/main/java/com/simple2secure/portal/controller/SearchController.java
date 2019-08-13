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
import com.simple2secure.api.model.SearchResult;
import com.simple2secure.portal.model.CustomErrorType;
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
	SearchUtils searchUtils;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{searchQuery}", method = RequestMethod.GET)
	public ResponseEntity<List<SearchResult>> getSearchResult(@PathVariable("searchQuery") String searchQuery,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(searchQuery) && !Strings.isNullOrEmpty(locale)) {

			List<SearchResult> srList = searchUtils.getAllSearchResults(searchQuery);

			if (srList != null) {
				return new ResponseEntity<>(srList, HttpStatus.OK);
			}

		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("service_not_found", locale)), HttpStatus.NOT_FOUND);
	}

}
