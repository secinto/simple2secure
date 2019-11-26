package com.simple2secure.portal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceType;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.portal.repository.DeviceInfoRepository;
import com.simple2secure.portal.repository.SystemUnderTestRepository;

@Component
public class SUTUtils {
	private static Logger log = LoggerFactory.getLogger(SUTUtils.class);
	
	@Autowired
	SystemUnderTestRepository sutRepository;
	
	@Autowired
	DeviceInfoRepository deviceInfoRepository;
	
	public void addProbeAsSUT(CompanyLicensePublic license) {
		SystemUnderTest sut = sutRepository.getByEndDeviceId(license.getDeviceId());
		DeviceInfo deviceInfo = deviceInfoRepository.findByDeviceId(license.getDeviceId());
		
		if(sut == null && deviceInfo != null && license != null) {
			SystemUnderTest newSut = new SystemUnderTest(license.getGroupId(), license.getDeviceId(), DeviceType.PROBE, 
					null, deviceInfo.getHostName(), deviceInfo.getIpAddress(), deviceInfo.getNetMask());
			
			sutRepository.save(newSut);
			log.info("New SUT has been stored in the db!");
		}
	}
}
