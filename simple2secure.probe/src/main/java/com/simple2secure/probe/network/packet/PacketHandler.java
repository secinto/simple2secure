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

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.utils.PcapUtil;

public class PacketHandler extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(PacketHandler.class);

	private PcapHandle handler;
	private Packet packet;

	public PacketHandler(ProcessingQueue<Packet> processingQueue) {
		packet = processingQueue.pop();
		handler = PcapUtil.getPcapHandle();
		if (packet != null && handler != null) {
			sendPackets(handler, packet);
		}
	}

	@Override
	public void run() {
		if (packet != null && handler != null) {
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

}
