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
package com.simple2secure.probe.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapIpV4Address;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
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

import com.google.common.base.Strings;
import com.simple2secure.api.model.PacketInfo;
import com.simple2secure.commons.network.NetUtils;

public class PcapUtil {
	private static Logger log = LoggerFactory.getLogger(PcapUtil.class);

	private static PcapNetworkInterface outgoingInterface = null;
	private static String outgoingIPAddress = null;
	private static String outgoingNetmask = null;

	/**
	 * This method converts a packet converted to a hex stream, back to a packet.
	 *
	 * @param... a string object which contains a network packet converted to a hex stream string. @param... offset @return... a network
	 * packet converted from a hex stream string
	 */
	public static Packet convertHexStreamToPacket(String hexStreamAsString, int offset) throws IllegalRawDataException {
		byte[] decodedString = Base64.getDecoder().decode(hexStreamAsString);
		return EthernetPacket.newPacket(decodedString, offset, decodedString.length);
	}

	/**
	 * This method converts a network packet to a hex stream string
	 *
	 * @param... raw data of a packet as byte array @return... returns the converted network packet as hex stream string
	 */
	public static String convertPackRawDataToHexStreamString(byte[] rawData) {
		byte[] encodedRawData = Base64.getEncoder().encode(rawData);
		return new String(encodedRawData);
	}

	public static PcapNetworkInterface getOutgoingInterface(boolean show, boolean renew) {

		if (outgoingInterface != null && !renew) {
			log.debug("Already obtained outgoing interface is used and returend as result of getOutgoingInterface");
			return outgoingInterface;
		}

		List<PcapNetworkInterface> interfaces;

		try {
			interfaces = Pcaps.findAllDevs();
		} catch (PcapNativeException e) {
			log.error("An exception occured during obtaining all available network interfaces using PCAP4J. Reason {}", e.getLocalizedMessage());
			return null;
		}

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
						if (PcapUtil.outgoingInterface != null) {
							log.info("Found another usable address {}, discarding old one {}", ipAddress, outgoingIPAddress);
						}
						outgoingInterface = currentInterface;
						outgoingIPAddress = ipAddress;
						outgoingNetmask = ((PcapIpV4Address) address).getNetmask().getHostAddress();
						break;
					}
				}
			}
		}
		return outgoingInterface;
	}

	public static String getOutgoingIPAddress() {
		if (Strings.isNullOrEmpty(outgoingIPAddress)) {
			getOutgoingInterface(false, true);
		}
		return outgoingIPAddress;
	}

	public static String getOutgoingNetmask() {
		if (Strings.isNullOrEmpty(outgoingNetmask)) {
			getOutgoingInterface(false, true);
		}
		return outgoingNetmask;
	}

	public static boolean checkAddress(String ipAddress) {
		try (Socket socket = new java.net.Socket()) {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

			while (nets.hasMoreElements()) {
				NetworkInterface networkInterface = nets.nextElement();
				List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
				for (InterfaceAddress address : addresses) {

					InetAddress inet = InetAddress.getByName(ipAddress);

					if (address.getAddress() instanceof Inet4Address && address.getAddress().equals(inet)) {
						socket.bind(new InetSocketAddress(address.getAddress(), 0));
						log.info("Trying to use address {} for pinging remote address {}", address.getAddress(), "simple2secure.info");
						socket.connect(new InetSocketAddress(InetAddress.getByName("simple2secure.info"), 51003));
						socket.close();
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
}
