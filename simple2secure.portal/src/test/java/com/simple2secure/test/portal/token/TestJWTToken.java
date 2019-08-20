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
package com.simple2secure.test.portal.token;

import java.util.Date;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.portal.security.auth.CustomAuthenticationProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Disabled
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
