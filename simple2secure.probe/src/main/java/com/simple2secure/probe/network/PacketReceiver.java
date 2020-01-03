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

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.exceptions.NetworkException;
import com.simple2secure.probe.utils.LocaleHolder;

public class PacketReceiver implements PacketListener, Runnable {
	private static Logger log = LoggerFactory.getLogger(PacketReceiver.class);

	private PcapHandle handle;

	private boolean running = false;
	private ProcessingQueue<PacketContainer> processingQueue;

	public PacketReceiver(PcapHandle handle, ProcessingQueue<PacketContainer> processingQueue) {
		this.handle = handle;
		this.processingQueue = processingQueue;
	}

	@Override
	public void gotPacket(Packet packet) {
		try {
			processingQueue.push(new PacketContainer(packet, handle.getTimestamp().getTime()));
		} catch (NetworkException ne) {
			log.info("Received packet couldn't be parsed: " + ne.getMessage());
		}
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		try {
			running = true;
			handle.loop(-1, this);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(LocaleHolder.getMessage("interruption_occured").getMessage());
		} catch (PcapNativeException e) {
			log.error(LocaleHolder.getMessage("unspecified_pcap_native_error").getMessage());
		} catch (NotOpenException e) {
			log.error(LocaleHolder.getMessage("pcap_interface_open_error").getMessage());
		} finally {
			running = false;
		}
	}

	/**
	 * Stops the packet receiver and the underlying handle from receiving packets. First breaks the loop, thereafter calls close.
	 */
	public void stop() {
		try {
			if (running) {
				handle.breakLoop();
				handle.close();
			}
		} catch (NotOpenException e) {
			log.debug("PacketReceiver.close called although loop wasn't running!", e);
		}
	}

	public PcapHandle getHandle() {
		return handle;
	}

}
