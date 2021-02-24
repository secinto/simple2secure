package com.simple2secure.portal.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.simple2secure.api.dto.DeviceDTO;
import com.simple2secure.api.dto.PublicDeviceDTO;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Context;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.DeviceInfoRepository;
import com.simple2secure.portal.repository.RuleDeviceMappingRepository;
import com.simple2secure.portal.utils.PortalUtils;

@Repository
@Transactional
public class DeviceInfoRepositoryImpl<T> extends DeviceInfoRepository {

	@Autowired
	private RuleDeviceMappingRepository ruleDeviceMappingRepository;
	
	@Autowired
	PortalUtils portalUtils;


	@PostConstruct
	public void init() {
		super.collectionName = "deviceInfo"; //$NON-NLS-1$
		super.className = DeviceInfo.class;
	}

	@Override
	public DeviceInfo findByDeviceId(ObjectId deviceId) {
		Query query = new Query(Criteria.where("_id").is(deviceId));
		return mongoTemplate.findOne(query, DeviceInfo.class);
	}

	@Override
	public List<DeviceInfo> findByDeviceType(String type) {
		Query query = new Query(Criteria.where("type").is(type.toString()));
		return mongoTemplate.find(query, DeviceInfo.class);
	}

	@Override
	public List<Device> findByContextId(ObjectId contextId) {
		AggregationOperation matchContext = Aggregation.match(new Criteria("_id").is(contextId));
		AggregationOperation lookUpContextGroup = Aggregation.lookup("companygroup", "_id", "contextId", "groups");
		AggregationOperation unwindGroups = Aggregation.unwind("deviceInfos");
		AggregationOperation lookUpGroupLicense = Aggregation.lookup("license", "groups._id", "groupId", "licenses");
		AggregationOperation lookUpDevInfo = Aggregation.lookup("deviceInfo", "licenses.deviceId", "_id", "deviceInfos");
		AggregationOperation unwindDeviceInfos = Aggregation.unwind("deviceInfos");

		Aggregation aggregation = Aggregation.newAggregation(Context.class, matchContext, lookUpContextGroup, unwindGroups, lookUpGroupLicense, lookUpDevInfo,
				unwindDeviceInfos);
		AggregationResults<DeviceDTO> results = mongoTemplate.aggregate(aggregation, "context", DeviceDTO.class);
		List<DeviceDTO> resultDocList = results.getMappedResults();
		List<Device> resultDeviceList = new ArrayList<>();
		for (DeviceDTO dDTO : resultDocList) {
			resultDeviceList.add(new Device(dDTO.getGroups(), dDTO.getDeviceInfos()));
		}
		return resultDeviceList;
	}

	@Override
	public List<Device> findByContextIdAndType(ObjectId contextId, String type, int page, int size, String filter) {
		AggregationOperation matchContext = Aggregation.match(new Criteria("_id").is(contextId));
		AggregationOperation lookUpContextGroup = Aggregation.lookup("companygroup", "_id", "contextId", "groups");
		AggregationOperation unwindGroups = Aggregation.unwind("groups");
		AggregationOperation lookUpGroupLicense = Aggregation.lookup("license", "groups._id", "groupId", "licenses");
		AggregationOperation unwindLicenses = Aggregation.unwind("licenses");
		AggregationOperation lookUpDevInfo = Aggregation.lookup("deviceInfo", "licenses.deviceId", "_id", "deviceInfos");
		AggregationOperation unwindDeviceInfos = Aggregation.unwind("deviceInfos");
		AggregationOperation matchDevType = Aggregation.match(new Criteria("deviceInfos.type").is(type).and("deviceInfos.deviceStatus").is(DeviceStatus.ONLINE));
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);
		
		String[] filterFields = { "groups.name", "deviceInfos.name", "deviceInfos.deviceStatus" };
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));
		
		Aggregation aggregation = Aggregation.newAggregation(Context.class, matchContext, lookUpContextGroup, unwindGroups, lookUpGroupLicense,
				unwindLicenses, lookUpDevInfo, unwindDeviceInfos, matchDevType, countTotal);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(Context.class, matchContext, lookUpContextGroup, unwindGroups, lookUpGroupLicense,
					unwindLicenses, lookUpDevInfo, unwindDeviceInfos, matchDevType, filtering, countTotal);
		}
		
		Object count = getCountResult(mongoTemplate.aggregate(aggregation, "deviceInfos", Object.class));
		
		int limit = portalUtils.getPaginationLimit(size);
		long skip = portalUtils.getPaginationStart(size, page, limit);

		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);
		
		aggregation = Aggregation.newAggregation(Context.class, matchContext, lookUpContextGroup, unwindGroups, lookUpGroupLicense,
				unwindLicenses, lookUpDevInfo, unwindDeviceInfos, matchDevType, paginationSkip, paginationLimit);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(Context.class, matchContext, lookUpContextGroup, unwindGroups, lookUpGroupLicense,
						unwindLicenses, lookUpDevInfo, unwindDeviceInfos, matchDevType, filtering, paginationSkip, paginationLimit);
		}

		AggregationResults<DeviceDTO> results = mongoTemplate.aggregate(aggregation, "context", DeviceDTO.class);
		List<DeviceDTO> resultDocList = results.getMappedResults();
		List<Device> resultDeviceList = new ArrayList<>();
		for (DeviceDTO dDTO : resultDocList) {
			resultDeviceList.add(new Device(dDTO.getGroups(), dDTO.getDeviceInfos()));
		}
		return resultDeviceList;
	}

	@Override
	public Map<String, Object> findAllPublicPodDevices(int page, int size, String filter) {
		
		AggregationOperation matchAvailabilityAndStatus = Aggregation.match(new Criteria("publiclyAvailable").is(true).and("deviceStatus").is("ONLINE"));
		AggregationOperation lookUpDeviceLicense = Aggregation.lookup("license", "_id", "deviceId", "licenses");
		AggregationOperation unwindLicenses = Aggregation.unwind("licenses");
		AggregationOperation lookUpLicenseGroup = Aggregation.lookup("companygroup", "licenses.groupId", "_id", "groups");
		AggregationOperation unwindGroups = Aggregation.unwind("groups");
		AggregationOperation countTotal = Aggregation.count().as(StaticConfigItems.COUNT_FIELD);
		
		String[] filterFields = { "groups.name", "name" };
		AggregationOperation filtering = Aggregation.match(defineFilterCriteriaWithManyFields(filterFields, filter));
		
		Aggregation aggregation = Aggregation.newAggregation(DeviceInfo.class, matchAvailabilityAndStatus, lookUpDeviceLicense, unwindLicenses, lookUpLicenseGroup,
				unwindGroups, countTotal);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(DeviceInfo.class, matchAvailabilityAndStatus, lookUpDeviceLicense, unwindLicenses, lookUpLicenseGroup,
					unwindGroups, filtering, countTotal);
		}

		Object count = getCountResult(mongoTemplate.aggregate(aggregation, "deviceInfo", Object.class));
		
		int limit = portalUtils.getPaginationLimit(size);
		long skip = portalUtils.getPaginationStart(size, page, limit);

		AggregationOperation paginationLimit = Aggregation.limit(limit);
		AggregationOperation paginationSkip = Aggregation.skip(skip);
		
		aggregation = Aggregation.newAggregation(DeviceInfo.class, matchAvailabilityAndStatus, lookUpDeviceLicense, unwindLicenses, lookUpLicenseGroup,
				unwindGroups, paginationSkip, paginationLimit);
		
		if (!Strings.isBlank(filter)) {
			aggregation = Aggregation.newAggregation(DeviceInfo.class, matchAvailabilityAndStatus, lookUpDeviceLicense, unwindLicenses, lookUpLicenseGroup,
					unwindGroups, filtering, paginationSkip, paginationLimit);
		}

		AggregationResults<PublicDeviceDTO> results = mongoTemplate.aggregate(aggregation, "deviceInfo", PublicDeviceDTO.class);
		
		List<Device> resultList = new ArrayList<Device>();
		
		for(PublicDeviceDTO pDDTO : results) {
			resultList.add(new Device(pDDTO.getGroups(), new DeviceInfo(pDDTO.get_id(), pDDTO.getName(), pDDTO.getType(), pDDTO.getDeviceStatus(), pDDTO.getLastOnlineTimestamp(), pDDTO.isPubliclyAvailable())));
		}
		Map<String, Object> devicesMap = new HashMap<>();
		devicesMap.put("devices", resultList);
		devicesMap.put("totalSize", count);

		return devicesMap;
	}

	@Override
	public void delete(DeviceInfo item) {
		ruleDeviceMappingRepository.deleteByDeviceId(item.getId());
		this.mongoTemplate.remove(Query.query(Criteria.where("_id").is(item.getId())), this.collectionName);
	}
}
