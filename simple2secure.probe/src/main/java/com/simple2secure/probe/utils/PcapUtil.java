package com.simple2secure.probe.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pcap4j.packet.ArpPacket.ArpHeader;
import org.pcap4j.packet.BsdLoopbackPacket.BsdLoopbackHeader;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.EthernetPacket.EthernetHeader;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpV4Packet.IpV4Header;
import org.pcap4j.packet.IpV6Packet.IpV6Header;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.Packet.Header;
import org.pcap4j.packet.PppPacket.PppHeader;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.ProtocolFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.PacketInfo;

public class PcapUtil {
	private static Logger log = LoggerFactory.getLogger(PcapUtil.class);

	public static Map<String, Boolean> findOutgoingInterfaces(List<String> addresses) {
		Map<String, Boolean> mapping = new HashMap<String, Boolean>();
		for (String ipAddress : addresses) {
			mapping.put(ipAddress, checkAddress(ipAddress));
		}
		return mapping;
	}

	public static Packet getPacketFromHexString(String hexStreamAsString, int offset) throws IllegalRawDataException {
		byte[] hexStringAsBArray = hexStringToByteArray(hexStreamAsString);
		return EthernetPacket.newPacket(hexStringAsBArray, offset, hexStringAsBArray.length);
	}

	public static boolean checkAddress(String ipAddress) {
		Socket soc = new java.net.Socket();
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

			while (nets.hasMoreElements()) {
				NetworkInterface networkInterface = nets.nextElement();
				List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
				for (InterfaceAddress address : addresses) {

					InetAddress inet = InetAddress.getByName(ipAddress);

					if (address.getAddress() instanceof Inet4Address && address.getAddress().equals(inet)) {
						soc.bind(new InetSocketAddress(address.getAddress(), 0));
						soc.connect(new InetSocketAddress(InetAddress.getByName("stackoverflow.com"), 80));
						soc.close();
						return true;
					}
				}
			}
		} catch (Exception e) {
			log.error("Trying to use address {} for pinging remote address failed {}", ipAddress, e.getLocalizedMessage());
		}
		return false;
	}

	public static String getMacAddress(InetAddress ipAddress) {
		try {
			// InetAddress ipAddress = InetAddress.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ipAddress);
			byte[] macAddressBytes = networkInterface.getHardwareAddress();
			StringBuilder macAddressBuilder = new StringBuilder();

			for (int macAddressByteIndex = 0; macAddressByteIndex < macAddressBytes.length; macAddressByteIndex++) {
				String macAddressHexByte = String.format("%02X", macAddressBytes[macAddressByteIndex]);
				macAddressBuilder.append(macAddressHexByte);

				if (macAddressByteIndex != macAddressBytes.length - 1) {
					macAddressBuilder.append(":");
				}
			}

			return macAddressBuilder.toString();
		} catch (Exception e) {
			log.error("Couldn't obtain network MAC address. Reason: {}", e.getCause());
			return "00:00:00:00:00:00";
		}
	}

	public static PacketInfo extractPacketInformation(Packet packet) {
		int length;
		String srcMac = "", destMac = "", srcIp = "", destIp = "", protocol = "";
		EtherType type = null;
		length = packet.length();

		Header h = packet.getHeader();
		if (h instanceof EthernetHeader) {
			EthernetHeader eh = (EthernetHeader) h;

			srcMac = eh.getSrcAddr().toString();
			destMac = eh.getDstAddr().toString();
			type = eh.getType();
		}
		// this is the case if a local file is used - could be deleted
		else if (h instanceof BsdLoopbackHeader) {
			if (((BsdLoopbackHeader) h).getProtocolFamily().equals(ProtocolFamily.PF_INET)) {
				type = EtherType.IPV4;
			} else {
				type = null;
			}

			srcMac = "local!";
			destMac = "local!";
		} else {
			//
			log.debug("None-Ethernet Packet arrived!");
		}
		Packet tmp = packet.getPayload();

		if (tmp.getHeader() == null) {
			log.debug("Malformed package encountered!\n" + packet.toString());
			srcIp = "MALFORMED PACKAGE!";
			destIp = "MALFORMED PACKAGE!";
			protocol = "MALFORMED PACKAGE!";
		} else if (type.equals(EtherType.IPV4)) {
			IpV4Header header = (IpV4Header) tmp.getHeader();
			srcIp = header.getSrcAddr().toString();
			destIp = header.getDstAddr().toString();
			protocol = "ipv4." + header.getProtocol().name();
		} else if (type.equals(EtherType.IPV6)) {
			IpV6Header header = (IpV6Header) tmp.getHeader();
			srcIp = header.getSrcAddr().toString();
			destIp = header.getDstAddr().toString();
			protocol = "ipv6." + header.getProtocol().name();
		} else if (type.equals(EtherType.ARP)) {
			ArpHeader header = (ArpHeader) tmp.getHeader();
			srcIp = header.getSrcProtocolAddr().toString();
			destIp = header.getDstProtocolAddr().toString();
			protocol = "arp." + type.name();
		} else if (type.equals(EtherType.PPP)) {
			PppHeader header = (PppHeader) tmp.getHeader();
			srcIp = "-";
			destIp = "-";
			protocol = "ppp." + header.getProtocol().name();
		} else {
			log.debug("Packet with unexpected protocol type arrived");
			srcIp = "?";
			destIp = "?";
			protocol = "?";
		}

		PacketInfo packetInfo = new PacketInfo(destIp, srcIp, destMac, srcMac, length, protocol);
		return packetInfo;
	}

	/**
	 * Does exclude the traffic to the local portal
	 *
	 * @return
	 */
	public static String getBPFFilterLocal() {
		StringBuilder bpfFilter = new StringBuilder();
		bpfFilter.append("not host 127.0.0.1 and not port (8443 or 8080 or 9000)");
		return bpfFilter.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
