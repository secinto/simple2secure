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
package com.simple2secure.probe.test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapIpV4Address;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.network.IPv4;
import com.simple2secure.commons.network.NetUtils;
import com.simple2secure.probe.exceptions.ProbeException;
import com.simple2secure.probe.utils.LocaleHolder;
import com.simple2secure.probe.utils.PcapUtil;

public class TestSendArpRequest {

	private static Logger log = LoggerFactory.getLogger(TestSendArpRequest.class);

	private static final String COUNT_KEY = TestSendArpRequest.class.getName() + ".count";
	private static final int COUNT = Integer.getInteger(COUNT_KEY, 1);

	private static final String READ_TIMEOUT_KEY = TestSendArpRequest.class.getName() + ".readTimeout";
	private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

	private static final String SNAPLEN_KEY = TestSendArpRequest.class.getName() + ".snaplen";
	private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]

	private static final MacAddress SRC_MAC_ADDR = MacAddress.getByName("fe:00:01:02:03:04");

	private static MacAddress resolvedAddr;
	private static String localAddress = "";
	private static String localGateway = "";

	@Test
	public void testSendArpRequest() throws PcapNativeException, NotOpenException {
		System.out.println(COUNT_KEY + ": " + COUNT);
		System.out.println(READ_TIMEOUT_KEY + ": " + READ_TIMEOUT);
		System.out.println(SNAPLEN_KEY + ": " + SNAPLEN);
		System.out.println("\n");

		PcapNetworkInterface nif = getSingleInterface();

		log.debug(nif.getName() + "(" + nif.getDescription() + ")");

		PcapHandle handle = nif.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
		PcapHandle sendHandle = nif.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
		ExecutorService pool = Executors.newSingleThreadExecutor();

		try {
			handle.setFilter(
					"arp and src host " + localGateway + " and dst host " + localAddress + " and ether dst " + Pcaps.toBpfString(SRC_MAC_ADDR),
					BpfCompileMode.OPTIMIZE);

			PacketListener listener = new PacketListener() {
				@Override
				public void gotPacket(Packet packet) {
					if (packet.contains(ArpPacket.class)) {
						ArpPacket arp = packet.get(ArpPacket.class);
						if (arp.getHeader().getOperation().equals(ArpOperation.REPLY)) {
							TestSendArpRequest.resolvedAddr = arp.getHeader().getSrcHardwareAddr();
						}
					}
					System.out.println(packet);
				}
			};

			Task t = new Task(handle, listener);
			pool.execute(t);

			ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
			try {
				arpBuilder.hardwareType(ArpHardwareType.ETHERNET).protocolType(EtherType.IPV4).hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
						.protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES).operation(ArpOperation.REQUEST).srcHardwareAddr(SRC_MAC_ADDR)
						.srcProtocolAddr(InetAddress.getByName(localAddress)).dstHardwareAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
						.dstProtocolAddr(InetAddress.getByName(localGateway));
			} catch (UnknownHostException e) {
				throw new IllegalArgumentException(e);
			}

			EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
			etherBuilder.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS).srcAddr(SRC_MAC_ADDR).type(EtherType.ARP).payloadBuilder(arpBuilder)
					.paddingAtBuild(true);

			for (int i = 0; i < COUNT; i++) {
				Packet p = etherBuilder.build();
				System.out.println(p);
				sendHandle.sendPacket(p);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
			}
		} finally {
			if (handle != null && handle.isOpen()) {
				handle.close();
			}
			if (sendHandle != null && sendHandle.isOpen()) {
				sendHandle.close();
			}
			if (pool != null && !pool.isShutdown()) {
				pool.shutdown();
			}

			System.out.println(localGateway + " was resolved to " + resolvedAddr);
		}
	}

	private PcapNetworkInterface getSingleInterface() {

		List<PcapNetworkInterface> interfaces;
		PcapNetworkInterface singleInterface = null;
		String ipAddress = "";
		String netmask = "";
		try {
			interfaces = Pcaps.findAllDevs();
		} catch (PcapNativeException e1) {
			throw new ProbeException(LocaleHolder.getMessage("pcap_no_interfaces"));
		}

		if (interfaces == null || interfaces.isEmpty()) {
			throw new ProbeException(LocaleHolder.getMessage("pcap_no_interfaces"));
		}

		for (PcapNetworkInterface currentInterface : interfaces) {
			/*
			 * Iterate through the addresses of the interfaces and check if someone fits.
			 *
			 * TODO: We should store the interfaces which have relevant addresses.
			 */
			List<PcapAddress> addresses = currentInterface.getAddresses();
			for (PcapAddress address : addresses) {
				if (address instanceof PcapIpV4Address) {
					ipAddress = ((PcapIpV4Address) address).getAddress().getHostAddress();
					if (NetUtils.isUseableIPv4Address(ipAddress) && PcapUtil.checkAddress(ipAddress)) {
						if (singleInterface != null) {
							log.info("Found another usable address {}, discarding old one {}", ipAddress, localAddress);
						}
						singleInterface = currentInterface;
						localAddress = ipAddress;
						netmask = ((PcapIpV4Address) address).getNetmask().getHostAddress();
					}
				}
			}
		}

		IPv4 findGateway = new IPv4(localAddress, netmask);
		localGateway = findGateway.getFirstHost();

		return singleInterface;
	}

	private static class Task implements Runnable {

		private PcapHandle handle;
		private PacketListener listener;

		public Task(PcapHandle handle, PacketListener listener) {
			this.handle = handle;
			this.listener = listener;
		}

		@Override
		public void run() {
			try {
				handle.loop(COUNT, listener);
			} catch (PcapNativeException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NotOpenException e) {
				e.printStackTrace();
			}
		}

	}

}
