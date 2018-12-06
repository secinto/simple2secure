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

	@Test
	public void f

}
