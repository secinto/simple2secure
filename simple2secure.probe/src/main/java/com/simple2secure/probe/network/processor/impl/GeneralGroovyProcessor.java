package com.simple2secure.probe.network.processor.impl;

import java.util.Map;

import com.simple2secure.probe.network.PacketContainer;
import com.simple2secure.probe.network.PacketProcessor;

import groovy.lang.GroovyClassLoader;

public class GeneralGroovyProcessor extends PacketProcessor {

	@SuppressWarnings("rawtypes")
	private Class scriptClass;

	@SuppressWarnings("resource")
	public GeneralGroovyProcessor(String name, Map<String, String> options) {
		super(name, options);
		scriptClass = new GroovyClassLoader().parseClass("");

	}

	@SuppressWarnings("unchecked")
	@Override
	public PacketContainer processPacket() {
		// TODO Auto-generated method stub

		try {
			Object scriptInstance = scriptClass.newInstance();
			scriptClass.getDeclaredMethod("packetProcessor", new Class[] {}).invoke(scriptInstance, new Object[] {});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void performAnalysis() {
		// TODO Auto-generated method stub

	}
}
