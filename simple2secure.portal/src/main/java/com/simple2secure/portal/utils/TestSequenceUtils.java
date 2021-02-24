package com.simple2secure.portal.utils;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.simple2secure.api.dto.TestSUTDataInput;
import com.simple2secure.api.dto.TestSequenceDTO;
import com.simple2secure.api.dto.TestWebDTO;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.TestObjWeb;
import com.simple2secure.api.model.TestSequence;
import com.simple2secure.api.model.TestStatus;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.providers.BaseUtilsProvider;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Component
@Slf4j
public class TestSequenceUtils extends BaseUtilsProvider {

	/**
	 * This method checks if the given User has the given rights to edit/delete given test sequence.
	 *
	 * @param ...Test
	 *          The @TestSequence which should be edit/delete-ed.
	 * @return ...true if the User has the rights to edit/delete the Test Sequence, false if not.
	 */
	public boolean hasUserRightsForTestSequence(TestSequence testSequence, String userId) {
		CompanyLicensePublic license = licenseRepository.findByDeviceId(testSequence.getPodId());
		CompanyGroup group = groupRepository.find(license.getGroupId());
		ContextUserAuthentication contextUA = contextUserAuthRepository.getByContextIdAndUserId(group.getContextId(), userId);
		if (contextUA != null) {
			if (contextUA.getUserRole().equals(UserRole.SUPERADMIN) || contextUA.getUserRole().equals(UserRole.ADMIN)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This function creates a TestSequenceDTO object which is used in web
	 *
	 * @param deviceId
	 * @param page
	 * @param size
	 * @return
	 */

	public List<TestSequenceDTO> getAllSequencesByDeviceId(ObjectId deviceId, int page, int size, String filter) {
		List<TestSequence> sequences = testSequenceRepository.getByDeviceId(deviceId, page, size, filter);
		List<TestSequenceDTO> sequenceDTOList = new ArrayList<>();
		if (sequences != null) {
			for (TestSequence sequence : sequences) {
				List<TestWebDTO> tests = new ArrayList<>();
				if (sequence.getTests() != null && !sequence.getTests().isEmpty()) {

					List<String> testIds = new ArrayList<>();

					for (TestSUTDataInput sutDataInput : sequence.getTests()) {
						testIds.add(sutDataInput.getTest().getTestId().toHexString());
					}

					List<TestObjWeb> testObjWebList = testUtils.convertTestIdsToTestObjectForWeb(testIds);

					if (testObjWebList != null) {
						tests = testUtils.createTestWebDTOsFromTestWebObjList(testObjWebList);
					}

				}
				sequenceDTOList.add(new TestSequenceDTO(sequence, tests));
			}
		}

		return sequenceDTOList;
	}

	/**
	 * This function clones a test sequence
	 * 
	 * @param sequence
	 * @return
	 */
	public TestSequence cloneSequence(TestSequence sequence) {
		TestSequence clonedSequence = new TestSequence();
		clonedSequence.setLastChangedTimeStamp(System.currentTimeMillis());
		clonedSequence.setPodId(sequence.getPodId());
		clonedSequence.setName(sequence.getName() + "_" + System.currentTimeMillis());
		clonedSequence.setTests(sequence.getTests());

		testSequenceRepository.save(clonedSequence);

		return clonedSequence;

	}
	
	/**
	 * This function clones a test sequence
	 * 
	 * @param sequence
	 * @return
	 */
	public void updateSquenceRunStatus(ObjectId sequenceRunId, TestStatus status) {
		SequenceRun sequenceRun = sequenceRunrepository.find(sequenceRunId);
		sequenceRun.setSequenceStatus(status);
		
		try {
			sequenceRunRepository.update(sequenceRun);
		} catch (ItemNotFoundRepositoryException e) {
			log.error("A problem occured while updating the status of the sequence run: ", sequenceRun.getId());
		}
		
		notificationUtils.addNewNotification(
				sequenceRun.getSequenceName() + " has been executed by the pod " + sequenceRun.getHostname(), sequenceRun.getContextId(),
				null, false);

	}

}
