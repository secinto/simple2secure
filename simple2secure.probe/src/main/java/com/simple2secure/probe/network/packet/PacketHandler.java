package com.simple2secure.probe.network.packet;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketHandler {
	private static Logger log = LoggerFactory.getLogger(PacketHandler.class);

	public PacketHandler() {
	}

	public void sendPackets(PcapHandle handler, Packet packet) {
		try {
			handler.sendPacket(packet);
		} catch (PcapNativeException e) {
			log.error("An error occured while sending packet.", e);
		} catch (NotOpenException e) {
			log.error("An error occured while sending packet.", e);
		} finally {
			if (handler != null && handler.isOpen()) {
				handler.close();
			}
		}
	}

}
