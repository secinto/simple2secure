package com.simple2secure.portal.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.simple2secure.api.model.TestSequence;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class TestSequenceRepository extends MongoRepository<TestSequence> {

	public abstract List<TestSequence> getByDeviceId(ObjectId deviceId, int page, int size, String filter);

	public abstract long getCountOfSequencesWithDeviceid(ObjectId deviceId);
}
