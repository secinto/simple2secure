/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.GroupAccessRight;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupAccesRightRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.GroupUtils;

@RestController
@RequestMapping("/api/group")
public class GroupController {

	static final Logger log = LoggerFactory.getLogger(GroupController.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ContextRepository contextRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	GroupAccesRightRepository groupAccessRightRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	GroupUtils groupUtils;

	/**
	 * This function add new group to the group repository
	 *
	 * @param group
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{userId}/{parentGroupId}/{contextId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> addGroup(@RequestBody CompanyGroup group, @PathVariable("userId") String userId,
			@PathVariable("parentGroupId") String parentGroupId, @PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (group != null && !Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(contextId)) {
			User user = userRepository.find(userId);
			ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(contextId, userId);
			if (Strings.isNullOrEmpty(group.getId()) && user != null && contextUserAuthentication != null) {
				if (groupUtils.checkIfGroupNameIsAllowed(group.getName())) {
					if (!parentGroupId.equals("null")) {
						// THERE IS A PARENT GROUP!!
						CompanyGroup parentGroup = groupRepository.find(parentGroupId);
						if (parentGroup != null) {

							group.setContextId(parentGroup.getContextId());
							group.setRootGroup(false);
							group.setParentId(parentGroupId);
							ObjectId groupId = groupRepository.saveAndReturnId(group);

							// If this is Superuser add new mapping between this superuser and group
							if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERUSER)) {
								GroupAccessRight groupAccessRight = new GroupAccessRight(contextUserAuthentication.getUserId(), groupId.toString(),
										contextUserAuthentication.getContextId());
								groupAccessRightRepository.save(groupAccessRight);
							}

							parentGroup.addChildrenId(groupId.toString());
							groupRepository.update(parentGroup);
							return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
						}
					} else {
						// NEW PARENT GROUP!
						//
						Context context = contextRepository.find(contextId);
						if (context != null) {

							group.setContextId(context.getId());
							group.setRootGroup(true);
							ObjectId groupId = groupRepository.saveAndReturnId(group);

							// If this is Superuser add new mapping between this superuser and group
							if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERUSER)) {
								GroupAccessRight groupAccessRight = new GroupAccessRight(contextUserAuthentication.getUserId(), groupId.toString(),
										contextUserAuthentication.getContextId());
								groupAccessRightRepository.save(groupAccessRight);
							}

							return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
						}
					}
				}

			} else {
				// UPDATING EXISTING GROUP

				groupRepository.update(group);
				return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
			}
		}
		log.error("Problem occured while saving/updating group");
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{groupID}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> getGroup(@PathVariable("groupID") String groupId, @RequestHeader("Accept-Language") String locale) {
		if (!Strings.isNullOrEmpty(groupId)) {
			CompanyGroup group = groupRepository.find(groupId);
			if (group != null) {
				return new ResponseEntity<CompanyGroup>(group, HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving group with id {}", groupId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<CompanyGroup>> getGroupsByContextId(@PathVariable("contextId") String contextId,
			@RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(contextId)) {
			Context context = contextRepository.find(contextId);
			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(context.getId());
				if (groups != null) {
					return new ResponseEntity<List<CompanyGroup>>(groups, HttpStatus.OK);
				}
			}
		}
		log.error("Problem occured while retrieving group for context with id {}", contextId);
		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{groupID}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<?> deleteGroup(@PathVariable("groupID") String groupId, @RequestHeader("Accept-Language") String locale) {

		if (!Strings.isNullOrEmpty(groupId)) {
			CompanyGroup group = groupRepository.find(groupId);
			if (group != null) {
				if (!group.isStandardGroup()) {
					groupUtils.deleteGroup(groupId, true);
					return new ResponseEntity<>(group, HttpStatus.OK);
				} else {
					return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("standard_group_delete_error", locale)),
							HttpStatus.NOT_FOUND);
				}
			}
		}
		log.error("Problem occured while deleting group with id {}", groupId);
		return new ResponseEntity<>(
				new CustomErrorType(
						messageByLocaleService.getMessage("problem_occured_while_deleting_group", ObjectUtils.toObjectArray(groupId), locale)),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function moves the group to the one which has been selected using drag&drop
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/move/{sourceGroupId}/{destGroupId}/{userId}", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> groupDragAndDrop(@PathVariable("sourceGroupId") String sourceGroupId,
			@PathVariable("destGroupId") String destGroupId, @PathVariable("userId") String userId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		CompanyGroup sourceGroup = groupRepository.find(sourceGroupId);
		CompanyGroup toGroup = groupRepository.find(destGroupId);
		User user = userRepository.find(userId);

		if (sourceGroup != null && user != null) {
			return groupUtils.checkIfGroupCanBeMoved(sourceGroup, toGroup, user, locale);
		} else {
			log.error("Problem occured while moving group with id {}", sourceGroupId);
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_moving_group", locale)),
					HttpStatus.NOT_FOUND);
		}
	}
}
