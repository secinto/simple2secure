package com.simple2secure.portal.utils;

import org.springframework.stereotype.Component;

import com.simple2secure.api.model.DeviceStatus;
import com.simple2secure.api.model.SystemUnderTest;
import com.simple2secure.portal.providers.BaseServiceProvider;

@Component
public class SUTUtils extends BaseServiceProvider {

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
}
