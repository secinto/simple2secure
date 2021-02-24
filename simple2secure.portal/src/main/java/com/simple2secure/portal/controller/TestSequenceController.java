package com.simple2secure.portal.controller;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.simple2secure.api.dto.TestSUTDataInput;
import com.simple2secure.api.dto.TestSequenceDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceType;
import com.simple2secure.api.model.FactToCheckByRuleEngine;
import com.simple2secure.api.model.ReportType;
import com.simple2secure.api.model.RuleFactType;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.api.model.TestSequence;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.api.model.TestSequenceStepResult;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.exceptions.ApiRequestException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputSequence;
import com.simple2secure.portal.validation.model.ValidInputUser;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@RestController
@RequestMapping(StaticConfigItems.SEQUENCE_API)
@Slf4j
public class TestSequenceController extends BaseUtilsProvider {

	@ValidRequestMapping
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getAllSequences(@PathVariable ValidInputDevice deviceId, 
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@RequestParam(
					required = false) String filter,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (deviceId.getValue() != null) {
			List<TestSequenceDTO> sequences = testSequenceUtils.getAllSequencesByDeviceId(deviceId.getValue(), page, size, filter);
			Map<String, Object> sequencesMap = new HashMap<>();
			if (sequences != null) {
				sequencesMap.put("sequences", sequences);
				sequencesMap.put("totalSize", testSequenceRepository.getCountOfSequencesWithDeviceid(deviceId.getValue()));
				return new ResponseEntity<>(sequencesMap, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_loading_sequences", locale.getValue()));
	}

	@ValidRequestMapping(
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<TestSequence> addNewSequence(@RequestBody TestSequence sequence, @ServerProvidedValue ValidInputUser user,
			@ServerProvidedValue ValidInputLocale locale) throws com.simple2secure.portal.exceptions.ItemNotFoundRepositoryException,
			NoSuchAlgorithmException, ItemNotFoundRepositoryException {
		if (sequence != null) {
			sequence.setLastChangedTimeStamp(System.currentTimeMillis());
			if (sequence.getId() != null) {
				testSequenceRepository.update(sequence);
				log.debug("Test sequence: {} has been updated", sequence.getName());
			} else {
				testSequenceRepository.save(sequence);
				log.debug("New test sequence: {} has been saved", sequence.getName());
			}
			return new ResponseEntity<>(sequence, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_sequence", locale.getValue()));
	}

	/**
	 * Deletes the specified test sequence from the portal.
	 *
	 * @param sequenceId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			method = ValidRequestMethodType.DELETE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
	public ResponseEntity<TestSequence> deleteSequence(@PathVariable ValidInputSequence sequenceId, @ServerProvidedValue ValidInputUser user,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		/*
		 * TODO: Verify that the user is allowed to delete this sequence, currently no information about context and user is checked in
		 * combination with the sequence selected.
		 */
		if (sequenceId.getValue() != null) {
			TestSequence sequence = testSequenceRepository.find(sequenceId.getValue());
			if (testSequenceUtils.hasUserRightsForTestSequence(sequence, user.getValue())) {
				if (sequence != null) {
					testSequenceRepository.delete(sequence);
					log.debug("Test sequence with id {} and name {} deleted from user {}", sequence.getId());
					return new ResponseEntity<>(sequence, HttpStatus.OK);
				}
			} else {
				throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_user_has_no_rights", locale.getValue()));
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_deleting_sequence", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/clone",
			method = ValidRequestMethodType.GET)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
	public ResponseEntity<TestSequence> cloneTestSequence(@PathVariable ValidInputSequence sequenceId,
			@ServerProvidedValue ValidInputUser user, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (sequenceId.getValue() != null) {
			TestSequence sequence = testSequenceRepository.find(sequenceId.getValue());
			if (sequence != null) {
				TestSequence clonedSequence = testSequenceUtils.cloneSequence(sequence);

				if (clonedSequence != null) {
					return new ResponseEntity<>(clonedSequence, HttpStatus.OK);
				}
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_cloning_testsequence", locale.getValue()));
	}

	/**
	 * Obtains the scheduled sequence for the specified device. This is only available for devices itself.
	 *
	 * @param deviceId
	 * @param locale
	 * @return
	 * @throws ItemNotFoundRepositoryException
	 */
	@ValidRequestMapping(
			value = "/scheduledSequence",
			method = ValidRequestMethodType.GET,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<List<SequenceRun>> getScheduledSequence(@PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		DeviceInfo deviceInfo = deviceInfoRepository.findByDeviceId(deviceId.getValue());

		if (deviceInfo != null) {
			deviceInfo.setLastOnlineTimestamp(System.currentTimeMillis());
			log.debug("Updating last online time for device id {} and name {}", deviceInfo.getId(), deviceInfo.getName());
			deviceInfoRepository.update(deviceInfo);
			log.debug("Updated last online time for device id {}", deviceInfo.getId());
			ResponseEntity<List<SequenceRun>> respEntObj = testUtils.getSequenceByDeviceId(deviceId.getValue(), locale);
			List<SequenceRun> allSeqRuns = respEntObj.getBody();
			List<SequenceRun> filteredSeqRuns = allSeqRuns.stream().filter(sR -> sR.getSequenceStatus().equals(TestStatus.PLANNED))
					.collect(Collectors.toList());
			return new ResponseEntity<>(filteredSeqRuns, HttpStatus.OK);
		}
		throw new ApiRequestException(
				messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_sequences", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/scheduleSequence",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<SequenceRun> addSequenceToSchedule(@RequestBody TestSequence sequence,
			@ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputUser userId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sequence != null && contextId.getValue() != null && !Strings.isNullOrEmpty(userId.getValue())) {

			TestSequence currSequence = testSequenceRepository.find(sequence.getId());

			if (currSequence != null) {
				LinkedHashMap<String, String> sequenceContent = new LinkedHashMap<>();
				// Constructing testSequenceContent on the server
				if (currSequence.getTests() != null && !currSequence.getTests().isEmpty()) {

					for (TestSUTDataInput testSutData : currSequence.getTests()) {
						Test test = testRepository.find(testSutData.getTest().getTestId());
						if (test != null) {
							String testContent = testUtils.mergeSUTAndDataInput(testSutData, test);
							sequenceContent.put(testSutData.getTest().getTestId().toHexString(), testContent);
						}
					}

					DeviceInfo deviceInfo = deviceInfoRepository.findByDeviceId(currSequence.getPodId());

					if (deviceInfo != null) {
						SequenceRun seqRun = new SequenceRun(sequence.getId(), sequence.getName(), currSequence.getPodId(), contextId.getValue(),
								TestRunType.MANUAL_PORTAL, sequenceContent, TestStatus.PLANNED, System.currentTimeMillis(), deviceInfo.getName());

						sequenceRunrepository.save(seqRun);

						log.debug("A new sequence run {} has been created and added to the schedule on the portal from user {}", currSequence.getId(),
								userId.getValue());

						return new ResponseEntity<>(seqRun, HttpStatus.OK);
					}
				}
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_scheduling_sequence", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/scheduledSequence")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")

	public ResponseEntity<Map<String, Object>> getScheduledSequenceWithPag(@ServerProvidedValue ValidInputContext contextId, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {

		if (contextId.getValue() != null) {
			Map<String, Object> scheduledSequencesMap = sequenceRunrepository.getByContextIdWithPagination(contextId.getValue(), page, size,
					filter);
			return new ResponseEntity<>(scheduledSequencesMap, HttpStatus.OK);
		}
		throw new ApiRequestException(
				messageByLocaleService.getMessage("problem_occured_while_retrieving_scheduled_sequences", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/update/status",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<SequenceRun> updateSequenceRunStatus(@RequestBody String sequenceRunInfo,
			@PathVariable ValidInputSequence sequenceId, @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (sequenceRunInfo != null && sequenceId.getValue() != null) {
			JsonNode obj = JSONUtils.fromString(sequenceRunInfo);
			String sequenceStatus = obj.findValue("status").asText();
			SequenceRun currSequenceRun = sequenceRunrepository.find(sequenceId.getValue());
			if (currSequenceRun != null) {
				currSequenceRun.setSequenceStatus(TestStatus.valueOf(sequenceStatus));
				sequenceRunrepository.update(currSequenceRun);
				return new ResponseEntity<>(currSequenceRun, HttpStatus.OK);
			}
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_updating_sequence_status", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/save/sequencerunresult",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<TestSequenceResult> saveSequenceRunResult(@RequestBody TestSequenceResult sequenceRunResult,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sequenceRunResult != null) {
			ObjectId testSequenceResultId = testSequenceResultRepository.saveAndReturnId(sequenceRunResult);
			testSequenceUtils.updateSquenceRunStatus(sequenceRunResult.getSequenceRunId(), TestStatus.EXECUTED);
			
			factsToCheckRepository.save(new FactToCheckByRuleEngine(testSequenceResultId, RuleFactType.TESTSEQUENCERESULT, false));
			return new ResponseEntity<>(sequenceRunResult, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_sequence_results", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/sequenceresults")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestSequenceResult>> getSequenceResults(@PathVariable ValidInputDevice deviceId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (deviceId != null) {
			List<TestSequenceResult> result = testSequenceResultRepository.getByDeviceId(deviceId.getValue());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_loading_sequence_results", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/sequencerunresults")
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestSequenceResult>> getSequenceRunResults(@PathVariable ValidInputSequence seqId,
			@ServerProvidedValue ValidInputLocale locale) {
		if (seqId != null) {
			List<TestSequenceResult> result = testSequenceResultRepository.getBySequenceId(seqId.getValue());
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_loading_sequence_results", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/result/groups",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getTestReportsByGroupIdsAndPagination(@RequestBody List<CompanyGroup> groups, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
		if (groups != null) {

			List<ObjectId> groupIds = portalUtils.extractIdsFromObjects(groups);
			if (groupIds != null && !groupIds.isEmpty()) {
				List<Device> devices = deviceUtils.getAllDevicesWithReportsByGroupId(groupIds, DeviceType.POD, ReportType.TESTSEQUENCE);
				if (devices != null) {
					List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);
					Map<String, Object> sequenceResults = new HashMap<>();
					if (deviceIds != null) {
						sequenceResults = testSequenceResultRepository.getSequenceResultsByDeviceIdWithPagination(deviceIds, page, size, filter);
					}
					return new ResponseEntity<>(sequenceResults, HttpStatus.OK);
				}
			}
			throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports_group", locale.getValue()));
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/result/devices",
			method = ValidRequestMethodType.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<Map<String, Object>> getSequenceResultsByDeviceIdsAndPagination(@RequestBody List<Device> devices, @RequestParam(
			required = false) String filter,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_PAGE_PAGINATION) int page,
			@RequestParam(
					defaultValue = StaticConfigItems.DEFAULT_SIZE_PAGINATION) int size,
			@ServerProvidedValue ValidInputLocale locale) {
		if (devices != null) {

			List<ObjectId> deviceIds = portalUtils.extractIdsFromObjects(devices);

			if (deviceIds != null && !deviceIds.isEmpty()) {
				Map<String, Object> sequenceResults = testSequenceResultRepository.getSequenceResultsByDeviceIdWithPagination(deviceIds, page, size,
						filter);
				return new ResponseEntity<>(sequenceResults, HttpStatus.OK);
			}
		}
		log.error("Error occured while retrieving test results for groups");
		throw new ApiRequestException(messageByLocaleService.getMessage("error_while_getting_reports", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/result/delete/selected",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
	public ResponseEntity<List<TestSequenceResult>> deleteSelectedNetworkReports(@RequestBody List<TestSequenceResult> sequenceResults,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sequenceResults != null) {
			for (TestSequenceResult sequenceResult : sequenceResults) {
				TestSequenceResult dbSequenceResult = testSequenceResultRepository.find(sequenceResult.getId());
				if (dbSequenceResult != null) {
					testSequenceResultRepository.delete(dbSequenceResult);
				}
			}
			return new ResponseEntity<>(sequenceResults, HttpStatus.OK);
		}
		log.error("Error occured while deleting selected sequence results!");
		throw new ApiRequestException(messageByLocaleService.getMessage("no_reports_provided", locale.getValue()));
	}

	@ValidRequestMapping(
			value = "/save/sequencestepresult",
			method = ValidRequestMethodType.POST)
	@PreAuthorize("hasAnyAuthority('ROLE_DEVICE')")
	public ResponseEntity<TestSequenceStepResult> saveSequenceStepResult(@RequestBody TestSequenceStepResult sequenceStepResult,
			@ServerProvidedValue ValidInputLocale locale) {
		if (sequenceStepResult != null) {
			testSequenceStepResultRepository.save(sequenceStepResult);
			return new ResponseEntity<>(sequenceStepResult, HttpStatus.OK);
		}
		throw new ApiRequestException(messageByLocaleService.getMessage("problem_occured_while_saving_sequence_results", locale.getValue()));
	}

}
