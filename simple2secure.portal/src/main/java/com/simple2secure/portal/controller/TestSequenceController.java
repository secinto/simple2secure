package com.simple2secure.portal.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.simple2secure.api.dto.TestSequenceRunDTO;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.api.model.TestSequence;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.commons.config.StaticConfigItems;
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
import com.simple2secure.portal.utils.PortalUtils;
import com.simple2secure.portal.utils.TestUtils;

import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputDevice;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputPage;
import simple2secure.validator.model.ValidInputSequence;
import simple2secure.validator.model.ValidInputSize;
import simple2secure.validator.model.ValidInputUser;

@RestController
@RequestMapping(StaticConfigItems.SEQUENCE_API)
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

	@Autowired
	PortalUtils portalUtils;

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getAllSequences(@PathVariable ValidInputDevice deviceId, @PathVariable ValidInputPage page,
			@PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale.getValue()) && !Strings.isNullOrEmpty(deviceId.getValue())) {
			List<TestSequence> allSeqFromDb = testSequenceRepository.getByDeviceId(deviceId.getValue(), page.getValue(), size.getValue());
			Map<String, Object> sequencesMap = new HashMap<>();
			if (allSeqFromDb != null) {
				sequencesMap.put("sequences", allSeqFromDb);
				sequencesMap.put("totalSize", testSequenceRepository.getCountOfSequencesWithDeviceid(deviceId.getValue()));
				return new ResponseEntity<>(sequencesMap, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_loading_sequences", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestSequence> addNewSequence(@RequestBody TestSequence sequence, @ServerProvidedValue ValidInputLocale locale)
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

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_sequence", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestSequence> deleteSequence(@PathVariable String sequenceId, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(sequenceId)) {
			TestSequence sequence = testSequenceRepository.find(sequenceId);
			if (sequence != null) {
				testSequenceRepository.delete(sequence);
				return new ResponseEntity<>(sequence, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_deleting_sequence", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@ValidRequestMapping(value = "/scheduledSequence", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<List<SequenceRun>> getScheduledSequence(@PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		CompanyLicensePrivate podLicense = licenseRepository.findByDeviceId(deviceId.getValue());

		if (podLicense != null) {
			podLicense.setLastOnlineTimestamp(System.currentTimeMillis());
			log.debug("Updating last online time for device {}", deviceId.getValue());
			licenseRepository.update(podLicense);
			log.debug("Updated last online time for device {}", deviceId);
			ResponseEntity<List<SequenceRun>> respEntObj = testUtils.getSequenceByDeviceId(deviceId.getValue(), locale.getValue());
			List<SequenceRun> allSeqRuns = respEntObj.getBody();
			List<SequenceRun> filteredSeqRuns = allSeqRuns.stream().filter(sR -> sR.getSequenceStatus().equals(TestStatus.PLANNED))
					.collect(Collectors.toList());
			return new ResponseEntity<>(filteredSeqRuns, HttpStatus.OK);
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_sequences", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@ValidRequestMapping(value = "/scheduleSequence", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SequenceRun> addSequenceToSchedule(@RequestBody TestSequence sequence, @ServerProvidedValue ValidInputContext contextId,
			@ServerProvidedValue ValidInputUser userId, @ServerProvidedValue ValidInputLocale locale) {
		if (sequence != null && !Strings.isNullOrEmpty(contextId.getValue()) && !Strings.isNullOrEmpty(userId.getValue())) {

			User user = userRepository.find(userId.getValue());

			if (user != null) {

				TestSequence currSequence = testSequenceRepository.find(sequence.getId());

				if (currSequence != null) {
					SequenceRun seqRun = new SequenceRun(sequence.getId(), sequence.getName(), sequence.getPodId(), contextId.getValue(),
							TestRunType.MANUAL_PORTAL, currSequence.getSequenceContent(), TestStatus.PLANNED, System.currentTimeMillis());

					sequenceRunrepository.save(seqRun);

					notificationUtils.addNewNotificationPortal(sequence.getName() + " has been scheduled using the portal by " + user.getEmail(),
							contextId.getValue());

					return new ResponseEntity<>(seqRun, HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_scheduling_sequence", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/scheduledSequence")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getScheduledSequenceWithPag(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale)
			throws ItemNotFoundRepositoryException {

		if (!Strings.isNullOrEmpty(contextId.getValue())) {
			List<TestSequenceRunDTO> scheduledSequenceRuns = testUtils.generateSequenceRunDTOBySequenceRun(
					sequenceRunrepository.getByContextIdWithPagination(contextId.getValue(), page.getValue(), size.getValue()));
			Map<String, Object> scheduledSequencesMap = new HashMap<>();
			scheduledSequencesMap.put("sequences", scheduledSequenceRuns);
			scheduledSequencesMap.put("totalSize", sequenceRunrepository.countByContextId(contextId.getValue()));
			return new ResponseEntity<>(scheduledSequencesMap, HttpStatus.OK);
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_sequences", locale.getValue())),
				HttpStatus.NOT_FOUND);

	}

	@ValidRequestMapping(value = "/update/status", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<SequenceRun> updateSequenceRunStatus(@RequestBody String sequenceRunInfo,
			@PathVariable ValidInputSequence sequenceId, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (sequenceRunInfo != null && !Strings.isNullOrEmpty(sequenceId.getValue())) {
			JsonNode obj = JSONUtils.fromString(sequenceRunInfo);
			String sequenceStatus = obj.findValue("status").asText();
			SequenceRun currSequenceRun = sequenceRunrepository.find(sequenceId.getValue());
			if (currSequenceRun != null) {
				currSequenceRun.setSequenceStatus(TestStatus.valueOf(sequenceStatus));
				sequenceRunrepository.update(currSequenceRun);
				return new ResponseEntity<>(currSequenceRun, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_updating_sequence_status", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/save/sequencerunresult", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('DEVICE')")
	public ResponseEntity<TestSequenceResult> saveSequenceRunResult(@RequestBody TestSequenceResult sequenceRunResult,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sequenceRunResult != null) {
			testSequenceResultRepository.save(sequenceRunResult);
			return new ResponseEntity<>(sequenceRunResult, HttpStatus.OK);
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_saving_sequence_results", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/sequenceresults")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestSequenceResult>> getSequenceResults(@PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (deviceId != null) {
			List<TestSequenceResult> result = testSequenceResultRepository.getByDeviceId(deviceId.getValue());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_loading_sequence_results", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/sequencerunresults")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestSequenceResult>> getSequenceRunResults(@PathVariable ValidInputSequence seqId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (seqId != null) {
			List<TestSequenceResult> result = testSequenceResultRepository.getBySequenceId(seqId.getValue());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}

		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_loading_sequence_results", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

	@ValidRequestMapping(value = "/result")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getSequenceRunResultsByContextId(@ServerProvidedValue ValidInputContext contextId,
			@PathVariable ValidInputPage page, @PathVariable ValidInputSize size, @ServerProvidedValue ValidInputLocale locale) {
		if (!Strings.isNullOrEmpty(contextId.getValue()) && !Strings.isNullOrEmpty(locale.getValue())) {
			List<SequenceRun> sequenceRuns = sequenceRunrepository.getByContextId(contextId.getValue());
			List<String> sequenceIds = portalUtils.extractIdsFromObjects(sequenceRuns);

			if (sequenceIds != null) {
				List<TestSequenceResult> sequenceResults = testSequenceResultRepository.getBySequenceRunIds(sequenceIds, page.getValue(),
						size.getValue());
				Map<String, Object> sequenceResultMap = new HashMap<>();
				if (!sequenceResults.isEmpty()) {
					sequenceResultMap.put("results", sequenceResults);
					sequenceResultMap.put("totalSize", testSequenceResultRepository.getCountOfSequencesWithSequenceRunIds(sequenceIds));
					return new ResponseEntity<>(sequenceResultMap, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(
							new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_loading_sequence_results", locale.getValue())),
							HttpStatus.NOT_FOUND);
				}
			}
		}
		return new ResponseEntity<>(
				new CustomErrorType(messageByLocaleService.getMessage("problem_occured_while_loading_sequence_results", locale.getValue())),
				HttpStatus.NOT_FOUND);
	}

}
