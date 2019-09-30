package com.simple2secure.portal.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

import com.google.common.base.Strings;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestSequence;
import com.simple2secure.commons.config.LoadedConfigItems;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.model.CustomErrorType;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.TestRepository;
import com.simple2secure.portal.repository.TestSequenceRepository;
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
	@RequestMapping(value = "/{podId}", method = RequestMethod.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestSequence>> getAllSequences(@PathVariable("podId") String podId,
			@RequestHeader("Accept-Language") String locale) throws ItemNotFoundRepositoryException {
		if (!Strings.isNullOrEmpty(locale) && !Strings.isNullOrEmpty(podId)) {
			List<TestSequence> allSeqFromDb = testSequenceRepository.getByPodId(podId);

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
	@RequestMapping(value = "/add", method = RequestMethod.POST)
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
			sequence.setSequenceHash(testUtils.getHexValueHash(testUtils.calculateMd5Hash(sequence.getSequenceContent().toString())));
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
	@RequestMapping(value = "/delete/{sequenceId}", method = RequestMethod.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
	public ResponseEntity<TestSequence> deleteTest(@PathVariable("sequenceId") String sequenceId,
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

}
