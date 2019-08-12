package com.simple2secure.portal.controller;

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
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{searchString}", method = RequestMethod.GET)
	public ResponseEntity<SearchResult> getSearchResult(@PathVariable("searchString") String searchString,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(searchString) && !Strings.isNullOrEmpty(locale)) {
			SearchResult sr = new SearchResult(searchString, "ksoakdoskad");
			return new ResponseEntity<SearchResult>(sr, HttpStatus.OK);
		}
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("service_not_found", locale)), HttpStatus.NOT_FOUND);
	}

}
