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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.api.model.validation.ValidInputLocale;
import com.simple2secure.api.model.validation.ValidInputParamType;
import com.simple2secure.api.model.validation.ValidatedInput;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.repository.GroupRepository;
import com.simple2secure.portal.validator.ValidRequestMapping;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class PortalUtils {
	private static Logger log = LoggerFactory.getLogger(PortalUtils.class);

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	GroupRepository groupRepository;

	static final String CLAIM_POD = "podID";
	static final String CLAIMS_SUBJECT = "data";

	/**
	 * This function generates a token(activation, invitation, paswordReset) for each user
	 *
	 * @return
	 */
	public synchronized String generateToken() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * This function checks if processor with the provided name or class already exist in the database. New processor will be only added if it
	 * does not exist.
	 *
	 * @param processors
	 * @param processor
	 * @return
	 */
	public boolean checkIfListAlreadyContainsProcessor(List<Processor> processors, Processor processor) {
		for (Processor processor_item : processors) {
			if (processor_item.getName().trim().equals(processor.getName().trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check before each request if access token has expired
	 *
	 * @param expirationDate
	 * @return
	 */
	public boolean isAccessTokenExpired(Date expirationDate) {
		Date currentDate = new Date(System.currentTimeMillis());

		if (expirationDate.before(currentDate)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Converts the given TimeUnits time to milliseconds.
	 *
	 * @param time
	 *          The value of the time unit.
	 * @param timeUnit
	 *          The unit in which the time is measured.
	 * @return The specified amount of time in milliseconds.
	 */
	public long convertTimeUnitsToMilis(long time, TimeUnit timeUnit) {
		if (timeUnit != null) {
			return timeUnit.toMillis(time);
		} else {
			return 0;
		}
	}

	/**
	 * This function checks if this group has children groups
	 *
	 * @param group
	 * @return
	 */
	public static boolean groupHasChildren(CompanyGroup group) {
		if (group != null) {
			if (group.getChildrenIds() != null) {
				if (group.getChildrenIds().size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * This function finds the parent group of the current child
	 *
	 * @param group
	 * @return
	 */
	public CompanyGroup findTheParentGroup(CompanyGroup group) {

		if (!Strings.isNullOrEmpty(group.getParentId())) {
			CompanyGroup parentGroup = groupRepository.find(group.getParentId());
			if (parentGroup != null) {
				return parentGroup;
			}
		}
		return null;
	}

	/**
	 * This function searches recursively for all dependent groups until the root group is found
	 *
	 * @param group
	 * @return
	 */
	public List<CompanyGroup> findAllParentGroups(CompanyGroup group) {
		List<CompanyGroup> foundGroups = new ArrayList<>();
		foundGroups.add(group);
		boolean rootGroupFound = false;
		while (!rootGroupFound) {
			CompanyGroup parentGroup = findTheParentGroup(group);
			if (parentGroup != null) {
				foundGroups.add(parentGroup);
				if (parentGroup.isRootGroup()) {
					rootGroupFound = true;
				} else {
					group = parentGroup;
				}
			} else {
				rootGroupFound = true;
			}
		}
		return foundGroups;
	}

	/**
	 * This function checks if the invitation token is still valid
	 *
	 * @param expirationTime
	 * @return
	 */
	public boolean checkIfTokenIsStillValid(long expirationTime) {
		if (System.currentTimeMillis() <= expirationTime) {
			return true;
		}
		return false;
	}

	/**
	 * This function reads the files from the resources folder according to the folder name
	 *
	 * @param folder
	 * @return
	 */
	private static File[] getResourceFolderFiles(String folder) {
		URL url = Resources.getResource(folder);
		String path = url.getPath();
		log.debug("Folder on the following path {} found", path);
		return new File(path).listFiles();
	}

	/**
	 * This is a function which reads the files from the resources folder and converts to the byte array in order to prepare them for download
	 *
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public byte[] downloadFile() throws IOException, URISyntaxException {
		File[] probe = getResourceFolderFiles("probe");
		byte[] array = Files.readAllBytes(probe[0].toPath());
		return array;
	}

	/**
	 * This function converts an input stream object to string
	 *
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public String convertInputStreamToString(InputStream inputStream) throws IOException {
		return IOUtils.toString(inputStream, "UTF-8");
	}

	/**
	 * This function generates pod token which is used for communication between pod and portal. With this token we will be able to send
	 * request to the pod service from the portal.
	 *
	 * @return
	 */
	public String generatePodToken(String deviceId, String tokenSecret) {

		Claims claims = Jwts.claims().setSubject(CLAIMS_SUBJECT);
		claims.put(CLAIM_POD, deviceId);

		String podToken = Jwts.builder().setExpiration(new Date(System.currentTimeMillis() + 3600000))
				.signWith(SignatureAlgorithm.HS512, tokenSecret).compact();

		return podToken;
	}

	/**
	 * This function returns the limit value for the pagination, according to the provided parameters (page and size)
	 *
	 * @param size
	 * @param page
	 * @return
	 */
	public int getPaginationLimit(int size) {
		if (size == 0) {
			size = StaticConfigItems.DEFAULT_VALUE_SIZE;
		}
		return size;
	}

	/**
	 * This function returns the value of the documents which has to be skipped when querying from the database.
	 *
	 * @param size
	 * @param page
	 * @param limit
	 * @return
	 */
	public int getPaginationStart(int size, int page, int limit) {
		if (size == 0) {
			size = StaticConfigItems.DEFAULT_VALUE_SIZE;
		}
		return ((page + 1) * size) - limit;
	}

	/**
	 * This functions extracts the id from the list of objects and adds it to the list of the strings which is returned
	 *
	 * @param groups
	 * @return
	 */
	public List<String> extractIdsFromObjects(List<?> objects) {
		List<String> ids = new ArrayList<>();

		if (objects != null) {
			for (Object object : objects) {
				if (object.getClass().equals(CompanyGroup.class)) {
					CompanyGroup group = (CompanyGroup) object;
					if (!Strings.isNullOrEmpty(group.getId())) {
						ids.add(group.getId());
					}
				}

				else if (object.getClass().equals(TestRun.class)) {
					TestRun testRun = (TestRun) object;
					if (!Strings.isNullOrEmpty(testRun.getId())) {
						ids.add(testRun.getId());
					}
				}

				else if (object.getClass().equals(CompanyLicensePrivate.class)) {
					CompanyLicensePrivate license = (CompanyLicensePrivate) object;
					if (!Strings.isNullOrEmpty(license.getDeviceId())) {
						ids.add(license.getDeviceId());
					}
				}

				else if (object.getClass().equals(SequenceRun.class)) {
					SequenceRun sequenceRun = (SequenceRun) object;
					if (!Strings.isNullOrEmpty(sequenceRun.getId())) {
						ids.add(sequenceRun.getId());
					}
				}
			}
		}

		return ids;
	}

	/**
	 * This method checks if the provided parameter should be part of the generated method url
	 *
	 * @param param
	 * @return
	 */
	private boolean isParamPathVariable(Parameter param) {
		if (param.getType().getSuperclass().equals(ValidatedInput.class)) {
			if (!param.getType().equals(ValidInputLocale.class)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method generates the method url string from the provided parameter type
	 *
	 * @param param
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	private String getRequestMethodTag(Parameter param) throws InstantiationException, IllegalAccessException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {

		if (param.getType().getSuperclass().equals(ValidatedInput.class)) {
			Class<?> clazz = param.getType();
			Object method_object = clazz.newInstance();
			Method method = param.getType().getDeclaredMethod("getTag");
			String tag = (String) method.invoke(method_object);
			return tag;
		}
		return "";
	}

	/**
	 * This method generates the complete method url from the provided parameters.
	 *
	 * @param params
	 * @param beanName
	 * @param m
	 * @return
	 */
	private StringBuilder createMethodUrl(String beanName, Method m) {
		StringBuilder sb = new StringBuilder();
		Parameter[] params = m.getParameters();
		if (params.length > 0) {
			for (Parameter param : params) {
				boolean isParamPathVariable = isParamPathVariable(param);
				if (isParamPathVariable) {
					log.debug("Paramter {} in {}.{} will be used for creating Request Method Header", param.getType(), beanName, m.getName());
					try {
						sb.append(getRequestMethodTag(param));
					} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException
							| InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return sb;
	}

	/**
	 * This Method returns the provided annotation value variable according to the Parameter Type.
	 *
	 * @param m
	 * @return
	 */
	private Object getValueFromAnnotation(Method m, ValidInputParamType parameterType) {
		ValidRequestMapping[] vrm = m.getAnnotationsByType(ValidRequestMapping.class);

		if (vrm.length > 0) {
			if (parameterType.equals(ValidInputParamType.METHOD)) {
				return vrm[0].method();
			} else if (parameterType.equals(ValidInputParamType.VALUE)) {
				return vrm[0].value();
			} else if (parameterType.equals(ValidInputParamType.CONSUMES)) {
				return vrm[0].consumes();
			} else if (parameterType.equals(ValidInputParamType.PRODUCES)) {
				return vrm[0].produces();
			}
		}
		return null;
	}

	/**
	 * This method returns the class url from the class RequestMethod annotation.
	 *
	 * @param clazz
	 * @return
	 */
	public String[] getClassUrlFromAnnotation(Class<?> clazz) {

		String rmList[] = { "" };
		Annotation[] rm = clazz.getAnnotationsByType(RequestMapping.class);

		for (Annotation anno : rm) {
			RequestMapping rmnew = (RequestMapping) anno;
			rmList = rmnew.value();
			return rmList;
		}
		return rmList;
	}

	/**
	 * This method returns the full method. It combines clazz and method urls
	 *
	 * @param clazz_url
	 * @param method_url
	 * @return
	 */
	private String generateUrl(String[] clazz_url, String annotated_value, StringBuilder method_url) {

		return String.join("", clazz_url) + annotated_value + method_url.toString();
	}

	/**
	 * This function generates the RequestMappingInfo from the provided parameters for each annotated method
	 *
	 * @param beanName
	 * @param m
	 * @param clazz_url
	 * @return
	 */
	public RequestMappingInfo createRequestMappingInfo(String beanName, Method m, String[] clazz_url) {
		StringBuilder method_url = createMethodUrl(beanName, m);
		RequestMethod rm = (RequestMethod) getValueFromAnnotation(m, ValidInputParamType.METHOD);
		String annotated_value = (String) getValueFromAnnotation(m, ValidInputParamType.VALUE);
		String[] consumes_value = (String[]) getValueFromAnnotation(m, ValidInputParamType.CONSUMES);
		String[] produces_value = (String[]) getValueFromAnnotation(m, ValidInputParamType.PRODUCES);
		String complete_url = generateUrl(clazz_url, annotated_value, method_url);
		log.info("New mapping added ({}): {}", rm, complete_url);
		return RequestMappingInfo.paths(complete_url).methods(rm).consumes(consumes_value).produces(produces_value).build();
	}
}
