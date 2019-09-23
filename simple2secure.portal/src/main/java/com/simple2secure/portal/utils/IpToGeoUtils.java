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

package com.simple2secure.portal.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

@Component
public class IpToGeoUtils {

	private static Logger log = LoggerFactory.getLogger(IpToGeoUtils.class);

	private DatabaseReader dbReader;

	@PostConstruct
	public void initialize() {
		String fileName = "geoIP/geolite.mmdb";
		ClassLoader classLoader = getClass().getClassLoader();
		File database = new File(classLoader.getResource(fileName).getFile());
		try {
			dbReader = new DatabaseReader.Builder(database).build();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public CityResponse convertIPtoGeoLocation(String ip) {
		CityResponse response = null;
		try {
			InetAddress ipAddress = InetAddress.getByName(ip);
			response = dbReader.city(ipAddress);
		} catch (IOException | GeoIp2Exception e) {
			log.error(e.getMessage());
		}
		return response;
	}

}
