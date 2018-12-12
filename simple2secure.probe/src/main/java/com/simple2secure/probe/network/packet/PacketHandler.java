package com.simple2secure.probe.network.packet;

import java.util.TimerTask;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.utils.PcapUtil;

public class PacketHandler extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(PacketHandler.class);

	private PcapHandle handler;
	private Packet packet;

	public PacketHandler(Packet packet) {
		this.packet = packet;
		handler = PcapUtil.getPcapHandle();

		if (this.packet != null && handler != null) {
			sendPackets(handler, packet);
		}

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

	@Override
	public void run() {
		// sendPackets(PcapUtil.getPcapHandle(), packet);
	}

}
