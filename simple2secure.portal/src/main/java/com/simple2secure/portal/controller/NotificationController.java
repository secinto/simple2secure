package com.simple2secure.portal.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.simple2secure.api.model.Notification;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.NotificationRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@RestController
public class NotificationController {

	@Autowired
	private NotificationRepository repository;
	
    @Autowired
    MessageByLocaleService messageByLocaleService;

	public static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

	@RequestMapping(value = "/api/notification", method = RequestMethod.POST, consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Notification> saveNotification(@RequestBody Notification notification, @RequestHeader("Accept-Language") String locale) {
		this.repository.save(notification);
		return new ResponseEntity<Notification>(notification, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/api/notification/{user_id}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<Notification>> getNoficitaionByUserID(@PathVariable("user_id") String userId, @RequestHeader("Accept-Language") String locale) {
		List<Notification> notifications = this.repository.findByUserId(userId);
		if(notifications == null) {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("error_while_getting_notifications", locale)), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<List<Notification>>(notifications, HttpStatus.OK);
	}
}
