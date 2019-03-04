package com.simple2secure.test.portal.utils;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.maxmind.geoip2.model.CityResponse;
import com.simple2secure.portal.Simple2SecurePortal;
import com.simple2secure.portal.utils.IpToGeoUtils;

@ExtendWith({ SpringExtension.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { Simple2SecurePortal.class })
@ActiveProfiles("test")
public class TestIpToGeoUtils {

	@Autowired
	IpToGeoUtils ipToGeoUtils;

	@Test
	public void testConvertIpAdressToGeoLocationPositive() throws IOException {
		String ipAddress = "87.243.178.234";

		CityResponse response = ipToGeoUtils.convertIPtoGeoLocation(ipAddress);

		assertNotNull(response);
	}

	@Test
	public void testConvertIpAdressToGeoLocationLocalHost() throws IOException {
		String ipAddress = InetAddress.getLocalHost().getHostAddress();

		CityResponse response = ipToGeoUtils.convertIPtoGeoLocation(ipAddress);

		assertNull(response);
	}

}
