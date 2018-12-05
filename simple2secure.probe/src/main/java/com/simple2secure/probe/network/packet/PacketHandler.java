package com.simple2secure.probe.network.packet;

import java.util.ArrayList;
import java.util.List;

import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
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
		}
	}

	public List<Packet> receivePackets(String filterString, PcapHandle handler) {
		List<Packet> packetList = new ArrayList<>();
		try {
			try {
				handler.setFilter(filterString, BpfCompileMode.OPTIMIZE);
			} catch (PcapNativeException e) {
				log.error("An error occured while setting the BPF filter to the listener.", e);
			} catch (NotOpenException e) {
				log.error("An error occured while setting the BPF filter to the listener.", e);
			}

			PacketListener listener = new PacketListener() {
				@Override
				public void gotPacket(Packet packet) {
					packetList.add(packet);
				}
			};
		} finally {
			if (handler != null && handler.isOpen()) {
				handler.close();
			}
		}
		return packetList;
	}
}
