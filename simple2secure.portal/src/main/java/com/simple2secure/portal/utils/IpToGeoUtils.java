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
