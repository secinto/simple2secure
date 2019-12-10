/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
package com.simple2secure.test.portal.utils;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.utils.IpToGeoUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestIpToGeoUtils {

	@Autowired
	IpToGeoUtils ipToGeoUtils;
	/*
	 * @Test public void testConvertIpAdressToGeoLocationPositive() throws IOException { String ipAddress = "87.243.178.234";
	 * 
	 * CityResponse response = ipToGeoUtils.convertIPtoGeoLocation(ipAddress);
	 * 
	 * assertNotNull(response); }
	 * 
	 * @Test public void testConvertIpAdressToGeoLocationLocalHost() throws IOException { String ipAddress =
	 * InetAddress.getLocalHost().getHostAddress();
	 * 
	 * CityResponse response = ipToGeoUtils.convertIPtoGeoLocation(ipAddress);
	 * 
	 * assertNull(response); }
	 */
}
