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
package com.simple2secure.portal.security.auth;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.ContextUserAuthentication;
import com.simple2secure.api.model.CurrentContext;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.Token;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.ContextUserAuthRepository;
import com.simple2secure.portal.repository.CurrentContextRepository;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.utils.PortalUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenAuthenticationService {

	public static final Logger log = LoggerFactory.getLogger(TokenAuthenticationService.class);

	@Autowired
	SettingsRepository settingsRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	LicenseRepository licenseRepository;

	@Autowired
	CurrentContextRepository currentContextRepository;

	@Autowired
	ContextUserAuthRepository contextUserAuthRepository;

	@Autowired
	PortalUtils portalUtils;

	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	static final String CLAIMS_SUBJECT = "data";
	static final String CLAIM_USERID = "userID";
	static final String CLAIM_USERROLE = "userRole";
	static final String CLAIM_PROBEID = "probeID";

	/**
	 * This function is used to create probe authentication token so that it is available to send data to the portal
	 *
	 * @param probeId
	 * @param group
	 * @param license
	 * @return
	 */
	public String addProbeAuthentication(String probeId, CompanyGroup group, CompanyLicensePrivate license) {
		if (!Strings.isNullOrEmpty(probeId) && group != null && license != null) {

			List<Settings> settings = settingsRepository.findAll();

			long expirationTime = 0;

			if (settings != null) {
				if (settings.size() == 1) {
					expirationTime = portalUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenProbeValidityTime(),
							settings.get(0).getAccessTokenProbeValidityUnit());
				} else {
					return null;
				}
			} else {
				return null;
			}

			Claims claims = Jwts.claims().setSubject(CLAIMS_SUBJECT);
			claims.put(CLAIM_PROBEID, probeId);
			claims.put(CLAIM_USERROLE, UserRole.PROBE);
			String accessToken = Jwts.builder().setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + expirationTime))
					.signWith(SignatureAlgorithm.ES512, license.getTokenSecret()).compact();

			return accessToken;
		} else {
			log.error("Probe id or group is null");
			return null;
		}

	}

	/**
	 * This function is used to create pod authentication token so that it is available to send data to the portal
	 *
	 * @param podId
	 * @param group
	 * @param license
	 * @return
	 */
	public String addPodAuthentication(String podId, CompanyGroup group, CompanyLicensePrivate license) {
		if (!Strings.isNullOrEmpty(podId) && group != null && license != null) {

			List<Settings> settings = settingsRepository.findAll();

			long expirationTime = 0;

			if (settings != null) {
				if (settings.size() == 1) {
					expirationTime = portalUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenProbeValidityTime(),
							settings.get(0).getAccessTokenProbeValidityUnit());
				} else {
					return null;
				}
			} else {
				return null;
			}

			Claims claims = Jwts.claims().setSubject(CLAIMS_SUBJECT);
			claims.put(CLAIM_PROBEID, podId);
			claims.put(CLAIM_USERROLE, UserRole.POD);
			String accessToken = Jwts.builder().setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + expirationTime))
					.signWith(SignatureAlgorithm.HS512, license.getTokenSecret()).compact();

			return accessToken;
		} else {
			log.error("Pod id or group is null");
			return null;
		}

	}

	public void addAuthentication(HttpServletResponse res, String username, Collection<? extends GrantedAuthority> collection) {
		User user = userRepository.findByEmailOnlyActivated(username);
		if (user != null) {

			Token token = tokenRepository.findByUserId(user.getId());

			Claims claims = Jwts.claims().setSubject(CLAIMS_SUBJECT);
			claims.put(CLAIM_USERID, user.getId());

			List<Settings> settings = settingsRepository.findAll();

			long expirationTime = 0;

			if (settings != null) {
				if (settings.size() == 1) {
					expirationTime = portalUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenValidityTime(),
							settings.get(0).getAccessTokenValidityUnit());
				}
			}

			String accessToken = Jwts.builder().setClaims(claims).setSubject(username)
					.setExpiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(SignatureAlgorithm.HS512, user.getPassword())
					.compact();

			if (token == null) {
				token = new Token(user.getId(), "", accessToken, "", new Date(System.currentTimeMillis()));
				tokenRepository.save(token);
			} else {
				token.setAccessToken(accessToken);
				token.setLastLoginDate(new Date(System.currentTimeMillis()));
				try {
					tokenRepository.update(token);
				} catch (ItemNotFoundRepositoryException e) {
					log.error("Error occured: {}", e);
				}
			}

			res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + accessToken);
		} else {
			log.error("User {} cannot be found.", username);
		}

	}

	public Authentication getAuthentication(HttpServletRequest request) {
		String accessToken = resolveToken(request);
		if (accessToken != null) {
			Token token = tokenRepository.findByAccessToken(accessToken.replace(TOKEN_PREFIX, "").trim());
			UserRole userRole = UserRole.LOGINUSER;
			if (token != null) {
				User user = userRepository.find(token.getUserId());

				if (user != null) {

					boolean isAccessTokenValid = validateToken(accessToken, user.getPassword());

					if (isAccessTokenValid) {

						CurrentContext currentContext = currentContextRepository.findByUserId(user.getId());

						if (currentContext != null) {
							ContextUserAuthentication contextUserAuthentication = contextUserAuthRepository
									.find(currentContext.getContextUserAuthenticationId());

							if (contextUserAuthentication != null) {
								userRole = contextUserAuthentication.getUserRole();
							}

						}

						return user != null
								? new UsernamePasswordAuthenticationToken(user, null, CustomAuthenticationProvider.getAuthorities(userRole.name()))
								: null;
					} else {
						return null;
					}
				} else {
					log.error("User with following id: {} does not exist", token.getUserId());
					return null;
				}

			}

			else {
				// Handle token for probeId
				// Check if there is a token with this id in the licenseRepo - for probe
				CompanyLicensePrivate license = licenseRepository.findByAccessToken(accessToken.replace(TOKEN_PREFIX, "").trim());

				if (license != null) {
					boolean isAccessTokenValid = validateToken(accessToken, license.getTokenSecret());

					if (isAccessTokenValid) {
						if (!Strings.isNullOrEmpty(license.getPodId())) {
							return license != null
									? new UsernamePasswordAuthenticationToken(license, null, CustomAuthenticationProvider.getAuthorities(UserRole.POD.name()))
									: null;
						}
						return license != null
								? new UsernamePasswordAuthenticationToken(license, null, CustomAuthenticationProvider.getAuthorities(UserRole.PROBE.name()))
								: null;
					} else {
						return null;
					}
				} else {
					log.error("Token not found");
					return null;

				}
			}
		} else {
			log.error("Provided request does not contain accessToken in the header");
			return null;
		}

	}

	private String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader(HEADER_STRING);
		if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.replace(TOKEN_PREFIX, "").trim();
		}
		return null;
	}

	public boolean validateToken(String token, String secretKey) {
		try {
			Date expirationDate = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();

			if (portalUtils.isAccessTokenExpired(expirationDate)) {
				return false;
			}
			log.info("Token still valid!" + " Expiration date: " + expirationDate.toString());
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.error("Error: {}", e);
			return false;
		}
	}

	public Date getTokenExpirationDate(String token, String secretKey) {
		try {
			Date expirationDate = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
			return expirationDate;
		} catch (JwtException | IllegalArgumentException e) {
			log.error("Error: {}", e);
			return null;
		}
	}

}
