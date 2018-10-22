package com.simple2secure.probe.network.processor.impl;

import java.util.Arrays;
import java.util.Map;

import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.IcmpV6CommonPacket;
import org.pcap4j.packet.IcmpV6CommonPacket.IcmpV6CommonHeader;
import org.pcap4j.packet.IcmpV6EchoRequestPacket;
import org.pcap4j.packet.IpV4Packet.IpV4Header;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.Packet.Header;
import org.pcap4j.packet.TcpPacket.TcpHeader;
import org.pcap4j.packet.UdpPacket.UdpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.probe.network.PacketContainer;
import com.simple2secure.probe.network.PacketProcessor;

public class CommonPatternProcessor extends PacketProcessor {

	Logger log = LoggerFactory.getLogger(PacketProcessor.class);

	/**
	 * From my own observations, these are the 'phases' (sent packets) during Nmap
	 * -o: Six TCP Syn Packets Two ICMP echo Packets One TCP Packet which would
	 * usually be used for congestion controll Six TCP Packets with (Almost)
	 * identical Options
	 *
	 * more accurate definition can be seen in the actual code.
	 */

	// the raw Data of the options portion of the TCP Syn Packet sent by nmap -o
	// always the same
	public static final byte[] RAW_SYN_DATA_ONE = hexStringToByteArray("03030a01020405b4080affffffff000000000402");
	private static final byte[] RAW_SYN_DATA_TWO = hexStringToByteArray("020405780303000402080affffffff0000000000");
	private static final byte[] RAW_SYN_DATA_THREE = hexStringToByteArray("080affffffff0000000001010303050102040280");
	private static final byte[] RAW_SYN_DATA_FOUR = hexStringToByteArray("0402080affffffff0000000003030a00");
	private static final byte[] RAW_SYN_DATA_FIVE = hexStringToByteArray("020402180402080affffffff0000000003030a00");
	private static final byte[] RAW_SYN_DATA_SIX = hexStringToByteArray("020401090402080affffffff00000000");
	private static final byte[] RAW_EXPLICIT_CONGESTION_NOTIFICATION = hexStringToByteArray("03030a01020405b404020101");
	private static final byte[] RAW_TCP_TWO_TO_SIX = hexStringToByteArray("03030a0102040109080affffffff000000000402");
	private static final byte[] RAW_TCP_SEVEN = hexStringToByteArray("03030f0102040109080affffffff000000000402");

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();


	public CommonPatternProcessor(String name, Map<String, String> options) {
		super(name, options);
	}

	/**
	 * This function converts a hex String into the byte array
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/***
	 * calculates the flag from the raw Data
	 * 
	 * @param header
	 * @return
	 */
	private static int calcFlag(TcpHeader header) {
		// the last half byte of 12 and the thirteenth byte is always the position of
		// the flag byte in a TCP header
		return (header.getRawData()[12] & 0xf) + header.getRawData()[13];
	}

	@Override
	public PacketContainer processPacket() {
		// Vertex v = null;
		/*
		 * TO-DO
		 * 
		 * if (packet.getVertex() != null) { v = packet.getVertex(); } else { v =
		 * util.addVertex(identifier); //packet.setVertex(v); }
		 */

		Packet packet = this.packet.getPacket();
		Header header1 = packet.getPayload().getHeader();
		if (header1 instanceof IpV4Header) {
			Header header2 = packet.getPayload().getPayload().getHeader();
			if (header2 instanceof TcpHeader) {

				TcpHeader tHeader = (TcpHeader) header2;
				int flag = calcFlag(tHeader);

				// null packets should NEVER appear in a network as they break TCP rules
				if (flag == 0) {
					byte[] rawData = tHeader.getRawData();
					if (rawData.length > 20) {
						byte[] options = Arrays.copyOfRange(rawData, 20, rawData.length);
						if (Arrays.equals(options, RAW_TCP_TWO_TO_SIX) && tHeader.getWindow() == 128) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_TWO);
						}

						else {
							// util.setVertexProp(v, TYPE, PacketTags.TCP_NULL);
						}

					} else {
						// util.setVertexProp(v, TYPE, PacketTags.TCP_NULL);
					}

				}
				// fin packets are normal, however an excessive amount indicates a fin-scan
				else if (flag == 1) {
					// util.setVertexProp(v, TYPE, PacketTags.TCP_FIN);
				}

				// syn packets are normal, but should not be more than synack packets
				// a connection with only a syn and no syn/ack packet is half-open and shouldn't
				// appear too often
				else if (flag == 2) {
					byte[] rawData = tHeader.getRawData();
					// checks whether it is one of the six probe packets sent by nmap -o
					if (rawData.length > 20) {
						byte[] options = Arrays.copyOfRange(rawData, 20, rawData.length);
						int window = tHeader.getWindow();
						if (Arrays.equals(options, RAW_SYN_DATA_ONE) && window == 1) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_SYN_ONE);
						}

						else if (Arrays.equals(options, RAW_SYN_DATA_TWO) && window == 63) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_SYN_TWO);
						}

						else if (Arrays.equals(options, RAW_SYN_DATA_THREE) && window == 4) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_SYN_THREE);
						}

						else if (Arrays.equals(options, RAW_SYN_DATA_FOUR) && window == 4) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_SYN_FOUR);
						}

						else if (Arrays.equals(options, RAW_SYN_DATA_FIVE) && window == 16) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_SYN_FIVE);
						}

						else if (Arrays.equals(options, RAW_SYN_DATA_SIX) && window == 512) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_SYN_SIX);
						}

						else if (Arrays.equals(options, RAW_TCP_TWO_TO_SIX) && window == 31337) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_FIVE);
						}

						else {
							// util.setVertexProp(v, TYPE, PacketTags.TCP_SYN);
						}

					} else {
						// util.setVertexProp(v, TYPE, PacketTags.TCP_SYN);
					}
				}
				// rst packets forcibly close the connection, could be used as a kind of DOS
				// attack
				// possibly put into another class?
				else if (flag == 4) {
					// util.setVertexProp(v, TYPE, PacketTags.TCP_RST);
				}

				// ack packets are normal, however an excessive amount indicates an ack-scan
				else if (flag == 16) {
					byte[] rawData = tHeader.getRawData();
					if (rawData.length > 20) {
						byte[] options = Arrays.copyOfRange(rawData, 20, rawData.length);
						if (Arrays.equals(options, RAW_TCP_TWO_TO_SIX) && tHeader.getWindow() == 1024) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_FOUR);
						}

						else if (Arrays.equals(options, RAW_TCP_TWO_TO_SIX) && tHeader.getWindow() == 32768) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_SIX);
						}

						else {
							// util.setVertexProp(v, TYPE, PacketTags.TCP_ACK);
						}

					} else {
						// util.setVertexProp(v, TYPE, PacketTags.TCP_ACK);
					}

				}
				// synack packets are normal - only used to find out the proportion
				else if (flag == 18) {
					// util.setVertexProp(v, TYPE, PacketTags.TCP_SYN_ACK);
				}

				// xmas packets should NEVER appear in a network, as they break TCP rules
				else if (flag == 41) {
					byte[] rawData = tHeader.getRawData();
					if (rawData.length > 20) {
						byte[] options = Arrays.copyOfRange(rawData, 20, rawData.length);
						if (Arrays.equals(options, RAW_TCP_SEVEN) && tHeader.getWindow() == 65535) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_SEVEN);
						}

						else {
							// util.setVertexProp(v, TYPE, PacketTags.TCP_XMAS);
						}

					}
				}
				// part of the Nmap -o Scan. Illegal in itself, has however no other known uses
				// scan-wise.
				else if (flag == 43) {
					byte[] rawData = tHeader.getRawData();
					if (rawData.length > 20) {
						byte[] options = Arrays.copyOfRange(rawData, 20, rawData.length);
						if (Arrays.equals(options, RAW_TCP_TWO_TO_SIX) && tHeader.getWindow() == 256) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_THREE);
						}

					}
				}
				// this is a very peculiar constellation: SYN, ECN, CWR and the Reserved bit
				else if (flag == -128) {
					byte[] rawData = tHeader.getRawData();
					if (rawData.length > 20) {
						byte[] options = Arrays.copyOfRange(rawData, 20, rawData.length);
						if (tHeader.getUrgentPointer() == -2059 && tHeader.getAcknowledgmentNumber() == 0
								&& Arrays.equals(options, RAW_EXPLICIT_CONGESTION_NOTIFICATION)) {
							// util.setVertexProp(v, TYPE, PacketTags.OS_TCP_EXPLICIT_CONGESTION);
						}

					}
				}
			} else if (packet.get(IcmpV4CommonPacket.class) != null && packet.get(IcmpV4EchoPacket.class) != null) {
				IcmpV4CommonPacket iPacket1 = packet.get(IcmpV4CommonPacket.class);
				byte code = iPacket1.getHeader().getCode().value();
				short seq = packet.get(IcmpV4EchoPacket.class).getHeader().getSequenceNumber();
				int length = iPacket1.getPayload().getPayload().length();

				if (seq == 295 && code == 9 && length == 120) {
					// util.setVertexProp(v, TYPE, PacketTags.OS_ICMP_ONE);
				}

				else if (seq == 296 && code == 0 && length == 150) {
					// util.setVertexProp(v, TYPE, PacketTags.OS_ICMP_TWO);
				}

			} else if (packet.get(IcmpV6CommonPacket.class) != null
					&& packet.get(IcmpV6EchoRequestPacket.class) != null) {
				IcmpV6CommonPacket iPacket1 = packet.get(IcmpV6CommonPacket.class);
				byte code = iPacket1.getHeader().getCode().value();
				short seq = packet.get(IcmpV6EchoRequestPacket.class).getHeader().getSequenceNumber();
				int length = iPacket1.getPayload().getPayload().length();

				if (seq == 0 && code == 9 && length == 120) {
					// util.setVertexProp(v, TYPE, PacketTags.OS_ICMPV6_ONE);
				}

				else if (seq == 1 && code == 0 && length == 0) {
					// util.setVertexProp(v, TYPE, PacketTags.OS_ICMPV6_TWO);
				}

			} else if (packet.get(IcmpV6CommonPacket.class) != null) {
				IcmpV6CommonHeader iHeader = packet.get(IcmpV6CommonPacket.class).getHeader();
				if (iHeader.getCode().value() == 0 && iHeader.getType().value() == 135) {
					// util.setVertexProp(v, TYPE, PacketTags.OS_ICMPV6_SOLIC);
				}

			} else if (header2 instanceof UdpHeader) {
				// both UDPs and empty UDPs can be used for scanning, however a high proportion
				// of empty ones is more
				// alarming
				if (packet.length() == 28) {
					// util.setVertexProp(v, TYPE, PacketTags.UDP_EMPTY);
				}

			} // potentially check for uncommon/unexpected protocol types. But which?
		}

		return this.packet;
	}

	public void performAnalysis() {

	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
