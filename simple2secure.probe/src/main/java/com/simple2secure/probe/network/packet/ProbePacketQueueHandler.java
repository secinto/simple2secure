/**
 *********************************************************************
 *   simple2secure is a cyber risk and information security platform.
 *   Copyright (C) 2019  by secinto GmbH <https://secinto.com>
 *********************************************************************
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *********************************************************************
 */
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
		try {
			this.probePacket = probePacket;
			packetToQueue = PcapUtil.convertHexStreamToPacket(probePacket.getPacketAsHexStream(), 0);
			this.processingQueue = processingQueue;

		} catch (IllegalRawDataException e) {
			log.error("Could not convert hex stream to packet.", e);
		}
	}

	@Override
	public void run() {
		processingQueue.push(packetToQueue);
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

	// private static Logger log = LoggerFactory.getLogger(ProbePacketQueueHandler.class);
	// private ProcessingQueue<ProbePacket> probePacketQueue;
	// private Timer timer = new Timer(true);
	// private boolean isRunning = false;
	//
	// public ProbePacketQueueHandler() {
	// }
	//
	// @Override
	// public void run() {
	// isRunning = true;
	// while (isRunning) {
	// if (ProbePacketRequestHandler.dbHasChanged) {
	// probePacketQueue = new ProcessingQueue<>();
	// for (ProbePacket probeP : PacketUtil.getAllProbePacketsFromDB()) {
	// probePacketQueue.push(probeP);
	// }
	// ProbePacketRequestHandler.dbHasChanged = false;
	// }
	//
	// if (probePacketQueue != null && probePacketQueue.hasElement()) {
	// ProbePacket probePFromQ = probePacketQueue.pop();
	//
	// Packet packet;
	// try {
	// packet = PcapUtil.convertHexStreamToPacket(probePFromQ.getPacketAsHexStream(), 0);
	// TimerTask packetHandlerTask = new PacketHandler(packet);
	// timer.scheduleAtFixedRate(packetHandlerTask, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(30));
	// // timer.schedule(packetHandlerTask, TimeUnit.SECONDS.toMillis(1));
	// } catch (IllegalRawDataException e) {
	// log.error("Could not convert hex stream to a packet.", e);
	// isRunning = false;
	// }
	// }
	// }
	// }
}
