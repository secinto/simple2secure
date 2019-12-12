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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.providers.BaseUtilsProvider;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputLocale;

@RestController
@RequestMapping(StaticConfigItems.DOWNLOAD_API)
public class DownloadController extends BaseUtilsProvider {

	private static Logger log = LoggerFactory.getLogger(DownloadController.class);

	/**
	 * This function donwloads the probe from the
	 *
	 * @param userId
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@ValidRequestMapping
	public ResponseEntity<byte[]> downloadProbe(@ServerProvidedValue ValidInputLocale locale) throws IOException, URISyntaxException {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "s2s_setup.exe");
		byte[] downloadData = portalUtils.downloadFile();

		if (downloadData != null) {
			return new ResponseEntity<>(downloadData, httpHeaders, HttpStatus.OK);
		} else {
			log.error("File for download not found!");
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_during_download", locale.getValue())),
					HttpStatus.NOT_FOUND);
		}
	}
}
