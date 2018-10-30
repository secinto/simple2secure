package com.simple2secure.test.portal.token;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.portal.security.auth.CustomAuthenticationProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TestJWTToken {

	private static Logger log = LoggerFactory.getLogger(TestJWTToken.class);

	static final long EXPIRATIONTIME = 864000000; // 10 days
	static final String SECRET = "ThisIsASecret";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";

	@Test
	public void testJWTTokenDate() throws Exception {
		Claims claims = Jwts.claims().setSubject("test");
		claims.put("userID", CustomAuthenticationProvider.userID);

		long currentDate = System.currentTimeMillis();

		log.info("CurrentDate {}", currentDate);
		log.info("CurrentDate + Expiration {}", currentDate + EXPIRATIONTIME);
		log.info("Date {}", new Date(currentDate + EXPIRATIONTIME));

		String JWT = Jwts.builder().setClaims(claims).setSubject("testuser").setExpiration(new Date(currentDate + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		if (!Strings.isNullOrEmpty(JWT)) {
			log.info("JWT Token {}", JWT);
		}
	}
}
