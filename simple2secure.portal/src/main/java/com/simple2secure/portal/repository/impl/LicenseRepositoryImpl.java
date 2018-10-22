package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.simple2secure.api.model.CompanyLicense;
import com.simple2secure.portal.repository.LicenseRepository;

@Repository
@Transactional
public class LicenseRepositoryImpl extends LicenseRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "license";
		super.className = CompanyLicense.class;
	}

	@Override
	public List<CompanyLicense> findByGroupId(String groupId) {
		Query query = new Query(Criteria.where("groupId").is(groupId));
		return this.mongoTemplate.find(query, CompanyLicense.class, this.collectionName);
	}

	@Override
	public List<CompanyLicense> findByUserId(String userId) {
		Query query = new Query(Criteria.where("userId").is(userId));
		return this.mongoTemplate.find(query, CompanyLicense.class, this.collectionName);
	}

	@Override
	public CompanyLicense findByGroupAndUserId(String groupId, String userId) {
		Query query = new Query(Criteria.where("userId").is(userId).and("groupId").is(groupId));
		List<CompanyLicense> license = this.mongoTemplate.find(query, CompanyLicense.class, this.collectionName);
		
		if(license != null && license.size() == 1) {
			return license.get(0);
		}
		else {
			return null;
		}
	}

	@Override
	public CompanyLicense findByProbeId(String probeId) {
		Query query = new Query(Criteria.where("probeId").is(probeId));
		return this.mongoTemplate.findOne(query, CompanyLicense.class, this.collectionName);
	}
	
	@Override
	public void deleteByGroupId(String groupId) {
		List<CompanyLicense> licenses = findByGroupId(groupId);
		
		if(licenses != null) {
			for(CompanyLicense license : licenses) {
				this.delete(license);
			}
		}
		
	}

	@Override
	public CompanyLicense findByAccessToken(String accessToken) {
		Query query = new Query(Criteria.where("accessToken").is(accessToken));
		return this.mongoTemplate.findOne(query, CompanyLicense.class, this.collectionName);
	}	
}
