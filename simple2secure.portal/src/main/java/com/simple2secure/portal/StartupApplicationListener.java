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
				dataInitializer.addDefaultConfiguration();
				dataInitializer.addDefaultSettings();
				dataInitializer.addDefaultLicensePlan();
				dataInitializer.addDefaultUsers();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		} else {
			log.info("------------------ NO DATA INITIALIZATION ---------------------");
		}

	}

}
