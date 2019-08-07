package com.simple2secure.portal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Notification;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NotificationRepository;

@Component
public class NotificationUtils {

	private static Logger log = LoggerFactory.getLogger(NotificationUtils.class);

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	GroupRepository groupRepository;

	public void addNewNotificationPortal(String data, String contextId) {
		Notification notification = new Notification(data, false, contextId, System.currentTimeMillis());
		notificationRepository.save(notification);
		log.info("New notification has been added");
	}

	public boolean addNewNotificationPod(String data, String podId) {

		CompanyLicensePrivate license = licenseRepository.findByPodId(podId);

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
