package com.simple2secure.portal.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.simple2secure.api.dto.TestSequenceRunDTO;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.Test;
import com.simple2secure.api.model.TestRunType;
import com.simple2secure.api.model.TestSequence;
import com.simple2secure.api.model.TestSequenceResult;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.api.model.User;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.crypto.CryptoUtils;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseUtilsProvider;
import com.simple2secure.portal.validation.model.ValidInputContext;
import com.simple2secure.portal.validation.model.ValidInputDevice;
import com.simple2secure.portal.validation.model.ValidInputLocale;
import com.simple2secure.portal.validation.model.ValidInputPage;
import com.simple2secure.portal.validation.model.ValidInputSequence;
import com.simple2secure.portal.validation.model.ValidInputSize;
import com.simple2secure.portal.validation.model.ValidInputUser;

import lombok.extern.slf4j.Slf4j;
import simple2secure.validator.annotation.ServerProvidedValue;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.model.ValidRequestMethodType;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping(StaticConfigItems.SEQUENCE_API)
@Slf4j
public class TestSequenceController extends BaseUtilsProvider {

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

      return (ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_loading_sequences", locale);
   }

   @ValidRequestMapping(method = ValidRequestMethodType.POST)
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

      return (ResponseEntity<TestSequence>) buildResponseEntity("problem_occured_while_saving_sequence", locale);
   }

   /**
    * Deletes the specified test sequence from the portal.
    *
    * @param sequenceId
    * @param locale
    * @return
    * @throws ItemNotFoundRepositoryException
    */
   @ValidRequestMapping(method = ValidRequestMethodType.DELETE)
   @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER')")
   public ResponseEntity<TestSequence> deleteSequence(@PathVariable ValidInputSequence sequenceId,
         @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
      /*
       * TODO: Verify that the user is allowed to delete this sequence, currently no information about context and user is checked in
       * combination with the sequence selected.
       */
      if (!Strings.isNullOrEmpty(sequenceId.getValue())) {
         TestSequence sequence = testSequenceRepository.find(sequenceId.getValue());
         if (sequence != null) {
            testSequenceRepository.delete(sequence);
            log.debug("Test sequence with id {} and name {} deleted from user {}", sequence.getId());
            return new ResponseEntity<>(sequence, HttpStatus.OK);
         }
      }

      return (ResponseEntity<TestSequence>) buildResponseEntity("problem_occured_while_deleting_sequence", locale);

   }

   /**
    * Obtains the scheduled sequence for the specified device. This is only available for devices itself.
    *
    * @param deviceId
    * @param locale
    * @return
    * @throws ItemNotFoundRepositoryException
    */
   @ValidRequestMapping(value = "/scheduledSequence", method = ValidRequestMethodType.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
   @PreAuthorize("hasAnyAuthority('DEVICE')")
   public ResponseEntity<List<SequenceRun>> getScheduledSequence(@PathVariable ValidInputDevice deviceId,
         @ServerProvidedValue ValidInputLocale locale) throws ItemNotFoundRepositoryException {
      DeviceInfo deviceInfo = deviceInfoRepository.findByDeviceId(deviceId.getValue());

      if (deviceInfo != null) {
         deviceInfo.setLastOnlineTimestamp(System.currentTimeMillis());
         log.debug("Updating last online time for device id {} and name {}", deviceInfo.getDeviceId(), deviceInfo.getName());
         deviceInfoRepository.update(deviceInfo);
         log.debug("Updated last online time for device id {}", deviceInfo.getDeviceId());
         ResponseEntity<List<SequenceRun>> respEntObj = testUtils.getSequenceByDeviceId(deviceId.getValue(), locale);
         List<SequenceRun> allSeqRuns = respEntObj.getBody();
         List<SequenceRun> filteredSeqRuns = allSeqRuns.stream().filter(sR -> sR.getSequenceStatus().equals(TestStatus.PLANNED))
               .collect(Collectors.toList());
         return new ResponseEntity<>(filteredSeqRuns, HttpStatus.OK);
      }

      return (ResponseEntity<List<SequenceRun>>) buildResponseEntity("problem_occured_while_retrieving_scheduled_sequences", locale);

   }

   @ValidRequestMapping(value = "/scheduleSequence", method = ValidRequestMethodType.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
   @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
   public ResponseEntity<SequenceRun> addSequenceToSchedule(@RequestBody TestSequence sequence,
         @ServerProvidedValue ValidInputContext contextId, @ServerProvidedValue ValidInputUser userId,
         @ServerProvidedValue ValidInputLocale locale) {
      if (sequence != null && !Strings.isNullOrEmpty(contextId.getValue()) && !Strings.isNullOrEmpty(userId.getValue())) {

         User user = userRepository.find(userId.getValue());

         if (user != null) {

            TestSequence currSequence = testSequenceRepository.find(sequence.getId());

            if (currSequence != null) {
               SequenceRun seqRun = new SequenceRun(sequence.getId(), sequence.getName(), sequence.getPodId(), contextId.getValue(),
                     TestRunType.MANUAL_PORTAL, currSequence.getSequenceContent(), TestStatus.PLANNED, System.currentTimeMillis());

               sequenceRunrepository.save(seqRun);

               log.debug("A new sequence run {} has been created and added to the schedule on the portal from user {}",
                     currSequence.getId(), user.getId());

               notificationUtils.addNewNotificationPortal(sequence.getName() + " has been scheduled using the portal by " + user.getEmail(),
                     contextId.getValue());

               return new ResponseEntity<>(seqRun, HttpStatus.OK);
            }
         }
      }

      return (ResponseEntity<SequenceRun>) buildResponseEntity("problem_occured_while_scheduling_sequence", locale);

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

      return (ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_retrieving_scheduled_sequences", locale);

   }

   @ValidRequestMapping(value = "/update/status", method = ValidRequestMethodType.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
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

      return (ResponseEntity<SequenceRun>) buildResponseEntity("problem_occured_while_updating_sequence_status", locale);

   }

   @ValidRequestMapping(value = "/save/sequencerunresult", method = ValidRequestMethodType.POST)
   @PreAuthorize("hasAnyAuthority('DEVICE')")
   public ResponseEntity<TestSequenceResult> saveSequenceRunResult(@RequestBody TestSequenceResult sequenceRunResult,
         @ServerProvidedValue ValidInputLocale locale) {
      if (sequenceRunResult != null) {
         testSequenceResultRepository.save(sequenceRunResult);
         return new ResponseEntity<>(sequenceRunResult, HttpStatus.OK);
      }

      return (ResponseEntity<TestSequenceResult>) buildResponseEntity("problem_occured_while_saving_sequence_results", locale);
   }

   @ValidRequestMapping(value = "/sequenceresults")
   @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
   public ResponseEntity<List<TestSequenceResult>> getSequenceResults(@PathVariable ValidInputDevice deviceId,
         @ServerProvidedValue ValidInputLocale locale) {
      if (deviceId != null) {
         List<TestSequenceResult> result = testSequenceResultRepository.getByDeviceId(deviceId.getValue());
         return new ResponseEntity<>(result, HttpStatus.OK);
      }

      return (ResponseEntity<List<TestSequenceResult>>) buildResponseEntity("problem_occured_while_loading_sequence_results", locale);
   }

   @ValidRequestMapping(value = "/sequencerunresults")
   @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN', 'SUPERUSER', 'USER')")
   public ResponseEntity<List<TestSequenceResult>> getSequenceRunResults(@PathVariable ValidInputSequence seqId,
         @ServerProvidedValue ValidInputLocale locale) {
      if (seqId != null) {
         List<TestSequenceResult> result = testSequenceResultRepository.getBySequenceId(seqId.getValue());
         return new ResponseEntity<>(result, HttpStatus.OK);
      }

      return (ResponseEntity<List<TestSequenceResult>>) buildResponseEntity("problem_occured_while_loading_sequence_results", locale);
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
               return (ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_loading_sequence_results", locale);
            }
         }
      }
      return (ResponseEntity<Map<String, Object>>) buildResponseEntity("problem_occured_while_loading_sequence_results", locale);
   }

}
