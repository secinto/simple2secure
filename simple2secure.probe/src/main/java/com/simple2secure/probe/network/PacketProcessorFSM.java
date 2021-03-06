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
package com.simple2secure.probe.network;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.Step;
import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.utils.ProbeUtils;

public class PacketProcessorFSM implements Runnable {

	private static Logger log = LoggerFactory.getLogger(PacketProcessorFSM.class);

	private ProcessingQueue<PacketContainer> processingQueue;

	private boolean running = false;

	private static List<PacketProcessor> processors;
	private PacketContainer lastPacket = null;

	public PacketProcessorFSM(ProcessingQueue<PacketContainer> processingQueue) {
		this.processingQueue = processingQueue;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				lastPacket = processingQueue.pop();
				PacketProcessor pp = getProcessorForPacket(lastPacket);
				if (pp != null) {
					lastPacket = pp.process();
				}
				if (lastPacket == null) {
					log.debug("Processing for this packet has been finished. Processor returned NULL packet!");
					continue;
				}
				if (lastPacket.isPacketInProcess()) {
					processingQueue.push(lastPacket);
					log.trace("Processing of packet with ID {} not finished. Pushing it onto the queue.", lastPacket.getId());
				} else {
					log.trace("Processing for this packet with ID {} has finished all steps.", lastPacket.getId());
				}
			} catch (Exception e) {
				if (lastPacket != null) {
					log.error("PacketProcessorFSM got exception for packet {} due to an exception {}", lastPacket, e.getStackTrace());
				} else {
					log.error("PacketProcessorFSM got exception for packet due to an exception {}", e.getMessage());
				}
			}
		}
	}

	public PacketProcessor getProcessorForPacket(PacketContainer packet) {
		/*
		 * TODO: Refactor this in order to not run this for every packet but only if the configuration changes.
		 */
		updateProcessorsForPacket();

		int next = packet.getNext();
		packet.setNext(next + 1);
		if (next >= processors.size()) {
			packet.setPacketInProcess(false);
			return null;
		} else {
			packet.setPacketInProcess(true);
			PacketProcessor processor = processors.get(next);

			packet.setProcessor(
					ProbeUtils.getProcessorFromListByName(processor.getName(), ProbeConfiguration.getInstance().getCurrentProcessors()));

			processor.initialize(packet);
			return processor;
		}
	}

	private void updateProcessorsForPacket() {
		processors = new ArrayList<>();
		for (Step stp : ProbeConfiguration.getInstance().getCurrentSteps().values()) {
			PacketProcessor processor = ProbeConfiguration.getInstance().getCurrentPacketProcessors().get(stp.getName());
			if (processor != null) {
				processors.add(processor);
			}
		}

	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

}
