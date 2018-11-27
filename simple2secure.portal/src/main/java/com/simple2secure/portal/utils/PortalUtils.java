/*
 * Copyright (c) 2017 Secinto GmbH This software is the confidential and proprietary information of Secinto GmbH. All rights reserved.
 * Secinto GmbH and its affiliates make no representations or warranties about the suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability, fitness for a particular purpose, or non-infringement. NXP B.V.
 * and its affiliates shall not be liable for any damages suffered by licensee as a result of using, modifying or distributing this software
 * or its derivatives. This copyright notice must appear in all copies of this software.
 */

package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Processor;
import com.simple2secure.portal.repository.GroupRepository;

@Component
public class PortalUtils {
	private static Logger log = LoggerFactory.getLogger(PortalUtils.class);

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	GroupRepository groupRepository;

	/**
	 * This function generates an activation token for each user
	 *
	 * @return
	 */
	public synchronized String generateToken() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * This function checks if processor with the provided name or class already exist in the database. New processor will be only added if it
	 * does not exist.
	 *
	 * @param processors
	 * @param processor
	 * @return
	 */
	public static boolean checkIfListAlreadyContainsProcessor(List<Processor> processors, Processor processor) {
		for (Processor processor_item : processors) {
			if (processor_item.getName().trim().equals(processor.getName().trim())) {
				return true;
			}
			if (processor_item.getProcessor_class().trim().equals(processor.getProcessor_class().trim())) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Check before each request if access token has expired
	 *
	 * @param expirationDate
	 * @return
	 */
	public boolean isAccessTokenExpired(Date expirationDate) {
		Date currentDate = new Date(System.currentTimeMillis());

		if (expirationDate.before(currentDate)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Converts the given TimeUnits time to milliseconds.
	 *
	 * @param time
	 *          The value of the time unit.
	 * @param timeUnit
	 *          The unit in which the time is measured.
	 * @return The specified amount of time in milliseconds.
	 */
	public long convertTimeUnitsToMilis(long time, TimeUnit timeUnit) {
		if (timeUnit != null) {
			return timeUnit.toMillis(time);
		} else {
			return 0;
		}
	}

	/**
	 * This function checks if this group has children groups
	 *
	 * @param group
	 * @return
	 */
	public static boolean groupHasChildren(CompanyGroup group) {
		if (group != null) {
			if (group.getChildrenIds() != null) {
				if (group.getChildrenIds().size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This function finds the parent group of the current child
	 *
	 * @param group
	 * @return
	 */
	public CompanyGroup findTheParentGroup(CompanyGroup group) {

		if (!Strings.isNullOrEmpty(group.getParentId())) {
			CompanyGroup parentGroup = groupRepository.find(group.getParentId());
			if (parentGroup != null) {
				return parentGroup;
			}
		}
		return null;
	}

	/**
	 * This function searches recursively for all dependent groups until the root group is found
	 *
	 * @param group
	 * @return
	 */
	public List<CompanyGroup> findAllParentGroups(CompanyGroup group) {
		List<CompanyGroup> foundGroups = new ArrayList<>();
		foundGroups.add(group);
		boolean rootGroupFound = false;
		while (!rootGroupFound) {
			CompanyGroup parentGroup = findTheParentGroup(group);
			if (parentGroup != null) {
				foundGroups.add(parentGroup);
				if (parentGroup.isRootGroup()) {
					rootGroupFound = true;
				} else {
					group = parentGroup;
				}
			} else {
				rootGroupFound = true;
			}
		}
		return foundGroups;
	}
}
