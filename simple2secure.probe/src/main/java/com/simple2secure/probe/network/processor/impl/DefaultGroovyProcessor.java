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
package com.simple2secure.probe.network.processor.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.Processor;
import com.simple2secure.probe.network.PacketContainer;
import com.simple2secure.probe.network.PacketProcessor;

import groovy.lang.GroovyClassLoader;

public class DefaultGroovyProcessor extends PacketProcessor {

	private static Logger log = LoggerFactory.getLogger(DefaultGroovyProcessor.class);

	private final GroovyClassLoader classLoader;

	private PacketProcessor innerGroovyProcessor;

	public DefaultGroovyProcessor(String name, Map<String, String> options) {
		super(name, options);
		classLoader = new GroovyClassLoader();
	}

	@Override
	public PacketContainer processPacket() {
		// TODO Auto-generated method stub
		if (innerGroovyProcessor == null) {
			initialize();
		}
		if (innerGroovyProcessor != null) {
			innerGroovyProcessor.initialize(packet);
			innerGroovyProcessor.process();
		}
		return packet;
	}

	@Override
	public void performAnalysis() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {

		try {
			Processor groovyProcessor = packet.getProcessor();
			if (groovyProcessor != null) {
				Class groovy = classLoader.parseClass(groovyProcessor.getGroovyProcessor());

				Constructor<?> constructor = groovy.getConstructor(String.class, Map.class);

				Map<String, String> options = new HashMap<>();
				PacketProcessor processor = (PacketProcessor) constructor.newInstance(groovy.getCanonicalName(), options);
				if (processor != null) {
					innerGroovyProcessor = processor;
				}
			}
		} catch (Exception e) {
			log.error("Error occured during the instantiation of groovy script: " + e.getMessage());
		}

	}

}
