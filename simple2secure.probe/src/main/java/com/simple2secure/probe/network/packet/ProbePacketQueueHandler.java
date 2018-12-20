package com.simple2secure.probe.network.packet;

import java.util.TimerTask;

import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.utils.PcapUtil;

public class ProbePacketQueueHandler extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(ProbePacketQueueHandler.class);

	private Packet packetToQueue;
	private ProcessingQueue<Packet> processingQueue;
	private ProbePacket probePacket;

	public ProbePacketQueueHandler(ProbePacket probePacket, ProcessingQueue<Packet> processingQueue) {
		this.probePacket = probePacket;
		this.processingQueue = processingQueue;
	}

	/**
	 * This Class converts the network packet within the ProbePacket and push the network packet to the provided queue.
	 */
	@Override
	public void run() {
		try {
			packetToQueue = PcapUtil.convertHexStreamToPacket(probePacket.getPacketAsHexStream(), 0);
			processingQueue.push(packetToQueue);
		} catch (IllegalRawDataException e) {
			log.error("Could not create network packet from hex stream.", e);
		}
	}

	public Packet getPacketToQueue() {
		return packetToQueue;
	}

	public void setPacketToQueue(Packet packetToQueue) {
		this.packetToQueue = packetToQueue;
	}

	public ProcessingQueue<Packet> getProcessingQueue() {
		return processingQueue;
	}

	public void setProcessingQueue(ProcessingQueue<Packet> processingQueue) {
		this.processingQueue = processingQueue;
	}

	public ProbePacket getProbePacket() {
		return probePacket;
	}

	public void setProbePacket(ProbePacket probePacket) {
		this.probePacket = probePacket;
	}
}
