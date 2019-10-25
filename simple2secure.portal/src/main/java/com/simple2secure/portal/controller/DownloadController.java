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

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.PortalUtils;

@RestController
@RequestMapping("/api/download")
public class DownloadController {

	private static Logger log = LoggerFactory.getLogger(DownloadController.class);

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	PortalUtils portalUtils;

	/**
	 * This function donwloads the probe from the
	 *
	 * @param userId
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(
			value = "",
			method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadProbe(@RequestHeader("Accept-Language") String locale) throws IOException, URISyntaxException {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "s2s_setup.exe");
		byte[] downloadData = portalUtils.downloadFile();

		if (downloadData != null) {
			return new ResponseEntity<>(downloadData, httpHeaders, HttpStatus.OK);
		} else {
			log.error("File for download not found!");
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_during_download", locale)),
					HttpStatus.NOT_FOUND);
		}
	}
}
