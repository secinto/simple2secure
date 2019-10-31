package com.simple2secure.portal.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.api.model.TestSequence;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.crypto.CryptoUtils;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SequenceRunRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestSequenceRepository;
import com.simple2secure.portal.repository.TestSequenceResultRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.service.MessageByLocaleService;
import com.simple2secure.portal.utils.NotificationUtils;
import com.simple2secure.portal.utils.TestUtils;

@RestController
@RequestMapping("/api/sequence")
public class TestSequenceController {

	private static Logger log = LoggerFactory.getLogger(TestSequenceController.class);

	@Autowired
	LoadedConfigItems loadedConfigItems;

	@Autowired
	MessageByLocaleService messageByLocaleService;

	@Autowired
	TestSequenceRepository testSequenceRepository;

	@Autowired
	SequenceRunRepository sequenceRunrepository;

	@Autowired
	TestSequenceResultRepository testSequenceResultRepository;

	@Autowired
	TestRepository testRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	NotificationUtils notificationUtils;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	TestUtils testUtils;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/{deviceId}",
			method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestSequence>> getAllSequences(@PathVariable("deviceId") String deviceId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale) && !Strings.isNullOrEmpty(deviceId)) {
			List<TestSequence> allSeqFromDb = testSequenceRepository.getByPodId(deviceId);

			if (allSeqFromDb != null && allSeqFromDb.size() != 0) {
				return new ResponseEntity<>(allSeqFromDb, HttpStatus.OK);
			} else {
				return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_loading_sequences", locale)),
						HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_loading_sequences", locale)),
					HttpStatus.NOT_FOUND);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/add",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestSequence> addNewSequence(@RequestBody TestSequence sequence, @RequestHeader("Accept-Language") String locale)
			throws com.simple2secure.portal.exceptions.ItemNotFoundRepositoryException, NoSuchAlgorithmException,
			ItemNotFoundRepositoryException {
		if (sequence != null) {
			sequence.setLastChangedTimeStamp(System.currentTimeMillis());
			// TODO: Calculate correct hash value
			List<Test> testList = new ArrayList<>();
			for (String testId : sequence.getSequenceContent()) {
				Test dbTest = testRepository.getTestByName(testId);
				testList.add(dbTest);
			}
			sequence.setSequenceHash(CryptoUtils.generateSecureHashHexString(sequence.getSequenceContent().toString()));
			if (!Strings.isNullOrEmpty(sequence.getId())) {
				testSequenceRepository.update(sequence);
				log.debug("Test sequence: {} has been updated", sequence.getName());
			} else {
				testSequenceRepository.save(sequence);
				log.debug("New test sequence: {} has been saved", sequence.getName());
			}
			return new ResponseEntity<>(sequence, HttpStatus.OK);
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_sequence", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/delete/{sequenceId}",
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestSequence> deleteSequence(@PathVariable("sequenceId") String sequenceId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(sequenceId)) {
			TestSequence sequence = testSequenceRepository.find(sequenceId);
			if (sequence != null) {
				testSequenceRepository.delete(sequence);
				return new ResponseEntity<>(sequence, HttpStatus.OK);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_test", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/scheduledSequence/{deviceId}",
			method = RequestMethod.GET,
			consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<List<SequenceRun>> getScheduledSequence(@PathVariable("deviceId") String deviceId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		CompanyLicensePrivate podLicense = licenseRepository.findByDeviceId(deviceId);

		if (podLicense != null) {
			podLicense.setLastOnlineTimestamp(System.currentTimeMillis());
			log.debug("Updating last online time for device {}", deviceId);
			licenseRepository.update(podLicense);
			log.debug("Updated last online time for device {}", deviceId);
			ResponseEntity<List<SequenceRun>> respEntObj = testUtils.getSequenceByDeviceId(deviceId, locale);
			List<SequenceRun> allSeqRuns = respEntObj.getBody();
			List<SequenceRun> filteredSeqRuns = allSeqRuns.stream().filter(sR -> sR.getSequenceStatus().equals(TestStatus.PLANNED))
					.collect(Collectors.toList());
			return new ResponseEntity<>(filteredSeqRuns, HttpStatus.OK);
		}

		return new ResponseEntity(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_tests", locale)),
				HttpStatus.NOT_FOUND);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/scheduleSequence/{contextId}/{userId}",
			method = RequestMethod.POST,
			consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SequenceRun> addSequenceToSchedule(@RequestBody TestSequence sequence, @PathVariable("contextId") String contextId,
			@PathVariable("userId") String userId, @RequestHeader("Accept-Language") String locale) {
		if (sequence != null && !Strings.isNullOrEmpty(contextId) && !Strings.isNullOrEmpty(userId)) {

			User user = userRepository.find(userId);

			if (user != null) {

				TestSequence currSequence = testSequenceRepository.find(sequence.getId());

				if (currSequence != null) {
					SequenceRun seqRun = new SequenceRun(sequence.getId(), sequence.getName(), sequence.getPodId(), contextId,
							TestRunType.MANUAL_PORTAL, currSequence.getSequenceContent(), TestStatus.PLANNED, System.currentTimeMillis());

					sequenceRunrepository.save(seqRun);

					notificationUtils.addNewNotificationPortal(sequence.getName() + " has been scheduled using the portal by " + user.getEmail(),
							contextId);

					return new ResponseEntity<>(seqRun, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/update/status/{sequenceRunId}",
			method = RequestMethod.POST,
			consumes = "application/json")
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<SequenceRun> updateSequenceRunStatus(@RequestBody String sequenceRunInfo,
			@PathVariable("sequenceRunId") String sequenceRunId, @RequestHeader("Accept-Language") String locale)
			throws ItemNotFoundRepositoryException {
		if (sequenceRunInfo != null && !Strings.isNullOrEmpty(sequenceRunId)) {
			JsonNode obj = JSONUtils.fromString(sequenceRunInfo);
			String sequenceStatus = obj.findValue("status").asText();
			if (sequenceRunId != null) {

				SequenceRun currSequenceRun = sequenceRunrepository.find(sequenceRunId);
				currSequenceRun.setSequenceStatus(TestStatus.valueOf(sequenceStatus));
				sequenceRunrepository.update(currSequenceRun);
			}
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(
			value = "/save/sequencerunresult",
			method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<SequenceRun> saveSequenceRunResult(@RequestBody TestSequenceResult sequenceRunResult,
			@RequestHeader("Accept-Language") String locale) {
		if (sequenceRunResult != null) {
			testSequenceResultRepository.save(sequenceRunResult);
		}

		return new ResponseEntity(new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_test", locale)),
				HttpStatus.NOT_FOUND);
	}
}
