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

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.ChartData;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.GroupAccessRight;
import com.simple2secure.api.model.OsQueryGroupMapping;
import com.simple2secure.api.model.PieChartData;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseServiceProvider;
import com.simple2secure.portal.validation.model.ValidInputLocale;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Component
@Slf4j
public class GroupUtils extends BaseServiceProvider {

	/**
	 * This function adds new group for each new registered user
	 */
	public CompanyGroup createNewGroupRegistration(ObjectId contextId) {
		CompanyGroup group = new CompanyGroup(StaticConfigItems.STANDARD_GROUP_NAME, contextId, true, true);
		groupRepository.save(group);
		group = groupRepository.findStandardGroupByContextId(contextId);
		dataInitialization.mapActiveQueriesToDefaultGroup(group.getId());
		return group;
	}

	/**
	 * This function moves the group to the destination group. If source group was root group in this function it will be unset. Children to
	 * the destination group will be added in this function. If toGroup is null it means that fromGroup will be declared as new root group.
	 *
	 * @param fromGroup
	 * @param toGroup
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	public boolean moveGroup(CompanyGroup fromGroup, CompanyGroup toGroup) throws ItemNotFoundRepositoryException {
		if (toGroup != null) {
			CompanyGroup parentGroup = groupRepository.find(fromGroup.getParentId());
			if (parentGroup != null) {
				parentGroup.removeChildrenId(fromGroup.getId());
				groupRepository.update(parentGroup);

				fromGroup.setParentId(toGroup.getId());
				toGroup.addChildrenId(fromGroup.getId());

				groupRepository.update(fromGroup);
				groupRepository.update(toGroup);
				return true;
			}
			// This is the root group!
			else {
				if (fromGroup.isRootGroup()) {
					fromGroup.setParentId(toGroup.getId());
					fromGroup.setRootGroup(false);
					toGroup.addChildrenId(fromGroup.getId());

					groupRepository.update(fromGroup);
					groupRepository.update(toGroup);
					return true;
				}
				return false;
			}
		} else {
			CompanyGroup parentGroup = groupRepository.find(fromGroup.getParentId());
			if (parentGroup != null) {
				parentGroup.removeChildrenId(fromGroup.getId());
				groupRepository.update(parentGroup);

				fromGroup.setParentId(null);
				fromGroup.setRootGroup(true);

				groupRepository.update(fromGroup);
				return true;
			}
			// In other case no need to move because this is already root group
			return false;
		}
	}

	/**
	 * This function deletes group and all dependencies of this group
	 *
	 * @param groupId
	 */
	public void deleteGroup(ObjectId groupId, boolean deleteAll) {

		// Remove OSQuery configuration
		queryGroupMappingRepository.deleteByGroupId(groupId);

		// if this flag is set all group dependencies will be deleted
		if (deleteAll) {
			// Delete all licenses and all probe reports which were created
			List<CompanyLicensePrivate> licenses = licenseRepository.findAllByGroupId(groupId);

			if (licenses != null) {
				for (CompanyLicensePrivate license : licenses) {
					if (license.getDeviceId() != null) {
						reportsRepository.deleteByDeviceId(license.getDeviceId());
						networkReportRepository.deleteByDeviceId(license.getDeviceId());
					}
					licenseRepository.delete(license);
				}
			}

			// Remove GroupAccessRights
			groupAccessRightRepository.deleteByGroupId(groupId);

			deleteGroupFromChildren(groupId);
		}

	}

	/**
	 * This function is used to delete all groups and its dependencies according to the contextId
	 *
	 * @param contextId
	 */
	public void deleteGroupsByContextId(ObjectId contextId) {
		if (contextId != null) {
			List<CompanyGroup> groups = groupRepository.findByContextId(contextId);
			if (groups != null) {
				for (CompanyGroup group : groups) {
					deleteGroup(group.getId(), true);
				}
			}
		}
	}

	/**
	 * This function iterates over the group children and deletes them accordingly
	 *
	 * @param groupId
	 */
	private void deleteGroupFromChildren(ObjectId groupId) {
		CompanyGroup group = groupRepository.find(groupId);

		if (group != null) {
			if (!group.isRootGroup()) {
				CompanyGroup parent = groupRepository.find(group.getParentId());

				if (parent != null) {
					parent.removeChildrenId(groupId);
					try {
						groupRepository.update(parent);
					} catch (ItemNotFoundRepositoryException e) {
						log.error("Parent group not found {}", e);
					}
				}

				if (groupHasChildren(group)) {
					deleteGroupChildren(group);
				}
			} else {
				if (groupHasChildren(group)) {
					deleteGroupChildren(group);
				}
			}

			if (!group.isStandardGroup()) {
				groupRepository.delete(group);
			}

		}
	}

	/**
	 * This function checks if group has children, iterates over them and deletes their children and own children. At the end this group is
	 * also deleted
	 *
	 * @param parentGroup
	 */
	private void deleteGroupChildren(CompanyGroup parentGroup) {
		List<CompanyGroup> children = getGroupChildren(parentGroup);

		for (CompanyGroup group : children) {
			if (groupHasChildren(group)) {
				deleteGroupChildren(group);
				deleteGroup(group.getId(), true);
			} else {
				deleteGroup(group.getId(), true);
			}
		}
	}

	/**
	 * This function returns the children of the certain group
	 *
	 * @param group
	 * @return
	 */
	private List<CompanyGroup> getGroupChildren(CompanyGroup group) {
		if (groupHasChildren(group)) {
			return groupRepository.findByParentId(group.getId());
		} else {
			return null;
		}
	}

	/**
	 * This function is used to move the group. It iterates over the root groups. It is always called recursively to get the children of this
	 * root group, and builds the correct structure to show it in the web.
	 *
	 * @param root
	 * @return
	 */
	public CompanyGroup groupTraverse(CompanyGroup root) {
		List<CompanyGroup> myChildren = new ArrayList<>();
		List<CompanyGroup> allChildren = groupRepository.findByParentId(root.id);
		if (allChildren != null && allChildren.size() > 0) {
			for (CompanyGroup child : allChildren) {
				if (groupHasChildren(child)) {
					List<CompanyGroup> children = getGroupChildren(child);
					child.setChildren(children);
					myChildren.add(child);

					for (CompanyGroup childItem : children) {
						groupTraverse(childItem);
					}
				} else {
					myChildren.add(child);
				}
			}
		}
		root.setChildren(myChildren);
		return root;
	}

	/**
	 * This function checks the user role, which wants to move the group. Superadmins can move every group. Superusers can move only assigned
	 * groups(both fromGroup and toGroup must be assigned to the super user). Admin can move every group if it belongs to the context to which
	 * this admin belongs.
	 *
	 * @param fromGroup
	 * @param toGroup
	 * @param user
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */

	public ResponseEntity<CompanyGroup> checkIfGroupCanBeMoved(CompanyGroup fromGroup, CompanyGroup toGroup, String userId,
			ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (fromGroup != null) {
			ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(fromGroup.getContextId(),
					userId);

			if (contextUserAuthentication != null) {
				// SUPERADMIN
				if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERADMIN)) {
					// SUPERADMIN can move everything
					if (moveGroup(fromGroup, toGroup)) {
						return new ResponseEntity<>(fromGroup, HttpStatus.OK);
					}
				}
				// SUPERUSER
				else if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERUSER)) {
					// Move only if superuser belongs to the both groups(both groups are assigned to him)

					GroupAccessRight groupAccessRight = groupAccessRightRepository.findByGroupIdAndUserId(fromGroup.getId(), new ObjectId(userId));
					if (groupAccessRight != null) {
						// in this case the moved group will be root group
						if (toGroup == null) {
							if (moveGroup(fromGroup, toGroup)) {
								return new ResponseEntity<>(fromGroup, HttpStatus.OK);
							}
						} else {
							// check if toGroup is assigned to this user
							groupAccessRight = groupAccessRightRepository.findByGroupIdAndUserId(toGroup.getId(), new ObjectId(userId));
							if (groupAccessRight != null) {
								if (moveGroup(fromGroup, toGroup)) {
									return new ResponseEntity<>(fromGroup, HttpStatus.OK);
								}
							}
						}
					}
				}
				// ADMIN
				else if (contextUserAuthentication.getUserRole().equals(UserRole.ADMIN)) {

					if (fromGroup.getContextId() != null) {
						// check if contextId of the from Group is same as contextId assigned to the user

						if (contextUserAuthentication != null) {
							// In case that toGroup is null, fromGroup will be new root group
							if (toGroup == null) {
								if (moveGroup(fromGroup, toGroup)) {
									return new ResponseEntity<>(fromGroup, HttpStatus.OK);
								}
							} else {
								if (toGroup.getContextId() != null) {
									// Check if fromGroup contextId is same as toGroup contextId. If both are same move the group
									if (fromGroup.getContextId().equals(toGroup.getContextId())) {
										if (moveGroup(fromGroup, toGroup)) {
											return new ResponseEntity<>(fromGroup, HttpStatus.OK);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_moving_group", locale.getValue()));
	}

	/**
	 * This function returns the groupIds of the superuser where he has been assigned to.
	 *
	 * @param context
	 * @param user
	 * @return
	 */
	public List<ObjectId> getAllAssignedGroupIdsForSuperUser(Context context, String userId) {
		List<ObjectId> groupIds = new ArrayList<>();

		List<GroupAccessRight> accessRightList = groupAccessRightRepository.findByContextIdAndUserId(context.getId(), new ObjectId(userId));

		if (accessRightList != null) {
			for (GroupAccessRight accessRight : accessRightList) {
				if (accessRight != null) {
					if (checkIfGroupExists(accessRight.getGroupId())) {
						groupIds.add(accessRight.getGroupId());
					}
				}
			}
		}

		return groupIds;
	}

	/**
	 * This function is used to generate the correct structure which will be shown in web
	 *
	 * @param context
	 * @return
	 */
	public List<CompanyGroup> getAllGroupsByContextId(Context context) {
		List<CompanyGroup> myGroupsWithChildren = new ArrayList<>();
		List<CompanyGroup> myGroups = groupRepository.findRootGroupsByContextId(context.getId());
		if (myGroups == null) {
			myGroups = new ArrayList<>();
		} else {
			for (CompanyGroup group : myGroups) {
				if (!groupHasChildren(group)) {
					myGroupsWithChildren.add(group);
				} else {
					myGroupsWithChildren.add(groupTraverse(group));
				}
			}
		}
		return myGroupsWithChildren;
	}

	/**
	 * This function checks if the groups exists in the database according to the provided groupId
	 *
	 * @param groupId
	 * @return
	 */
	public boolean checkIfGroupExists(ObjectId groupId) {
		if (groupId != null) {
			CompanyGroup group = groupRepository.find(groupId);
			if (group != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This function copies the configuration from the source group to the destination group
	 *
	 * @param sourceGroupId
	 * @param destGroupId
	 */
	public void copyGroupConfiguration(ObjectId sourceGroupId, ObjectId destGroupId) {

		List<OsQueryGroupMapping> queryMappings = queryGroupMappingRepository.findByGroupId(sourceGroupId);

		if (queryMappings != null) {
			for (OsQueryGroupMapping mapping : queryMappings) {
				mapping.setGroupId(destGroupId);
				mapping.setId(null);
				queryGroupMappingRepository.save(mapping);
			}
		}
	}

	/**
	 * This function checks if the current group name is allowed. Only standard group (which is added automatically) can be named as STANDARD.
	 *
	 * @param groupName
	 * @return
	 */
	public boolean checkIfGroupNameIsAllowed(String groupName, ObjectId contextId) {

		if (!Strings.isNullOrEmpty(groupName)) {
			if (!groupName.toLowerCase().trim().equals(StaticConfigItems.STANDARD_GROUP_NAME.toLowerCase().trim())) {
				List<CompanyGroup> groups = groupRepository.findByContextId(contextId);
				if (groups != null) {
					for (CompanyGroup group : groups) {
						if (group != null) {
							if (group.getName().toLowerCase().trim().equals(groupName.toLowerCase().trim())) {
								return false;
							}
						}
					}
				}
				return true;
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

		if (group.getParentId() != null) {
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

	/**
	 * This function checks if this group has children groups
	 *
	 * @param group
	 * @return
	 */
	private boolean groupHasChildren(CompanyGroup group) {
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
	 * This is only a temp function to retrieve some data for the line and bar chart widget.
	 *
	 * @param context
	 * @param tag
	 * @return
	 */
	public ChartData getLicenseDownloadsForContext(Context context, String tag) {

		List<CompanyGroup> contextGroups = getAllGroupsByContextId(context);

		List<String> labels = new ArrayList<>();
		List<List<Integer>> series = new ArrayList<>();
		List<Integer> seriesOfData = new ArrayList<>();
		if (contextGroups != null) {
			for (CompanyGroup group : contextGroups) {
				labels.add(group.getName());
				seriesOfData.add(context.getCurrentNumberOfLicenseDownloads());
			}
		}
		series.add(seriesOfData);

		ChartData chartData = new ChartData(labels, series);

		if (tag.equals(StaticConfigItems.WIDGET_API_GET_NUMBER_OF_LICENSE)) {
			chartData.setSeriesPieChart(seriesOfData);
		}
		return chartData;
	}

	/**
	 * This is only a temp function to retrieve some data for the pie chart widget.
	 *
	 * @param context
	 * @return
	 */
	public PieChartData getDataForPieChart(Context context) {
		List<CompanyGroup> contextGroups = getAllGroupsByContextId(context);
		List<Integer> series = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		if (contextGroups != null) {
			for (CompanyGroup group : contextGroups) {
				labels.add(group.getName());
				series.add(context.getCurrentNumberOfLicenseDownloads());
			}
		}

		PieChartData pieChartData = new PieChartData(series, labels);
		return pieChartData;

	}
}
