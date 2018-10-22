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

			Class groovy = classLoader.parseClass(groovyProcessor.getGroovyProcessor());

			Constructor<?> constructor = groovy.getConstructor(String.class, Map.class);

			Map<String, String> options = new HashMap<String, String>();
			PacketProcessor processor = (PacketProcessor) constructor.newInstance(groovy.getCanonicalName(), options);
			if (processor != null) {
				innerGroovyProcessor = processor;
			}
		} catch (Exception e) {
			log.error("Error occured during the instantiation of groovy script: " + e.getMessage());
		}

	}

}
