package com.simple2secure.probe.groovy

import org.slf4j.Logger

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
		log.debug("Processing packet in groovy with id {}", test);
		return this.packet;
	}

	@Override
	public void performAnalysis() {
		log.debug("Groovy script perform start analysis {} for packet with timestamp {}", this.packet.getTimestamp());
	}
}
