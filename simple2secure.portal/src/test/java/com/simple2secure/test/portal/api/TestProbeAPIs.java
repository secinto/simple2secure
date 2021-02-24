package com.simple2secure.test.portal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestProbeAPIs extends TestAPIBase {

	private static Logger log = LoggerFactory.getLogger(TestProbeAPIs.class);

	@Autowired
	protected LoadedConfigItems loadedConfigItems;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private LicenseRepository licenseRepository;

	@Autowired
	private GroupRepository groupRepository;

	HttpHeaders headers = new HttpHeaders();

	@Test
	public void testDeleteProbeUserAuthenticated() {
		// Create license object which should be deleted
		CompanyLicensePrivate license = new CompanyLicensePrivate(new ObjectId("123"), new ObjectId("456"), "01/01/2020");
		license.setDeviceId(new ObjectId("789"));
		licenseRepository.save(license);

		// API call to delete the created license
		String url = loadedConfigItems.getBaseURL() + StaticConfigItems.DEVICE_API + "/deleteProbe/" + license.getDeviceId();
		ResponseEntity<CompanyLicensePrivate> response = restTemplate.exchange(url, HttpMethod.DELETE,
				new HttpEntity<String>(createHttpHeaders(UserRole.ADMIN)), CompanyLicensePrivate.class);

		// Deleted license should be returned in the response
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		log.debug("Response {0}", response.toString());

		// License should not exist in the database. NULL must be returned
		license = licenseRepository.findByDeviceId(new ObjectId("789"));
		assertNull(license);
	}

	@Test
	public void testDeleteProbeUserNotAuthenticated() {
		// Create license object which should be deleted
		CompanyLicensePrivate license = new CompanyLicensePrivate(new ObjectId("123"), new ObjectId("456"), "01/01/2020");
		license.setDeviceId(new ObjectId("789"));
		licenseRepository.save(license);

		// API call to delete the created license without auth token (not provided in the headers)
		String url = loadedConfigItems.getBaseURL() + StaticConfigItems.DEVICE_API + "/deleteProbe/" + license.getDeviceId();
		ResponseEntity<CompanyLicensePrivate> response = restTemplate.exchange(url, HttpMethod.DELETE,
				new HttpEntity<String>(createHttpHeadersWithoutAccessToken()), CompanyLicensePrivate.class);

		assertNotNull(response);
		// Unauthorized status code must be returned
		assertEquals(401, response.getStatusCodeValue());
	}

	@Test
	public void testDeleteProbeNotExists() {
		// This probeId does not exist in the database
		String probeId = "102";

		// API call to delete the created license without auth token (not provided in the headers)
		String url = loadedConfigItems.getBaseURL() + StaticConfigItems.DEVICE_API + "/deleteProbe/" + probeId;
		ResponseEntity<CompanyLicensePrivate> response = restTemplate.exchange(url, HttpMethod.DELETE,
				new HttpEntity<String>(createHttpHeaders(UserRole.ADMIN)), CompanyLicensePrivate.class);

		assertNotNull(response);
		// Not found status code must be returned
		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	public void testChangeProbeGroupUserAuthenticated() {
		// Create companyGroup object for the current group
		CompanyGroup group = new CompanyGroup("Old Group", new ArrayList<ObjectId>());
		ObjectId oldGroupId = groupRepository.saveAndReturnId(group);

		// Create companyGroup object for the group which will be the new group
		group.setName("New Group");
		ObjectId newGroupId = groupRepository.saveAndReturnId(group);
		group = groupRepository.find(newGroupId);

		// Create license object
		CompanyLicensePrivate license = new CompanyLicensePrivate(new ObjectId("123"), new ObjectId("456"), "01/01/2020");
		license.setDeviceId(new ObjectId("789"));
		license.setGroupId(oldGroupId);
		licenseRepository.save(license);

		// API call to change the probe group
		String url = loadedConfigItems.getBaseURL() + StaticConfigItems.DEVICE_API + "/changeGroup/" + license.getDeviceId();
		ResponseEntity<CompanyLicensePrivate> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<>(group, createHttpHeaders(UserRole.ADMIN)), CompanyLicensePrivate.class);

		assertNotNull(response);
		// Status code must be 200
		assertEquals(200, response.getStatusCodeValue());

		// There should be no licenses with the old groupId in the database
		List<CompanyLicensePrivate> licenses = licenseRepository.findAllByGroupId(oldGroupId);
		assertTrue(licenses.isEmpty());

		// There should be one license with the new groupId in the database
		licenses = licenseRepository.findAllByGroupId(newGroupId);
		assertFalse(licenses.isEmpty());
	}

	@Test
	public void testChangeProbeGroupUserNotAuthenticated() {
		// Create companyGroup object for the current group
		CompanyGroup group = new CompanyGroup("Old Group", new ArrayList<ObjectId>());
		ObjectId oldGroupId = groupRepository.saveAndReturnId(group);

		// Create companyGroup object for the group which will be the new group
		group.setName("New Group");
		ObjectId newGroupId = groupRepository.saveAndReturnId(group);
		group = groupRepository.find(newGroupId);

		// Create license object
		CompanyLicensePrivate license = new CompanyLicensePrivate(new ObjectId("123"), new ObjectId("456"), "01/01/2020");
		license.setDeviceId(new ObjectId("789"));
		license.setGroupId(oldGroupId);
		licenseRepository.save(license);

		// API call to change the probe group
		String url = loadedConfigItems.getBaseURL() + StaticConfigItems.DEVICE_API + "/changeGroup/" + license.getDeviceId();
		ResponseEntity<CompanyLicensePrivate> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<>(group, createHttpHeadersWithoutAccessToken()), CompanyLicensePrivate.class);

		assertNotNull(response);
		// Unauthorized status code must be returned
		assertEquals(401, response.getStatusCodeValue());
	}

	@Test
	public void testChangeProbeGroupNotExists() {

		// Create companyGroup object without saving it to the database
		CompanyGroup group = new CompanyGroup("Old Group", new ArrayList<ObjectId>());

		// Create license object
		CompanyLicensePrivate license = new CompanyLicensePrivate(new ObjectId("123"), new ObjectId("456"), "01/01/2020");
		license.setDeviceId(new ObjectId("789"));
		license.setGroupId(new ObjectId(""));
		licenseRepository.save(license);

		// API call to change the probe group
		String url = loadedConfigItems.getBaseURL() + StaticConfigItems.DEVICE_API + "/changeGroup/" + license.getDeviceId();
		ResponseEntity<CompanyLicensePrivate> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<>(group, createHttpHeaders(UserRole.ADMIN)), CompanyLicensePrivate.class);

		assertNotNull(response);

		// Not found status code must be returned
		assertEquals(404, response.getStatusCodeValue());

	}

	@Test
	public void testChangeProbeGroupProbeNotExists() {
		// This probeId does not exist in the database
		String probeId = "102";

		// Create companyGroup object and save to database
		CompanyGroup group = new CompanyGroup("Test Group", new ArrayList<ObjectId>());
		groupRepository.save(group);

		// API call to change the probe group
		String url = loadedConfigItems.getBaseURL() + StaticConfigItems.DEVICE_API + "/changeGroup/" + probeId;
		ResponseEntity<CompanyLicensePrivate> response = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<>(group, createHttpHeaders(UserRole.ADMIN)), CompanyLicensePrivate.class);

		assertNotNull(response);

		// Not found status code must be returned
		assertEquals(404, response.getStatusCodeValue());
	}
}
