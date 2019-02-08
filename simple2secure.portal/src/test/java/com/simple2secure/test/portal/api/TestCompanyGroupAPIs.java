package com.simple2secure.test.portal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.GroupAccessRight;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.GroupAccesRightRepository;
import com.simple2secure.portal.repository.GroupRepository;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestCompanyGroupAPIs extends TestAPIBase {

	private static Logger log = LoggerFactory.getLogger(TestCompanyGroupAPIs.class);

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupAccesRightRepository groupAccessRightRepository;

	@Autowired
	private ContextUserAuthRepository contextUserAuthRepo;

	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testCreateRootGroupPositive() {

		// We need to create CompanyGroup object and make an API call with it
		CompanyGroup group = new CompanyGroup("Test Group", new ArrayList<>());

		// API call to create root group
		String url = loadedConfigItems.getUsersAPI() + "/group/" + getAdminUser().getId() + "/null";
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<CompanyGroup>(group, createHttpHeaders(UserRole.ADMIN)), CompanyGroup.class);

		// Added group should be returned in the response
		assertNotNull(response);
		// Status 200 should be returned
		assertEquals(200, response.getStatusCodeValue());
		// Returned group should be root group
		assertTrue(response.getBody().isRootGroup());
	}

	@Test
	public void testCreateSubGroupPositive() {

		List<ContextUserAuthentication> contextUserAuth = contextUserAuthRepo.getByUserId(getAdminUser().getId());

		CompanyGroup parentGroup = new CompanyGroup("Parent Group", new ArrayList<>());
		parentGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId parentGroupId = groupRepository.saveAndReturnId(parentGroup);

		// We need to create CompanyGroup object and make an API call with it
		CompanyGroup group = new CompanyGroup("Test Group", new ArrayList<>());

		// API call to create root group
		String url = loadedConfigItems.getUsersAPI() + "/group/" + getAdminUser().getId() + "/" + parentGroupId.toString();
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<CompanyGroup>(group, createHttpHeaders(UserRole.ADMIN)), CompanyGroup.class);

		// Added group should be returned in the response
		assertNotNull(response);
		// Status 200 should be returned
		assertEquals(200, response.getStatusCodeValue());
		// Returned group should be sub group
		assertFalse(response.getBody().isRootGroup());
	}

	@Test
	public void moveRootGroupWithAdminUserPositive() {
		List<ContextUserAuthentication> contextUserAuth = contextUserAuthRepo.getByUserId(getAdminUser().getId());
		// Create source root group with the contextId of the admin user
		CompanyGroup sourceGroup = new CompanyGroup("Source Group", new ArrayList<>());
		sourceGroup.setRootGroup(true);
		sourceGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId sourceGroupId = groupRepository.saveAndReturnId(sourceGroup);

		// Create destination root group with the contextId of the admin user
		CompanyGroup destGroup = new CompanyGroup("Destination Group", new ArrayList<>());
		destGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId destGroupId = groupRepository.saveAndReturnId(destGroup);

		// API call to move source group to destGroup
		String url = loadedConfigItems.getUsersAPI() + "/groups/move/" + sourceGroupId.toString() + "/" + destGroupId.toString() + "/"
				+ getAdminUser().getId();
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<String>(createHttpHeaders(UserRole.ADMIN)), CompanyGroup.class);

		// Moved group should be returned in the response
		assertNotNull(response);

		// Status 200 should be returned
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void moveRootGroupWithAdminIfDestGroupIsNull() {
		List<ContextUserAuthentication> contextUserAuth = contextUserAuthRepo.getByUserId(getAdminUser().getId());

		// Create source root group with the contextId of the admin user
		CompanyGroup sourceGroup = new CompanyGroup("Source Group", new ArrayList<>());
		sourceGroup.setRootGroup(true);
		sourceGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId sourceGroupId = groupRepository.saveAndReturnId(sourceGroup);

		// This group does not exist
		String destGroupId = "1111";

		// API call to move source group to destGroup
		String url = loadedConfigItems.getUsersAPI() + "/groups/move/" + sourceGroupId.toString() + "/" + destGroupId + "/"
				+ getAdminUser().getId();
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<String>(createHttpHeaders(UserRole.ADMIN)), CompanyGroup.class);

		// No need to move group because this is already root group and should be moved again to the root position
		assertNotNull(response);

		// Status 200 should be returned
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void moveSubGroupWithAdminIfDestGroupIsNull() throws ItemNotFoundRepositoryException {
		List<ContextUserAuthentication> contextUserAuth = contextUserAuthRepo.getByUserId(getAdminUser().getId());

		// Create parent root group with the contextId of the admin user
		CompanyGroup parentGroup = new CompanyGroup("Parent Group", new ArrayList<>());
		parentGroup.setRootGroup(true);
		parentGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId parentGroupId = groupRepository.saveAndReturnId(parentGroup);

		// Create child of the parent group
		CompanyGroup childGroup = new CompanyGroup("Child Group", new ArrayList<>());
		childGroup.setParentId(parentGroupId.toString());
		childGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId childGroupId = groupRepository.saveAndReturnId(childGroup);

		// Update parent group with the childGroup id
		parentGroup = groupRepository.find(parentGroupId.toString());
		parentGroup.addChildrenId(childGroupId.toString());
		groupRepository.update(parentGroup);

		// This group id does not exist
		String destGroupId = "0000";

		// API call to move source group to destGroup
		String url = loadedConfigItems.getUsersAPI() + "/groups/move/" + childGroupId.toString() + "/" + destGroupId + "/"
				+ getAdminUser().getId();
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<String>(createHttpHeaders(UserRole.ADMIN)), CompanyGroup.class);

		assertNotNull(response);

		// Status 200 should be returned
		assertEquals(200, response.getStatusCodeValue());

		// check in database if moved group is now root group
		childGroup = groupRepository.find(childGroupId.toString());

		assertNotNull(childGroup);

		assertTrue(childGroup.isRootGroup());

		assertNull(childGroup.getParentId());
	}

	@Test
	public void moveRootGroupWithSuperUserPositive() {
		List<ContextUserAuthentication> contextUserAuth = contextUserAuthRepo.getByUserId(getSuperUser().getId());

		// Create source root group with the contextId of the superuser and add user id to the group list of the superusers
		CompanyGroup sourceGroup = new CompanyGroup("Source Group", new ArrayList<>());
		sourceGroup.setRootGroup(true);
		sourceGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId sourceGroupId = groupRepository.saveAndReturnId(sourceGroup);

		// Map superuser with source group
		GroupAccessRight groupAccessRight = new GroupAccessRight(getSuperUser().getId(), sourceGroupId.toString(),
				contextUserAuth.get(0).getContextId());
		groupAccessRightRepository.save(groupAccessRight);

		// Create destination root group with the contextId of the superuser
		CompanyGroup destGroup = new CompanyGroup("Destination Group", new ArrayList<>());
		destGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId destGroupId = groupRepository.saveAndReturnId(destGroup);

		// Map superuser with dest group
		groupAccessRight = new GroupAccessRight(getSuperUser().getId(), destGroupId.toString(), contextUserAuth.get(0).getContextId());
		groupAccessRightRepository.save(groupAccessRight);

		// API call to move source group to destGroup
		String url = loadedConfigItems.getUsersAPI() + "/groups/move/" + sourceGroupId.toString() + "/" + destGroupId.toString() + "/"
				+ getSuperUser().getId();
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<String>(createHttpHeaders(UserRole.SUPERUSER)), CompanyGroup.class);

		// Moved group should be returned in the response
		assertNotNull(response);

		// Status 200 should be returned
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	public void moveRootGroupWithSuperUserIfDestGroupIsNull() {
		List<ContextUserAuthentication> contextUserAuth = contextUserAuthRepo.getByUserId(getSuperUser().getId());
		// Create source root group with the contextId of the superuser and add user id to the group list of the superusers
		CompanyGroup sourceGroup = new CompanyGroup("Source Group", new ArrayList<>());
		sourceGroup.setRootGroup(true);
		sourceGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId sourceGroupId = groupRepository.saveAndReturnId(sourceGroup);

		// Map superuser with source group
		GroupAccessRight groupAccessRight = new GroupAccessRight(getSuperUser().getId(), sourceGroupId.toString(),
				contextUserAuth.get(0).getContextId());
		groupAccessRightRepository.save(groupAccessRight);

		// This group does not exist
		String destGroupId = "1111";

		// API call to move source group to destGroup
		String url = loadedConfigItems.getUsersAPI() + "/groups/move/" + sourceGroupId.toString() + "/" + destGroupId + "/"
				+ getSuperUser().getId();
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<String>(createHttpHeaders(UserRole.SUPERUSER)), CompanyGroup.class);

		// No need to move group because this is already root group and should be moved again to the root position
		assertNotNull(response);

		// Status 200 should be returned
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void moveRootGroupWithSuperUserIfGroupNotAssigned() {
		List<ContextUserAuthentication> contextUserAuth = contextUserAuthRepo.getByUserId(getSuperUser().getId());

		// Create source root group with the contextId of the admin user but without adding superuser to the list of the superuser in the
		// group
		CompanyGroup sourceGroup = new CompanyGroup("Source Group", new ArrayList<>());
		sourceGroup.setRootGroup(true);
		sourceGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId sourceGroupId = groupRepository.saveAndReturnId(sourceGroup);

		// This group does not exist
		String destGroupId = "1111";

		// API call to move source group to destGroup
		String url = loadedConfigItems.getUsersAPI() + "/groups/move/" + sourceGroupId.toString() + "/" + destGroupId + "/"
				+ getSuperUser().getId();
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<String>(createHttpHeaders(UserRole.SUPERUSER)), CompanyGroup.class);

		// No need to move group because this is already root group and should be moved again to the root position
		assertNotNull(response);

		// Status 200 should be returned
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void moveRootGroupWithSuperUserIfBothGroupsNotAssigned() {
		List<ContextUserAuthentication> contextUserAuth = contextUserAuthRepo.getByUserId(getSuperUser().getId());

		// Create source root group with the contextId of the superuser and add user id to the group list of the superusers
		CompanyGroup sourceGroup = new CompanyGroup("Source Group", new ArrayList<>());
		sourceGroup.setRootGroup(true);
		sourceGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId sourceGroupId = groupRepository.saveAndReturnId(sourceGroup);

		// Create destination root group with the contextId of the superuser
		CompanyGroup destGroup = new CompanyGroup("Destination Group", new ArrayList<>());
		destGroup.setContextId(contextUserAuth.get(0).getContextId());
		ObjectId destGroupId = groupRepository.saveAndReturnId(destGroup);

		// API call to move source group to destGroup
		String url = loadedConfigItems.getUsersAPI() + "/groups/move/" + sourceGroupId.toString() + "/" + destGroupId.toString() + "/"
				+ getSuperUser().getId();
		ResponseEntity<CompanyGroup> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<String>(createHttpHeaders(UserRole.SUPERUSER)), CompanyGroup.class);

		assertNotNull(response);

		// Status 404 must be returned because neither toGroup nor fromGroup have added the superuserId to the list of the superusers and such
		// superuser must not be able to move the group
		assertEquals(404, response.getStatusCodeValue());
	}

}
