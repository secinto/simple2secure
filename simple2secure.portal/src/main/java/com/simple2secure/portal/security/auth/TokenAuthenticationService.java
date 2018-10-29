package com.simple2secure.portal.security.auth;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Strings;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicense;
import com.simple2secure.api.model.Settings;
import com.simple2secure.api.model.Token;
import com.simple2secure.api.model.User;
import com.simple2secure.api.model.UserRole;
import com.simple2secure.portal.dao.exceptions.ItemNotFoundRepositoryException;
import com.simple2secure.portal.repository.LicenseRepository;
import com.simple2secure.portal.repository.SettingsRepository;
import com.simple2secure.portal.repository.TokenRepository;
import com.simple2secure.portal.repository.UserRepository;
import com.simple2secure.portal.utils.BeanUtil;
import com.simple2secure.portal.utils.PortalUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {

	public static final Logger log = LoggerFactory.getLogger(TokenAuthenticationService.class);

	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	static final String CLAIMS_SUBJECT = "data";
	static final String CLAIM_USERID = "userID";
	static final String CLAIM_USERROLE = "userRole";
	static final String CLAIM_PROBEID = "probeID";

	public static String addLicenseAuthentication(String probeId, CompanyGroup group, CompanyLicense license) {
		SettingsRepository settingsRepository = BeanUtil.getBean(SettingsRepository.class);
		if (!Strings.isNullOrEmpty(probeId) && group != null && license != null) {

			List<Settings> settings = settingsRepository.findAll();

			long expirationTime = 0;

			if (settings != null) {
				if (settings.size() == 1) {
					expirationTime = PortalUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenProbeValidityTime(),
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
					.signWith(SignatureAlgorithm.HS512, license.getTokenSecret()).compact();

			return accessToken;
		} else {
			log.error("Probe id or group is null");
			return null;
		}

	}

	public static void addAuthentication(HttpServletResponse res, String username, Collection<? extends GrantedAuthority> collection) {
		UserRepository userRepository = BeanUtil.getBean(UserRepository.class);
		TokenRepository tokenRepository = BeanUtil.getBean(TokenRepository.class);
		SettingsRepository settingsRepository = BeanUtil.getBean(SettingsRepository.class);
		User user = userRepository.findByEmailOnlyActivated(username);
		if (user != null) {

			Token token = tokenRepository.findByUserId(user.getId());

			Claims claims = Jwts.claims().setSubject(CLAIMS_SUBJECT);
			claims.put(CLAIM_USERID, user.getId());
			if (collection != null && collection.size() == 1) {
				claims.put(CLAIM_USERROLE, collection.iterator().next().getAuthority());
			} else {
				claims.put(CLAIM_USERROLE, "");
			}

			List<Settings> settings = settingsRepository.findAll();

			long expirationTime = 0;

			if (settings != null) {
				if (settings.size() == 1) {
					expirationTime = PortalUtils.convertTimeUnitsToMilis(settings.get(0).getAccessTokenValidityTime(),
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

	public static Authentication getAuthentication(HttpServletRequest request) {
		String accessToken = resolveToken(request);
		if (accessToken != null) {
			TokenRepository tokenRepository = BeanUtil.getBean(TokenRepository.class);
			UserRepository userRepository = BeanUtil.getBean(UserRepository.class);
			Token token = tokenRepository.findByAccessToken(accessToken.replace(TOKEN_PREFIX, "").trim());

			if (token != null) {
				User user = userRepository.find(token.getUserId());

				if (user != null) {

					boolean isAccessTokenValid = validateToken(accessToken, user.getPassword());

					if (isAccessTokenValid) {
						return user != null
								? new UsernamePasswordAuthenticationToken(user, null,
										CustomAuthenticationProvider.getAuthorities(user.getUserRole().name()))
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

				LicenseRepository licenseRepository = BeanUtil.getBean(LicenseRepository.class);

				CompanyLicense license = licenseRepository.findByAccessToken(accessToken.replace(TOKEN_PREFIX, "").trim());

				if (license != null) {
					boolean isAccessTokenValid = validateToken(accessToken, license.getTokenSecret());

					if (isAccessTokenValid) {
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

	private static String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader(HEADER_STRING);
		if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.replace(TOKEN_PREFIX, "").trim();
		}
		return null;
	}

	public static boolean validateToken(String token, String secretKey) {
		try {
			Date expirationDate = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();

			if (PortalUtils.isAccessTokenExpired(expirationDate)) {
				return false;
			}
			log.info("Token still valid!" + " Expiration date: " + expirationDate.toString());
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.error("Error: {}", e);
			return false;
		}
	}

	public static Date getTokenExpirationDate(String token, String secretKey) {
		try {
			Date expirationDate = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
			return expirationDate;
		} catch (JwtException | IllegalArgumentException e) {
			log.error("Error: {}", e);
			return null;
		}
	}

}
