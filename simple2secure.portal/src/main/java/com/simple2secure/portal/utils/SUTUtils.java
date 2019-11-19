package com.simple2secure.portal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.portal.repository.SystemUnderTestRepository;

@Component
public class SUTUtils {
	private static Logger log = LoggerFactory.getLogger(SUTUtils.class);
	
	@Autowired
	SystemUnderTestRepository sutRepository;
	
	public void addProbeAsSUT(CompanyLicensePublic license) {
		SystemUnderTest sut = sutRepository.getByEndDeviceId(license.getDeviceId());
		
		if(sut == null) {
			SystemUnderTest newSut = new SystemUnderTest(license.getGroupId(), license.getDeviceId(), license.getDeviceInfo().getHostname(), "PROBE", 
					null, null, license.getDeviceInfo().getIpAddress(), license.getDeviceInfo().getNetMask());
			
			sutRepository.save(newSut);
			log.info("New SUT has been stored in the db!");
		}
	}
}
