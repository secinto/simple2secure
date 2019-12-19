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

package com.simple2secure.portal;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.simple2secure.portal.utils.DataInitialization;
import com.simple2secure.portal.utils.PortalUtils;

import simple2secure.validator.annotation.ValidRequestMapping;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger log = LoggerFactory.getLogger(StartupApplicationListener.class);

	@Autowired
	private DataInitialization dataInitializer;

	@Autowired
	private ApplicationContext context;

	@Autowired
	public PortalUtils portalUtils;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		initializeData();
		try {
			initializeMethodHeaders();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This function checks if the default data is initialized, and initializes it by each app start
	 */
	private void initializeData() {
		if (dataInitializer != null) {
			log.info("------------------ DATA INITIALIZATION ---------------------");
			try {
				dataInitializer.addDefaultQueries();
				dataInitializer.addDefaultProcessors();
				dataInitializer.addDefaultSteps();
				dataInitializer.addDefaultSettings();
				dataInitializer.addDefaultLicensePlan();
				dataInitializer.addDefaultUsers();
				log.info("------------------ DATA INITIALIZATION SUCCESS ---------------------");
			} catch (IOException e) {
				log.error(e.getMessage());
				log.info("------------------ DATA INITIALIZATION ERROR ---------------------");
			}
		} else {
			log.info("------------------ NO DATA INITIALIZATION ---------------------");
		}
	}

	/**
	 * This function initializes the method headers and maps each header to the provided function. Only headers with the
	 * ValidatedRequestMapping annotation will be registered.
	 *
	 * @throws Exception
	 */
	private void initializeMethodHeaders() throws Exception {
		List<RequestMappingInfo> requestMappingList = new ArrayList<>();
		for (String beanName : context.getBeanNamesForAnnotation(RestController.class)) {
			Object bean = context.getBean(beanName);
			Class<?> clazz = AopUtils.getTargetClass(bean);
			String[] clazz_url = portalUtils.getClassUrlFromAnnotation(clazz);
			Method[] methods = clazz.getDeclaredMethods();

			for (Method m : methods) {
				if (m.isAnnotationPresent(ValidRequestMapping.class)) {
					// log.debug("Annotated method found: {}.{}", beanName, m.getName());

					RequestMappingInfo requestMappingInfo = portalUtils.createRequestMappingInfo(beanName, m, clazz_url);
					if (!requestMappingList.contains(requestMappingInfo)) {
						requestMappingList.add(requestMappingInfo);
						requestMappingHandlerMapping.registerMapping(requestMappingInfo, bean, m);
					} else {
						log.error("This request mapping {} has been already registered", requestMappingInfo);
						throw new RuntimeException("This request mapping has been already registered");
					}
				}
			}
		}
	}
}
