package com.simple2secure.portal.repository.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.simple2secure.api.model.AdminGroup;
import com.simple2secure.api.model.User;
import com.simple2secure.portal.repository.AdminGroupRepository;

@Repository
@Transactional
public class AdminGroupRepositoryImpl extends AdminGroupRepository {

	@PostConstruct
	public void init() {
		super.collectionName = "adminGroup"; //$NON-NLS-1$
		super.className = AdminGroup.class;
	}

	@Override
	public AdminGroup getAdminGroupByUserId(String userId) {
		List<AdminGroup> adminGroups = this.mongoTemplate.findAll(AdminGroup.class);
		if(adminGroups != null) {
			for(AdminGroup adminGroup : adminGroups) {
				if(adminGroup.getAdmins().contains(userId)) {
					return adminGroup;
				}
			}
		}
		return null;
	}

	@Override
	public AdminGroup deleteByAdminGroupId(String adminGroupId) {
		Query query = new Query(Criteria.where("id").is(adminGroupId));
		AdminGroup adminGroup = this.mongoTemplate.findOne(query, AdminGroup.class);
		
		if(adminGroup != null) {
			delete(adminGroup);
			return adminGroup;
		}
		else {
			return null;
		}
	}
}
