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

import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;

import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.probe.exceptions.NetworkException;
import com.simple2secure.probe.utils.LocaleHolder;
import com.simple2secure.probe.utils.PcapUtil;

public class NetworkMonitor {

	private static NetworkMonitor instance;

	private PcapNetworkInterface singleInterface;
	private PacketReceiver receiver;
	private PacketProcessorFSM packetProcessor;

	private ProcessingQueue<PacketContainer> processingQueue;
	private PcapHandle receiverHandle;
	// private PcapHandle senderHandle;

	public static NetworkMonitor startMonitor() {
		if (instance == null) {
			instance = new NetworkMonitor();
		}
		return instance;
	}

	public void stopMonitor() {
		receiver.stop();
		packetProcessor.stop();
	}

	private NetworkMonitor() {
		initMonitor();
	}

	private void initMonitor() {
		singleInterface = PcapUtil.getOutgoingInterface(true, false);

		if (singleInterface == null) {
			throw new NetworkException(LocaleHolder.getMessage("no_usable_address"));
		}

		try {
			receiverHandle = singleInterface.openLive(StaticConfigItems.SNAPLEN, PromiscuousMode.PROMISCUOUS, StaticConfigItems.READ_TIMEOUT);

			/*
			 * TODO: Verify if this setting works and is correctly applied. A verification for inconsistent or incorrect BPF filter strings must
			 * be developed
			 */
			receiverHandle.setFilter("not ((host 127.0.0.1 and port (8443 or 9000)) or (host 127.0.0.1 and port (51001 or 51003)))",
					BpfCompileMode.OPTIMIZE);

			processingQueue = new ProcessingQueue<>();

			receiver = new PacketReceiver(receiverHandle, processingQueue);
			packetProcessor = new PacketProcessorFSM(processingQueue);

			new Thread(packetProcessor).start();
			new Thread(receiver).start();

		} catch (Exception e) {
			if (packetProcessor != null) {
				packetProcessor.stop();
			}
			if (receiver != null) {
				receiver.stop();
			}
			throw new NetworkException(LocaleHolder.getMessage("pcap_interface_open_error"));
		}

	}

	public PacketReceiver getReceiver() {
		return receiver;
	}

	public PcapHandle getReceiverHandle() {
		return receiverHandle;
	}

	public boolean isRunning() {
		if (packetProcessor != null && receiver != null) {
			if (packetProcessor.isRunning() && receiver.isRunning()) {
				return true;
			}
		}

		return false;
	}

}
