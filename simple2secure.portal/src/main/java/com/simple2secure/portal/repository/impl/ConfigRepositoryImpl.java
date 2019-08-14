package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.model.Config;
import com.simple2secure.portal.repository.ConfigRepository;

@Repository
@Transactional
public class ConfigRepositoryImpl extends ConfigRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "config"; //$NON-NLS-1$
		super.className = Config.class;
	}

	@Override
	public Config findByProbeId(String probeId) {
		Query query = new Query(Criteria.where("probeId").is(probeId));
		Config config = this.mongoTemplate.findOne(query, Config.class);
		return config;
	}

	@Override
	public void deleteByProbeId(String probeId) {
		Config config = findByProbeId(probeId);
		if (config != null) {
			this.delete(config);
		}
	}

	@Override
	public Config findByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId).and("isGroupConfiguration").is(true));
		Config config = this.mongoTemplate.findOne(query, Config.class);
		return config;
	}

	@Override
	public List<Config> findAllByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		List<Config> configs = this.mongoTemplate.find(query, Config.class);
		return configs;
	}

	@Override
	public void deleteByGroupId(String groupId) {
		List<Config> configs = findAllByGroupId(groupId);
		if (configs != null) {
			for (Config config : configs) {
				this.delete(config);
			}
		}
	}
}
