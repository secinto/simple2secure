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
import java.util.Arrays;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.simple2secure.api.model.CompanyGroup;
import com.simple2secure.api.model.CompanyLicensePrivate;
import com.simple2secure.api.model.Device;
import com.simple2secure.api.model.OsQueryCategory;
import com.simple2secure.api.model.Processor;
import com.simple2secure.api.model.SequenceRun;
import com.simple2secure.api.model.TestRun;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.portal.controller.WidgetController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import simple2secure.validator.annotation.ValidRequestMapping;
import simple2secure.validator.annotation.WidgetFunction;
import simple2secure.validator.model.ValidInputContext;
import simple2secure.validator.model.ValidInputLocale;
import simple2secure.validator.model.ValidInputParamType;
import simple2secure.validator.model.ValidInputUser;
import simple2secure.validator.model.ValidatedInput;

@Component
public class PortalUtils {
	private static Logger log = LoggerFactory.getLogger(PortalUtils.class);

	@Autowired
	JavaMailSender javaMailSender;

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

				else if (object.getClass().equals(Device.class)) {
					Device device = (Device) object;
					if (!Strings.isNullOrEmpty(device.getDeviceId())) {
						ids.add(device.getDeviceId());
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
		Class<?> clazz = param.getType();
		if (clazz != null) {
			Class<?> super_clazz = clazz.getSuperclass();
			if (super_clazz != null) {
				if (super_clazz.equals(ValidatedInput.class)) {
					if (!clazz.equals(ValidInputLocale.class) && !clazz.equals(ValidInputContext.class) && !clazz.equals(ValidInputUser.class)) {
						return true;
					}
				}
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

		Class<?> clazz = param.getType();
		if (clazz != null) {
			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null) {
				if (superClazz.equals(ValidatedInput.class)) {
					if (!clazz.equals(ValidInputLocale.class)) {
						Object method_object = clazz.newInstance();
						Method method = param.getType().getDeclaredMethod("getTag");
						String tag = (String) method.invoke(method_object);
						return tag;
					}
				}
			}
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
	private StringBuilder createMethodUrl(Method m) {
		StringBuilder sb = new StringBuilder();
		Parameter[] params = m.getParameters();
		if (params.length > 0) {
			for (Parameter param : params) {
				boolean isParamPathVariable = isParamPathVariable(param);
				if (isParamPathVariable) {
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
				switch (vrm[0].method()) {
				case GET:
					return RequestMethod.GET;
				case POST:
					return RequestMethod.POST;
				case PUT:
					return RequestMethod.PUT;
				case DELETE:
					return RequestMethod.DELETE;
				case HEAD:
					return RequestMethod.HEAD;
				case OPTIONS:
					return RequestMethod.OPTIONS;
				case PATCH:
					return RequestMethod.PATCH;
				case TRACE:
					return RequestMethod.TRACE;
				}
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
		StringBuilder method_url = createMethodUrl(m);
		RequestMethod rm = (RequestMethod) getValueFromAnnotation(m, ValidInputParamType.METHOD);
		String annotated_value = (String) getValueFromAnnotation(m, ValidInputParamType.VALUE);
		String[] consumes_value = (String[]) getValueFromAnnotation(m, ValidInputParamType.CONSUMES);
		String[] produces_value = (String[]) getValueFromAnnotation(m, ValidInputParamType.PRODUCES);
		String complete_url = generateUrl(clazz_url, annotated_value, method_url);
		log.info("New mapping added ({}): {}", rm, complete_url);
		return RequestMappingInfo.paths(complete_url).methods(rm).consumes(consumes_value).produces(produces_value).build();
	}

	/**
	 * This function creates OsQueryCategory object from the JsonNode
	 *
	 * @param node
	 * @return
	 */
	public OsQueryCategory generateQueryCategoryObjectFromJson(JsonNode node) {
		String name = node.get("name").asText();
		String description = node.get("description").asText();
		int systemsAvailable = node.get("systemsAvailable").asInt();
		return new OsQueryCategory(name, description, systemsAvailable);
	}

	/**
	 * This function returns the list of the widget apis which are tagged with the @WidgetFunction annotation.
	 *
	 * @return
	 */
	public List<String> getWidgetApis() {
		List<String> apis = new ArrayList<>();
		final List<Method> allMethods = new ArrayList<>(Arrays.asList(WidgetController.class.getDeclaredMethods()));

		for (final Method method : allMethods) {
			if (method.isAnnotationPresent(WidgetFunction.class)) {
				StringBuilder method_url = createMethodUrl(method);
				String annotated_value = (String) getValueFromAnnotation(method, ValidInputParamType.VALUE);
				String[] clazz_url = { StaticConfigItems.WIDGET_API.replace("/api/", "") };
				String complete_url = generateUrl(clazz_url, annotated_value, method_url);
				apis.add(complete_url);
			}
		}

		return apis;
	}

}
