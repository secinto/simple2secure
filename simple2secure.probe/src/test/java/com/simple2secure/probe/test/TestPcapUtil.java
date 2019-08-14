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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

import com.simple2secure.api.model.PacketInfo;
import com.simple2secure.probe.utils.PcapUtil;

public class TestPcapUtil {

	@Test
	public void getNetIfByInetAddr_validIpAsParam_PcapNetworkInterface() throws PcapNativeException, UnknownHostException, SocketException {

		List<PcapNetworkInterface> networkInterfaceList = Pcaps.findAllDevs();
		PcapNetworkInterface networkInterface = PcapUtil.getNetworkInterfaceByInetAddr(PcapUtil.getIpAddrOfNetworkInterface());
		boolean containsNetworkInterface = networkInterfaceList.contains(networkInterface);

		Assertions.assertTrue(containsNetworkInterface);
	}

	@Test
	public void extractPacketInformation() {
		String strSrcIpAddress = "192.0.2.100";
		String strDstIpAddress = "192.168.123.1";
		MacAddress SRC_MAC_ADDR = MacAddress.getByName("fe:00:01:02:03:04");

		ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
		try {
			arpBuilder.hardwareType(ArpHardwareType.ETHERNET).protocolType(EtherType.IPV4).hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
					.protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES).operation(ArpOperation.REQUEST).srcHardwareAddr(SRC_MAC_ADDR)
					.srcProtocolAddr(InetAddress.getByName(strSrcIpAddress)).dstHardwareAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
					.dstProtocolAddr(InetAddress.getByName(strDstIpAddress));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}

		EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
		etherBuilder.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS).srcAddr(SRC_MAC_ADDR).type(EtherType.ARP).payloadBuilder(arpBuilder)
				.paddingAtBuild(true);

		PacketInfo packetInfo = PcapUtil.extractPacketInformation(etherBuilder.build());

		Assertions.assertEquals("/" + strDstIpAddress, packetInfo.getDestination_ip());
		Assertions.assertEquals("/" + strSrcIpAddress, packetInfo.getSource_ip());
		Assertions.assertEquals("arp.ARP", packetInfo.getProtocol());
		Assertions.assertEquals(MacAddress.ETHER_BROADCAST_ADDRESS.toString(), packetInfo.getDestination_mac());
		Assertions.assertEquals(SRC_MAC_ADDR.toString(), packetInfo.getSource_mac());
		Assertions.assertEquals(60, packetInfo.getLength());
	}
}
