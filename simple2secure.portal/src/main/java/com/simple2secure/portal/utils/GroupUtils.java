package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.ConfigRepository;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.NetworkReportRepository;
import com.simple2secure.portal.repository.ProcessorRepository;
import com.simple2secure.portal.repository.QueryRepository;
import com.simple2secure.portal.repository.ReportRepository;
import com.simple2secure.portal.repository.StepRepository;
import com.simple2secure.portal.service.MessageByLocaleService;

@Component
public class GroupUtils {

	private static Logger log = LoggerFactory.getLogger(GroupUtils.class);

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	StepRepository stepRepository;

	@Autowired
	ProcessorRepository processorRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	NetworkReportRepository networkReportRepository;

	@Autowired
	QueryRepository queryRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	/**
	 * This function copies the groups from the user which is being deleted to the privileged one
	 *
	 * @param privilegedUser
	 * @param user
	 */
	public void copyMyGroupsToPrivilegedUser(User privilegedUser, User user) {
		// List<CompanyGroup> groups = groupRepository.findByOwnerId(user.getId());
		//
		// if (groups != null) {
		// for (CompanyGroup group : groups) {
		// // group.setAddedByUserId(privilegedUser.getId());
		// // group.setOwner(privilegedUser.getEmail());
		// try {
		// groupRepository.update(group);
		// } catch (ItemNotFoundRepositoryException e) {
		// log.error("Group not found");
		// }
		// }
		// }
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
	public void deleteGroup(String groupId) {
		// Delete the group configurations
		configRepository.deleteByGroupId(groupId);

		// Delete the group steps
		stepRepository.deleteByGroupId(groupId);

		// Delete the group processors
		processorRepository.deleteByGroupId(groupId);

		// Delete all licenses and all probe reports which were created
		List<CompanyLicensePrivate> licenses = licenseRepository.findByGroupId(groupId);

		if (licenses != null) {
			for (CompanyLicensePrivate license : licenses) {
				if (!Strings.isNullOrEmpty(license.getProbeId())) {
					reportRepository.deleteByProbeId(license.getProbeId());
					networkReportRepository.deleteByProbeId(license.getProbeId());
				}
				licenseRepository.delete(license);
			}
		}

		// Remove OSQuery configuration
		queryRepository.deleteByGroupId(groupId);

		deleteGroupFromChildren(groupId);
	}

	/**
	 * This function iterates over the group children and deletes them accordingly
	 *
	 * @param groupId
	 */
	private void deleteGroupFromChildren(String groupId) {
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

				if (PortalUtils.groupHasChildren(group)) {
					deleteGroupChildren(group);
				}
			} else {
				if (PortalUtils.groupHasChildren(group)) {
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
			if (PortalUtils.groupHasChildren(group)) {
				deleteGroupChildren(group);
				deleteGroup(group.getId());
			} else {
				deleteGroup(group.getId());
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
		if (PortalUtils.groupHasChildren(group)) {
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
				if (PortalUtils.groupHasChildren(child)) {
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<CompanyGroup> checkIfGroupCanBeMoved(CompanyGroup fromGroup, CompanyGroup toGroup, User user, String locale)
			throws ItemNotFoundRepositoryException {

		if (fromGroup != null) {
			ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository.getByContextIdAndUserId(fromGroup.getContextId(),
					user.getId());

			if (contextUserAuthentication != null) {
				// SUPERADMIN
				if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERADMIN)) {
					// SUPERADMIN can move everything
					if (moveGroup(fromGroup, toGroup)) {
						return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
					}
				}
				// SUPERUSER
				else if (contextUserAuthentication.getUserRole().equals(UserRole.SUPERUSER)) {
					// Move only if superuser belongs to the both groups(both groups are assigned to him)
					if (fromGroup.getSuperUserIds().contains(user.getId())) {
						// in this case the moved group will be root group
						if (toGroup == null) {
							if (moveGroup(fromGroup, toGroup)) {
								return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
							}
						} else {
							// check if toGroup is assigned to this user
							if (toGroup.getSuperUserIds().contains(user.getId())) {
								if (moveGroup(fromGroup, toGroup)) {
									return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
								}
							}
						}
					}
				}
				// ADMIN
				else if (contextUserAuthentication.getUserRole().equals(UserRole.ADMIN)) {

					if (!Strings.isNullOrEmpty(fromGroup.getContextId())) {
						// check if contextId of the from Group is same as contextId assigned to the user

						if (contextUserAuthentication != null) {
							// In case that toGroup is null, fromGroup will be new root group
							if (toGroup == null) {
								if (moveGroup(fromGroup, toGroup)) {
									return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
								}
							} else {
								if (!Strings.isNullOrEmpty(toGroup.getContextId())) {
									// Check if fromGroup contextId is same as toGroup contextId. If both are same move the group
									if (fromGroup.getContextId().equals(toGroup.getContextId())) {
										if (moveGroup(fromGroup, toGroup)) {
											return new ResponseEntity<CompanyGroup>(fromGroup, HttpStatus.OK);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_moving_group", locale)),
				HttpStatus.NOT_FOUND);
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
				if (!PortalUtils.groupHasChildren(group)) {
					myGroupsWithChildren.add(group);
				} else {
					myGroupsWithChildren.add(groupTraverse(group));
				}
			}
		}
		return myGroupsWithChildren;
	}

}
