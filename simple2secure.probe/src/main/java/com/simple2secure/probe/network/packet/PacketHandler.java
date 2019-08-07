package com.simple2secure.probe.network.packet;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.utils.PcapUtil;

public class PacketHandler implements Runnable {
	private static Logger log = LoggerFactory.getLogger(PacketHandler.class);

	private PcapHandle handler;
	private Packet packet;
	private ProcessingQueue<Packet> processingQueue;

	public PacketHandler(ProcessingQueue<Packet> processingQueue) {
		this.processingQueue = processingQueue;
		handler = PcapUtil.getPcapHandle();
	}

	@Override
	public void run() {
		while (true) {
			packet = processingQueue.pop();
			if (packet != null && handler != null) {
				sendPackets(handler, packet);
			}
		}
	}

	public void sendPackets(PcapHandle handler, Packet packet) {
		try {
			handler.sendPacket(packet);
			log.info("The packet has been sent.");
		} catch (PcapNativeException e) {
			log.error("An error occured while sending packet.", e);
		} catch (NotOpenException e) {
			log.error("An error occured while sending packet.", e);
		}
	}

}
