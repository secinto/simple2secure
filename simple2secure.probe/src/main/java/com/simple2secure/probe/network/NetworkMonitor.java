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

import java.util.List;

import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapIpV4Address;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.network.NetUtils;
import com.simple2secure.probe.exceptions.NetworkException;
import com.simple2secure.probe.exceptions.ProbeException;
import com.simple2secure.probe.utils.LocaleHolder;
import com.simple2secure.probe.utils.PcapUtil;

public class NetworkMonitor {

	private static Logger log = LoggerFactory.getLogger(NetworkMonitor.class);

	private static NetworkMonitor instance;

	private List<PcapNetworkInterface> interfaces;
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

	private NetworkMonitor() {
		initMonitor();
	}

	private void initMonitor() {
		try {
			interfaces = Pcaps.findAllDevs();
		} catch (PcapNativeException e1) {
			throw new ProbeException(LocaleHolder.getMessage("pcap_no_interfaces"));
		}

		if (interfaces == null || interfaces.isEmpty()) {
			throw new ProbeException(LocaleHolder.getMessage("pcap_no_interfaces"));
		}

		getSingleInterface(false);

		if (singleInterface == null) {
			throw new NetworkException(LocaleHolder.getMessage("no_usable_address"));
		}

		try {
			receiverHandle = singleInterface.openLive(StaticConfigItems.SNAPLEN, PromiscuousMode.PROMISCUOUS, StaticConfigItems.READ_TIMEOUT);

			/*
			 * TODO: Verify if this setting works and is correctly applied. A verification for inconsistent or incorrect BPF filter strings must
			 * be developed
			 */
			receiverHandle.setFilter("not (host 127.0.0.1 and port (8080 or 8443 or 9000))", BpfCompileMode.OPTIMIZE);

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

	private void getSingleInterface(boolean show) {
		String previousAddress = "";

		for (PcapNetworkInterface currentInterface : interfaces) {
			if (show) {
				log.info(currentInterface.getName() + "(" + currentInterface.getDescription() + ")");
			}
			/*
			 * Iterate through the addresses of the interfaces and check if someone fits.
			 *
			 * TODO: We should store the interfaces which have relevant addresses.
			 */
			List<PcapAddress> addresses = currentInterface.getAddresses();
			for (PcapAddress address : addresses) {
				if (address instanceof PcapIpV4Address) {
					String ipAddress = ((PcapIpV4Address) address).getAddress().getHostAddress();
					if (NetUtils.isUseableIPv4Address(ipAddress) && PcapUtil.checkAddress(ipAddress)) {
						if (singleInterface != null) {
							log.info("Found another usable address {}, discarding old one {}", ipAddress, previousAddress);
						}
						singleInterface = currentInterface;
						previousAddress = ipAddress;
					}
				}
			}
		}
	}

	public PacketReceiver getReceiver() {
		return receiver;
	}

	public PcapHandle getReceiverHandle() {
		return receiverHandle;
	}

}
