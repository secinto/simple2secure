package com.simple2secure.probe.groovy

import com.simple2secure.probe.network.PacketContainer
import com.simple2secure.probe.network.PacketProcessor

class GroovyPacketProcessor extends PacketProcessor {

	@Override
	public PacketContainer processPacket() {
		// TODO Auto-generated method stub
		println "test";
		println this.packet.id;
		return null;
	}

	@Override
	public void performAnalysis() {
		// TODO Auto-generated method stub
		
	}

}
