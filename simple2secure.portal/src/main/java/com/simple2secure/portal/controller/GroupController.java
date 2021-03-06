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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.GroupAccessRight;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputDestGroup;
import com.simple2secure.portal.validation.model.ValidInputGroup;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputUser;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.GROUP_API)
@Slf4j
public class GroupController extends BaseUtilsProvider {

	/**
	 * This function add new group to the group repository
	 *
	 * @param group
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> addGroup(@RequestBody CompanyGroup group, @ServerProvidedValue ValidInputUser userId,
			ValidInputGroup groupId, @ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (group != null && !Strings.isNullOrEmpty(userId.getValue()) && contextId.getValue() != null) {

			ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(contextId.getValue(),
					userId.getValue());
			if (group.getId() == null && contextUserAuthentication != null) {
				if (groupUtils.checkIfGroupNameIsAllowed(group.getName(), contextId.getValue())) {
					if (groupId.getValue() != null) {
						// THERE IS A PARENT GROUP!!
						CompanyGroup parentGroup = groupRepository.find(groupId.getValue());
						if (parentGroup != null) {

							group.setContextId(parentGroup.getContextId());
							group.setRootGroup(false);
							group.setParentId(groupId.getValue());
							ObjectId groupIdDb = groupRepository.saveAndReturnId(group);

							// If this is Superuser add new mapping between this superuser and group
							if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERUSER)) {
								GroupAccessRight groupAccessRight = new GroupAccessRight(contextUserAuthentication.getUserId(), groupIdDb,
										contextUserAuthentication.getContextId());
								groupAccessRightRepository.save(groupAccessRight);
							}

							parentGroup.addChildrenId(groupIdDb);
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
							ObjectId groupIdDb = groupRepository.saveAndReturnId(group);

							// If this is Superuser add new mapping between this superuser and group
							if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERUSER)) {
								GroupAccessRight groupAccessRight = new GroupAccessRight(contextUserAuthentication.getUserId(), groupIdDb,
										contextUserAuthentication.getContextId());
								groupAccessRightRepository.save(groupAccessRight);
							}

							return new ResponseEntity<>(group, HttpStatus.OK);
						}
					}
				} else {
					log.error("Group cannot contain the standard name");
					throw new ApiRequestException(messageByLocaleService.getMessage("problem_saving_group_standard_name", locale.getValue()));
				}

			} else {
				// UPDATING EXISTING GROUP

				groupRepository.update(group);
				return new ResponseEntity<>(group, HttpStatus.OK);
			}
		}
		log.error("Problem occured while saving/updating group");
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_group", locale.getValue()));
	}

	/**
	 * This function returns the group according to the group id
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> getGroup(@PathVariable ValidInputGroup groupId, @ServerProvidedValue ValidInputLocale locale) {
		if (groupId.getValue() != null) {
			CompanyGroup group = groupRepository.find(groupId.getValue());
			if (group != null) {
				return new ResponseEntity<>(group, HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving group with id {}", groupId.getValue());
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale.getValue()));
	}

	/**
	 * This function returns all groups according to the contextId
	 */
	@ValidRequestMapping(
			value = "/context")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<CompanyGroup>> getGroupsByContextId(@ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (contextId.getValue() != null) {
			Context context = contextRepository.find(contextId.getValue());
			if (context != null) {
				List<CompanyGroup> groups = groupRepository.findByContextId(context.getId());
				if (groups != null) {
					return new ResponseEntity<>(groups, HttpStatus.OK);
				}
			}
		}
		log.error("Problem occured while retrieving group for context with id {}", contextId);
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_retrieving_group", locale.getValue()));
	}

	/**
	 * This function returns all users from the user repository
	 */
	@ValidRequestMapping(
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<?> deleteGroup(@PathVariable ValidInputGroup groupId, @ServerProvidedValue ValidInputLocale locale) {

		if (groupId.getValue() != null) {
			CompanyGroup group = groupRepository.find(groupId.getValue());
			if (group != null) {
				if (!group.isStandardGroup()) {
					groupUtils.deleteGroup(groupId.getValue(), true);
					return new ResponseEntity<>(group, HttpStatus.OK);
				} else {
					throw new ApiRequestException(messageByLocaleService.getMessage("standard_group_delete_error", locale.getValue()));
				}
			}
		}
		log.error("Problem occured while deleting group with id {}", groupId.getValue());
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_group", locale.getValue()));
	}

	/**
	 * This function moves the group to the one which has been selected using drag&drop
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/move",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> groupDragAndDrop(@PathVariable ValidInputGroup groupId, @PathVariable ValidInputDestGroup destGroupId,
			@ServerProvidedValue ValidInputUser userId, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		CompanyGroup sourceGroup = groupRepository.find(groupId.getValue());
		CompanyGroup toGroup = groupRepository.find(destGroupId.getValue());

		if (sourceGroup != null) {
			return groupUtils.checkIfGroupCanBeMoved(sourceGroup, toGroup, userId.getValue(), locale);
		} else {
			log.error("Problem occured while moving group with id {}", groupId);
			throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_moving_group", locale.getValue()));
		}
	}

	@ValidRequestMapping(
			value = "/copy",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<CompanyGroup> copyGroupConfiguration(@RequestBody CompanyGroup destGroup, @PathVariable ValidInputGroup groupId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (destGroup != null && groupId.getValue() != null) {

			CompanyGroup sourceGroup = groupRepository.find(groupId.getValue());

			if (sourceGroup != null && destGroup != null) {
				if (destGroup.getId() != null) {

					// Cannot copy if both group IDs are same
					if (!groupId.getValue().equals(destGroup.getId())) {

						// Delete all configuration from destGroup
						groupUtils.deleteGroup(destGroup.getId(), false);

						// Copy all configurations from the source group to dest group
						groupUtils.copyGroupConfiguration(sourceGroup.getId(), destGroup.getId());

						return new ResponseEntity<>(sourceGroup, HttpStatus.OK);
					}
				}
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_copying_config", locale.getValue()));
	}
}
