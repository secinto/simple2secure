package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.TestSequence;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestSequenceRepository extends MongoRepository<TestSequence> {

	public abstract List<TestSequence> getByDeviceId(String deviceId, int page, int size);

	public abstract TestSequence getSequenceByName(String name);

	public abstract TestSequence getSequenceByNameAndDeviceId(String name, String deviceId);

	public abstract long getCountOfSequencesWithDeviceid(String deviceId);

}
