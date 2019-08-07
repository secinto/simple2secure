package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestCaseSequence;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestSequenceRepository extends MongoRepository<TestCaseSequence> {

	public abstract List<TestCaseSequence> getAllIsFinishedAndScheduled(boolean isFinished, boolean isScheduled);

}
