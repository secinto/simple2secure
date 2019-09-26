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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.simple2secure.portal.utils.DataInitialization;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger log = LoggerFactory.getLogger(StartupApplicationListener.class);

	@Autowired
	private DataInitialization dataInitializer;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (dataInitializer != null) {
			log.info("------------------ DATA INITIALIZATION ---------------------");
			try {
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

}
