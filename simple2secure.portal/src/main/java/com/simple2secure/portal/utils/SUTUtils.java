package com.simple2secure.portal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.CompanyLicensePublic;
import com.simple2secure.api.model.DeviceInfo;
import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.api.model.DeviceType;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
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
					null, deviceInfo.getHostName(), deviceInfo.getIpAddress(), deviceInfo.getNetMask(), deviceInfo.getDeviceStatus(), deviceInfo.getLastOnlineTimestamp());
			
			sutRepository.save(newSut);
			log.info("New SUT has been stored in the db!");
		}
	}
	
	/**
	 * This method checks the current status (online, offline, unknown) of the sut according to the lastOnlineTimestamp
	 *
	 * @param sut
	 * @return
	 */
	public DeviceStatus getDeviceStatus(SystemUnderTest sut) {
		// make it multilingual
		if (sut.getLastOnlineTimestamp() == 0) {
			return DeviceStatus.UNKNOWN;
		} else {
			long timeDiff = System.currentTimeMillis() - sut.getLastOnlineTimestamp();
			if (timeDiff > 60000) {
				return DeviceStatus.OFFLINE;
			} else {
				return DeviceStatus.ONLINE;
			}
		}
	}
	
	
	public void updateSUTLastOnlineTtimestamp(String deviceId, long lastOnlineTimestamp) throws ItemNotFoundRepositoryException {
		SystemUnderTest sut = sutRepository.getByEndDeviceId(deviceId);
		sut.setLastOnlineTimestamp(lastOnlineTimestamp);
		sutRepository.update(sut);
	}
}
 