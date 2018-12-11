package com.simple2secure.probe.utils;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pcap4j.core.PcapAddress;
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

import com.simple2secure.api.model.PacketInfo;

public class PcapUtil {
	private static Logger log = LoggerFactory.getLogger(PcapUtil.class);

	public static Map<String, Boolean> findOutgoingInterfaces(List<String> addresses) {
		Map<String, Boolean> mapping = new HashMap<>();
		for (String ipAddress : addresses) {
			mapping.put(ipAddress, checkAddress(ipAddress));
		}
		return mapping;
	}

	public static Packet convertHexStreamToPacket(String hexStreamAsString, int offset) throws IllegalRawDataException {
		byte[] decodedString = Base64.getDecoder().decode(hexStreamAsString);
		return EthernetPacket.newPacket(decodedString, offset, decodedString.length);
	}

	public static String convertPackRawDataToHexStreamString(byte[] rawData) {
		byte[] encodedRawData = Base64.getEncoder().encode(rawData);
		return new String(encodedRawData);
	}

	/**
	 * This function returns the network interface with the provided ip address.
	 *
	 * @param... ipAdress of the network interface you want to get @return... PcapNetworkInterface object with the network interface of the
	 * provided ip
	 */
	public static PcapNetworkInterface getNetworkInterfaceByInetAddr(String ipAddress) {
		try {
			for (PcapNetworkInterface nI : Pcaps.findAllDevs()) {
				List<PcapAddress> ipAdressList = nI.getAddresses();
				for (PcapAddress add : ipAdressList) {
					if (add.getAddress().toString().equals("/" + ipAddress)) {
						return Pcaps.getDevByAddress(add.getAddress());
					}
				}
			}
		} catch (PcapNativeException e) {
			log.error("Could not find network interface with the provided ip.");
		}
		return null;
	}

	/**
	 * 8.8.8.8 the address does not have to be reachable
	 * https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	 *
	 * @return
	 * @throws UnknownHostException
	 * @throws SocketException
	 */
	public static String getIpAddrOfNetworkInterface() throws UnknownHostException, SocketException {
		// seems like this method is taking advantage of a sideffect of the "socket.connect" method
		String ipAddr;
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			ipAddr = socket.getLocalAddress().getHostAddress();
		}
		return ipAddr;
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
						socket.connect(new InetSocketAddress(InetAddress.getByName("stackoverflow.com"), 80));
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

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
