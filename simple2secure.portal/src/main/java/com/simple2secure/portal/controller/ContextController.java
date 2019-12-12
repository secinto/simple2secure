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
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.simple2secure.api.dto.ContextDTO;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.api.model.LicensePlan;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.providers.BaseUtilsProvider;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputUser;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.CONTEXT_API)
public class ContextController extends BaseUtilsProvider {

	public static final Logger log = LoggerFactory.getLogger(ContextController.class);

	/**
	 * This function adds new context (This is only possible for admins or superadmins)
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/add",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")

	public ResponseEntity<Context> addContext(@ServerProvidedValue ValidInputUser userId, @ServerProvidedValue ValidInputContext contextId,
			@RequestBody Context context, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(userId.getValue()) && !Strings.isNullOrEmpty(contextId.getValue()) && context != null) {

			// Update context
			if (!Strings.isNullOrEmpty(context.getId())) {
				contextRepository.update(context);
				return new ResponseEntity<>(context, HttpStatus.OK);
			} else {
				// Add new context
				if (!contextUtils.checkIfContextAlreadyExists(context, userId.getValue())) {
					Context currentContext = contextRepository.find(contextId.getValue());
					User currentUser = userRepository.find(userId.getValue());
					ContextUserAuthentication contextUserAuth = contextUserAuthRepository.getByContextIdAndUserId(contextId.getValue(),
							userId.getValue());
					if (currentContext != null && currentUser != null && contextUserAuth != null) {
						LicensePlan licensePlan = licensePlanRepository.findByName(StaticConfigItems.DEFAULT_LICENSE_PLAN);
						if (licensePlan != null) {

							context.setLicensePlanId(licensePlan.getId());
							ObjectId savedContextId = contextRepository.saveAndReturnId(context);

							if (savedContextId != null) {
								ContextUserAuthentication contextUserAuthenticationNew = new ContextUserAuthentication(userId.getValue(),
										savedContextId.toString(), contextUserAuth.getUserRole(), false);
								contextUserAuthRepository.save(contextUserAuthenticationNew);

								// add standard group for current context
								try {
									dataInitialization.addDefaultGroup(userId.getValue(), savedContextId.toString());
									contextUtils.mapSuperAdminsTotheContext(savedContextId.toString());
									return new ResponseEntity<>(context, HttpStatus.OK);
								} catch (IOException e) {
									log.error(e.getMessage());
									return new ResponseEntity<>(
											new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue())),
											HttpStatus.NOT_FOUND);
								}

							}
						}
					}
				} else {
					log.error("Context {} already exist", context.getName());
					return new ResponseEntity<>(
							new CustomErrorType(messageByLocaleService.getMessage("problem_occured_context_exists", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}
			}
		}
		log.error("Problem occured while adding context");
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function returns all context for the provided user
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'LOGINUSER')")
	public ResponseEntity<List<ContextDTO>> getContextsByUserId(@ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputLocale locale) {

		if (!Strings.isNullOrEmpty(userId.getValue())) {
			List<ContextUserAuthentication> contextUserAuthList = contextUserAuthRepository.getByUserId(userId.getValue());
			if (contextUserAuthList != null) {
				List<ContextDTO> contextList = new ArrayList<>();

				for (ContextUserAuthentication contextUserAuth : contextUserAuthList) {
					Context context = contextRepository.find(contextUserAuth.getContextId());
					if (context != null) {
						ContextDTO contextDTO = new ContextDTO(context, contextUserAuth.getUserRole());
						contextList.add(contextDTO);
					}
				}
				return new ResponseEntity<>(contextList, HttpStatus.OK);
			}
		}
		log.error("Problem occured while retrieving contexts for user ID {}" + userId.getValue());
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function deletes the selected Context
	 *
	 * @param userId
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/delete",
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<Context> deleteContext(@ServerProvidedValue ValidInputUser userId, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(contextId.getValue()) && !Strings.isNullOrEmpty(userId.getValue())) {
			Context context = contextRepository.find(contextId.getValue());
			User user = userRepository.find(userId.getValue());

			if (context != null && user != null) {
				if (contextUtils.checkIfUserCanDeleteContext(user, context)) {
					// call delete context dependencies
					contextUtils.deleteContextDependencies(context);
					return new ResponseEntity<>(context, HttpStatus.OK);
				} else {
					// User not allowed to delete
					log.error("{} not allowed to delete this default context {}", user.getEmail(), context.getName());
					return new ResponseEntity<>(
							new CustomErrorType(messageByLocaleService.getMessage("not_allowed_to_delete_this_context", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}

			}

		}
		log.error("Problem occured while deleting context {}" + contextId);
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_context", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	/**
	 * This function updates the current user context or sets new one if the user was not logged in
	 *
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER', 'LOGINUSER')")

	public ResponseEntity<CurrentContext> selectUserContext(@ServerProvidedValue ValidInputUser userId, @RequestBody Context context,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(userId.getValue()) && context != null) {
			ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(context.getId(),
					userId.getValue());

			if (contextUserAuthentication != null) {
				CurrentContext currentContext = currentContextRepository.findByUserId(userId.getValue());

				if (currentContext != null) {
					currentContext.setContextUserAuthenticationId(contextUserAuthentication.getId());
					currentContextRepository.update(currentContext);
				} else {
					currentContext = new CurrentContext(userId.getValue(), contextUserAuthentication.getId());
					currentContextRepository.save(currentContext);
				}

				return new ResponseEntity<>(currentContext, HttpStatus.OK);
			}
		}
		log.error("Problem occured while updating/creating context");
		return new ResponseEntity<>(new CustomErrorType(messageByLocaleService.getMessage("unknown_error_occured", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

}