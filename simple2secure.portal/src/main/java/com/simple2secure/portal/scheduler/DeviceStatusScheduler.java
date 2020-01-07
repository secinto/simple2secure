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
package com.simple2secure.portal.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.utils.NotificationUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DeviceStatusScheduler {

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	NotificationUtils notificationUtils;

	/**
	 * This function checks if there are some tests which need to be executed and adds those test to the TestRun table in the database
	 *
	 * @throws ItemNotFoundRepositoryException
	 *
	 */

	@Scheduled(fixedRate = 50000)
	public void checkDevices() throws ItemNotFoundRepositoryException {

		List<CompanyLicensePrivate> licensesOnline = licenseRepository.findByDeviceStatusOnline();

	}
}
