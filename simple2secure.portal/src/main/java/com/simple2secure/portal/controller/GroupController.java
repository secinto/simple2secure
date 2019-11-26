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
import com.simple2secure.api.model.ValidInputContext;
import com.simple2secure.api.model.ValidInputLocale;
import com.simple2secure.api.model.ValidInputUser;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ContextRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupAccesRightRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.GroupUtils;
import com.simple2secure.portal.validator.ValidInput;
import com.simple2secure.portal.validator.ValidRequestMapping;

@RestController
@RequestMapping(StaticConfigItems.GROUP_API)
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
	public ResponseEntity<CompanyGroup> addGroup(@RequestBody CompanyGroup group, @ValidInput ValidInputUser userId,
			@PathVariable String parentGroupId, @ValidInput ValidInputContext contextId, @ValidInput ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (group != null && !Strings.isNullOrEmpty(userId.getValue()) && !Strings.isNullOrEmpty(contextId.getValue())) {
			User user = userRepository.find(userId.getValue());
			ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(contextId.getValue(),
					userId.getValue());
			if (Strings.isNullOrEmpty(group.getId()) && user != null && contextUserAuthentication != null) {
				if (groupUtils.checkIfGroupNameIsAllowed(group.getName(), contextId.getValue())) {
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
							return new ResponseEntity<>(group, HttpStatus.OK);
						}
					} else {
						// NEW PARENT GROUP!
						//
						Context context = contextRepository.find(contextId.getValue());
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

							return new ResponseEntity<>(group, HttpStatus.OK);
						}
					}
				} else {
					log.error("Group cannot contain the standard name");
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("problem_saving_group_standard_name", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}

			} else {
				// UPDATING EXISTING GROUP

				groupRepository.update(group);
				return new ResponseEntity<>(group, HttpStatus.OK);
			}
		}
		log.error("Problem occured while saving/updating group");
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns the group according to the group id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{groupID}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> getGroup(@PathVariable String groupId, @ValidInput ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(groupId)) {
			CompanyGroup group = groupRepository.find(groupId);
			if (group != null) {
				return new ResponseEntity<>(group, HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving group with id {}", groupId);
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This function returns all groups according to the contextId
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/context/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<CompanyGroup>> getGroupsByContextId(@ValidInput ValidInputContext contextId,
			@ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(context.getId());
				if (groups != null) {
					return new ResponseEntity<>(groups, HttpStatus.OK);
				}
			}
		}
		log.error("Problem occured while retrieving group for context with id {}", contextId);
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all groups according to the contextId
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{userId}/{contextId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<List<CompanyGroup>> getGroupsByContextAndUserId(@ValidInput ValidInputContext contextId,
			@ValidInput ValidInputUser userId, @ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(contextId.getValue()) && !Strings.isNullOrEmpty(userId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			User user = userRepository.find(userId.getValue());
			if (context != null && user != null) {
				ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(contextId.getValue(),
						userId.getValue());
				if (contextUserAuthentication != null) {

					// TODO: check according to the user role which groups will be visible to the user
					List<CompanyGroup> groups = groupRepository.findByContextId(contextId.getValue());
					if (groups != null) {
						return new ResponseEntity<>(groups, HttpStatus.OK);
					}
				}
			}
		}
		log.error("Problem occured while retrieving group for context with id {}", contextId);
		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all users from the user repository
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/{groupID}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<?> deleteGroup(@PathVariable String groupId, @ValidInput ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(groupId)) {
			CompanyGroup group = groupRepository.find(groupId);
			if (group != null) {
				if (!group.isStandardGroup()) {
					groupUtils.deleteGroup(groupId, true);
					return new ResponseEntity<>(group, HttpStatus.OK);
				} else {
					return new ResponseEntity(
							new CustomErrorType(messageByLocaleService.getMessage("standard_group_delete_error", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}
			}
		}
		log.error("Problem occured while deleting group with id {}", groupId);
		return new ResponseEntity<>(new CustomErrorType(
				messageByLocaleService.getMessage("problem_occured_while_deleting_group", ObjectUtils.toObjectArray(groupId), locale.getValue())),
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
	public ResponseEntity<CompanyGroup> groupDragAndDrop(@PathVariable String sourceGroupId, @PathVariable String destGroupId,
			@ValidInput ValidInputUser userId, @ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		CompanyGroup sourceGroup = groupRepository.find(sourceGroupId);
		CompanyGroup toGroup = groupRepository.find(destGroupId);
		User user = userRepository.find(userId.getValue());

		if (sourceGroup != null && user != null) {
			return groupUtils.checkIfGroupCanBeMoved(sourceGroup, toGroup, user, locale.getValue());
		} else {
			log.error("Problem occured while moving group with id {}", sourceGroupId);
			return new ResponseEntity(
					new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_moving_group", locale.getValue())),
					HttpStatus.NO_CONTENT);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ValidRequestMapping(value = "/copy", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> copyGroupConfiguration(@RequestBody CompanyGroup destGroup, @PathVariable String sourceGroupId,
			@ValidInput ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (destGroup != null && !Strings.isNullOrEmpty(sourceGroupId)) {

			CompanyGroup sourceGroup = groupRepository.find(sourceGroupId);

			if (sourceGroup != null && destGroup != null) {
				if (!Strings.isNullOrEmpty(destGroup.getId())) {

					// Cannot copy if both group IDs are same
					if (!sourceGroupId.equals(destGroup.getId())) {

						// Delete all configuration from destGroup
						groupUtils.deleteGroup(destGroup.getId(), false);

						// Copy all configurations from the source group to dest group
						groupUtils.copyGroupConfiguration(sourceGroup.getId(), destGroup.getId());

						return new ResponseEntity<>(sourceGroup, HttpStatus.OK);
					}
				}
			}
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_copying_config", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}
}
