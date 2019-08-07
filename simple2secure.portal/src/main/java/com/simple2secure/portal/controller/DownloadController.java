/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
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
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadProbe(@RequestHeader("Accept-Language") String locale) throws IOException, URISyntaxException {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "s2s_setup.exe");
		byte[] downloadData = portalUtils.downloadFile();

		if (downloadData != null) {
			return new ResponseEntity<byte[]>(downloadData, httpHeaders, HttpStatus.OK);
		} else {
			log.error("File for download not found!");
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_during_download", locale)),
					HttpStatus.NOT_FOUND);
		}
	}
}
