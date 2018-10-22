package com.simple2secure.probe.groovy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.simple2secure.probe.network.PacketContainer
import com.simple2secure.probe.network.PacketProcessor

class SimpleGroovyPacketProcessor extends PacketProcessor {

	private static Logger log = LoggerFactory.getLogger(SimpleGroovyPacketProcessor.class);

	public SimpleGroovyPacketProcessor(String name, Map<String, String> options) {
		super(name, options);
	}

	@Override
	public PacketContainer processPacket() {
		String test = this.packet.getId();
		println("Obtained data from Java");
		println("Obtained packet with test id " + test);
		log.debug("Print debug message from groovy script " + name);
		return this.packet;
	}

	@Override
	public void performAnalysis() {
		log.debug("Groovy script perform analysis {}", this.packet.getTimestamp());
	}
}
