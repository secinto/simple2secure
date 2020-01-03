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

package com.simple2secure.portal.utils;

import org.springframework.stereotype.Component;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Notification;
import com.simple2secure.portal.providers.BaseServiceProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationUtils extends BaseServiceProvider {

	public void addNewNotificationPortal(String data, String contextId) {
		Notification notification = new Notification(data, false, contextId, System.currentTimeMillis());
		notificationRepository.save(notification);
		log.info("New notification has been added");
	}

	public boolean addNewNotificationPod(String data, String deviceId) {

		CompanyLicensePrivate license = licenseRepository.findByDeviceId(deviceId);

		if (license != null) {

			CompanyGroup group = groupRepository.find(license.getGroupId());

			if (group != null) {
				Notification notification = new Notification(data, false, group.getContextId(), System.currentTimeMillis());
				notificationRepository.save(notification);
				log.info("New pod notification has been added");
			}
		}
		log.error("Problem occurred by adding new notification from the pod");
		return false;
	}

}
