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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.simple2secure.commons.config.StaticConfigItems;

import simple2secure.validator.annotation.ValidRequestMapping;

@RestController
@RequestMapping(StaticConfigItems.ERROR_API)
public class CustomErrorController extends BaseController implements ErrorController {

	@ValidRequestMapping
	public Map<String, Object> error(HttpServletRequest request, WebRequest webrequest, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> errAttributes = getErrorAttributes(webrequest, false);
		map.put("status", response.getStatus());
		map.put("reason", errAttributes);
		map.put("errorMessage", getErrorMessage(errAttributes));
		return map;
	}

	private Map<String, Object> getErrorAttributes(WebRequest webrequest, boolean includeStackTrace) {
		return errorAttributes.getErrorAttributes(webrequest, includeStackTrace);
	}

	private String getErrorMessage(Map<String, Object> attributes) {
		return (String) attributes.get("message");
	}

	@Override
	public String getErrorPath() {
		return StaticConfigItems.ERROR_API;
	}
}
