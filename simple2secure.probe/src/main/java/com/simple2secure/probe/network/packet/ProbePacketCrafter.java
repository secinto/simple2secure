package com.simple2secure.probe.network.packet;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Rfc1349Tos;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.Packet.Builder;
import org.pcap4j.packet.SimpleBuilder;
import org.pcap4j.packet.UnknownPacket;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IcmpV4Code;
import org.pcap4j.packet.namednumber.IcmpV4Type;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.utils.PcapUtil;

public class ProbePacketCrafter {
	private String groupId;
	private String name;
	private boolean always;
	private int requestCount;
	private long analysisInterval;
	private static TimeUnit analysisIntervalUnit;
	private static String hexAsStringFrame;

	public static ProbePacket craftProbePacket(String type, String groupId, String name, boolean always, int requestCount,
			long analysisInterval) throws UnsupportedEncodingException {
		analysisIntervalUnit = analysisIntervalUnit.SECONDS;
		if (type.equals("arp")) {
			Packet result = craftArpPacket("192.168.123.111", "192.168.123.1", MacAddress.getByName("30:24:32:FC:89:38"));
			hexAsStringFrame = PcapUtil.convertPackRawDataToHexStreamString(result.getRawData());
		} else if (type.equals("ping")) {
			Packet result = craftIcmpEchoPacket(MacAddress.getByName("30:24:32:FC:89:38"));
			hexAsStringFrame = PcapUtil.convertPackRawDataToHexStreamString(result.getRawData());
		} else if (type.equals("icmpCommon")) {
			Packet result = craftIcmpCommonPacket(MacAddress.getByName("30:24:32:FC:89:38"), "test");
			hexAsStringFrame = PcapUtil.convertPackRawDataToHexStreamString(result.getRawData());
		}

		return new ProbePacket(groupId, name, always, requestCount, analysisInterval, analysisIntervalUnit, hexAsStringFrame);
	}

	private static Packet craftArpPacket(String srcIpAddress, String dstIpAddress, MacAddress srcMacAddress) {
		ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
		try {
			arpBuilder.hardwareType(ArpHardwareType.ETHERNET).protocolType(EtherType.IPV4).hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
					.protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES).operation(ArpOperation.REQUEST).srcHardwareAddr(srcMacAddress)
					.srcProtocolAddr(InetAddress.getByName(srcIpAddress)).dstHardwareAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
					.dstProtocolAddr(InetAddress.getByName(dstIpAddress));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}

		return createEthernetFrame(srcMacAddress, EtherType.ARP, arpBuilder);
	}

	private static Packet craftIcmpEchoPacket(MacAddress srcMacAddress) {
		short identifier = (short) 1234;
		short sequenceNumber = (short) 4321;
		UnknownPacket.Builder unknownb = new UnknownPacket.Builder();
		unknownb.rawData(new byte[] { (byte) 0, (byte) 1, (byte) 2, (byte) 3 });

		IcmpV4EchoPacket.Builder b = new IcmpV4EchoPacket.Builder();
		b.identifier(identifier).sequenceNumber(sequenceNumber).payloadBuilder(unknownb);
		IcmpV4EchoPacket packet = b.build();

		IcmpV4CommonPacket.Builder icmpV4b = new IcmpV4CommonPacket.Builder();
		icmpV4b.type(IcmpV4Type.ECHO).code(IcmpV4Code.NO_CODE).payloadBuilder(new SimpleBuilder(packet)).correctChecksumAtBuild(true);
		IpV4Packet.Builder ipv4b = new IpV4Packet.Builder();
		try {
			ipv4b.version(IpVersion.IPV4).tos(IpV4Rfc1349Tos.newInstance((byte) 0)).identification((short) 100).ttl((byte) 100)
					.protocol(IpNumber.ICMPV4)
					.srcAddr((Inet4Address) InetAddress.getByAddress(new byte[] { (byte) 192, (byte) 168, (byte) 123, (byte) 111 }))
					.dstAddr((Inet4Address) InetAddress.getByAddress(new byte[] { (byte) 192, (byte) 168, (byte) 123, (byte) 1 }))
					.payloadBuilder(icmpV4b).correctChecksumAtBuild(true).correctLengthAtBuild(true);
		} catch (UnknownHostException e) {
			throw new AssertionError("Never get here.");
		}
		return createEthernetFrame(srcMacAddress, EtherType.IPV4, ipv4b);
	}

	private static Packet craftIcmpCommonPacket(MacAddress srcMacAddress, String packetPayload) {
		UnknownPacket.Builder unknownb = new UnknownPacket.Builder();
		unknownb.rawData(packetPayload.getBytes());
		IcmpV4CommonPacket.Builder icmpV4b = new IcmpV4CommonPacket.Builder();
		icmpV4b.type(IcmpV4Type.SKIP).code(IcmpV4Code.NO_CODE).payloadBuilder(unknownb).correctChecksumAtBuild(true);
		IpV4Packet.Builder ipv4b = new IpV4Packet.Builder();
		try {
			ipv4b.version(IpVersion.IPV4).tos(IpV4Rfc1349Tos.newInstance((byte) 0)).identification((short) 100).ttl((byte) 100)
					.protocol(IpNumber.ICMPV4)
					.srcAddr((Inet4Address) InetAddress.getByAddress(new byte[] { (byte) 192, (byte) 168, (byte) 123, (byte) 111 }))
					.dstAddr((Inet4Address) InetAddress.getByAddress(new byte[] { (byte) 192, (byte) 168, (byte) 123, (byte) 1 }))
					.payloadBuilder(icmpV4b).correctChecksumAtBuild(true).correctLengthAtBuild(true);
		} catch (UnknownHostException e) {
			throw new AssertionError("Never get here.");
		}
		return createEthernetFrame(srcMacAddress, EtherType.IPV4, ipv4b);
	}

	public static Packet createEthernetFrame(MacAddress srcMacAddress, EtherType type, Builder builder) {
		EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
		etherBuilder.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS).srcAddr(srcMacAddress).type(type).payloadBuilder(builder).paddingAtBuild(true);
		return etherBuilder.build();
	}

}
