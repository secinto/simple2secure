package com.simple2secure.portal.repository;

import java.util.List;

import com.simple2secure.api.model.Step;
import com.simple2secure.portal.dao.MongoRepository;

public abstract class StepRepository extends MongoRepository<Step> {
	public abstract List<Step> getStepsByProbeId(String probeId, boolean select_all);
	
	public abstract List<Step> getStepsByGroupId(String groupId, boolean select_all, boolean isGroupStep);
	
	public abstract Step getByNameAndGroupId(String name, String groupId);
	
	public abstract Step getByNameAndProbeId(String name, String probeId);

	public abstract void deleteByProbeId(String probeId);
	
	public abstract void deleteByGroupId(String groupId);

}
