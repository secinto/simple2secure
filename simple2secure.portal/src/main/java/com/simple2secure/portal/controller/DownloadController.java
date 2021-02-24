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
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestContent;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputGroup;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.NotSecuredApi;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.DOWNLOAD_API)
@Slf4j
public class DownloadController extends BaseUtilsProvider {

	/**
	 * This function provides a zip directory with a valid license and a setup_s2s_probe.exe for download.
	 *
	 * @param groupId
	 *          The groupId is ne
	 * @return Bytearray for web where it will be converted to a zip for download.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@NotSecuredApi
	@ValidRequestMapping
	public ResponseEntity<byte[]> downloadProbe(@PathVariable ValidInputGroup groupId, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		httpHeaders.setContentDispositionFormData("attachment", "s2s_probe.zip");
		byte[] downloadData;
		try {
			downloadData = licenseUtils.downloadFile(contextId.getValue(), groupId.getValue());
		} catch (IOException | URISyntaxException e) {
			log.error("Error during download, problem while trying to download file");
			throw new ApiRequestException(messageByLocaleService.getMessage("error_during_download", locale.getValue()), e);
		}

		if (downloadData != null) {
			return new ResponseEntity<>(downloadData, httpHeaders, HttpStatus.OK);
		} else {
			throw new ApiRequestException(messageByLocaleService.getMessage("error_during_download", locale.getValue()));
		}
	}

	@ValidRequestMapping(
			value = "/services",
			method = ValidRequestMethodType.GET)
	public ResponseEntity<byte[]> downloadTests(@PathVariable ValidInputDevice deviceId, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (deviceId.getValue() != null) {
			List<Test> tests = testRepository.getByDeviceId(deviceId.getValue());
			List<TestContent> services = new ArrayList<>();
			if (tests != null) {
				for (Test test : tests) {
					TestContent content = testUtils.getTestContent(test.getTestContent());
					services.add(content);
				}
			}

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String json = objectMapper.writeValueAsString(services);
				byte[] isr = json.getBytes();
				String fileName = "services.json";
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders.setContentLength(isr.length);
				respHeaders.setContentType(new MediaType("text", "json"));
				respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
				respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
				return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
			}
		}

		throw new ApiRequestException(messageByLocaleService.getMessage("error_during_download", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/suts",
			method = ValidRequestMethodType.GET)
	public ResponseEntity<byte[]> downloadSuts(@ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (contextId.getValue() != null) {
			List<SystemUnderTest> sutList = sutRepository.getAllByContextId(contextId.getValue());

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String json = objectMapper.writeValueAsString(sutList);
				byte[] isr = json.getBytes();
				String fileName = "suts.json";
				HttpHeaders respHeaders = new HttpHeaders();
				respHeaders.setContentLength(isr.length);
				respHeaders.setContentType(new MediaType("text", "json"));
				respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
				respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
				return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
			}
		}

		throw new ApiRequestException(messageByLocaleService.getMessage("error_during_download", locale.getValue()));
	}
}
