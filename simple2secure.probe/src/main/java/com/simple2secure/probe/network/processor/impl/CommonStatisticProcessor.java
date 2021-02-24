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
package com.simple2secure.probe.network.processor.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.pcap4j.packet.ArpPacket.ArpHeader;
import org.pcap4j.packet.BsdLoopbackPacket.BsdLoopbackHeader;
import org.pcap4j.packet.EthernetPacket.EthernetHeader;
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
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.network.PacketContainer;
import com.simple2secure.probe.network.PacketProcessor;
import com.simple2secure.probe.utils.DBUtil;

/***
 * The CommonStatisticsProcessor reads all the basic data from the packet and writes it to the Database. This data includes: Timestamp,
 * Source- and Destination Mac Address, Source- and Destination IP Address, Type (IPv4, Ipv6...) and Protocol (TCP, UDP...) both in the same
 * slot, and length
 *
 * @author jhoffmann
 *
 */
public class CommonStatisticProcessor extends PacketProcessor {

	private static Logger log = LoggerFactory.getLogger(CommonStatisticProcessor.class);

	private Date analysisStartTime;

	private NetworkReport report;

	private String content;

	private String srcMac, destMac, srcIp, destIp, protocol;

	private EtherType type;

	private int length;

	Map<String, Integer> sourceIp;

	Map<String, Integer> destinationIp;

	Map<String, Integer> sourceMac;

	Map<String, Integer> destinationMac;

	Map<String, Integer> protocols;

	private int maxLength;

	public CommonStatisticProcessor(String name, Map<String, String> options) {
		super(name, options);
		analysisStartTime = new Date();
		report = new NetworkReport();
		report.setId(new ObjectId());
		report.setDeviceId(ProbeConfiguration.probeId);
		report.setGroupId(new ObjectId(ProbeConfiguration.groupId));
		report.setStartTime(analysisStartTime);
		report.setHostname(ProbeConfiguration.hostname);

		sourceIp = new TreeMap<>();
		destinationIp = new TreeMap<>();
		sourceMac = new TreeMap<>();
		destinationMac = new TreeMap<>();
		protocols = new TreeMap<>();
		maxLength = 0;
	}

	@Override
	public PacketContainer processPacket() {
		/*
		 * Obtain the features for this packet. Depending on the feature type and the actual packet (TCP, UDP, or higher layer packet instances)
		 * different features will be returned.
		 */

		Packet packet = this.packet.getPacket();

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
				type = EtherType.IPV4;
			}

			srcMac = "local!";
			destMac = "local!";
		} else {
			//
			log.debug("None-Ethernet Packet arrived!");
			return this.packet;
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
			String payloadHeader = packet.getPayload().getHeader().toString().trim();
			if (!Strings.isNullOrEmpty(payloadHeader)) {
				String packetType = StringUtils.substringBetween(payloadHeader, "[", "]").trim();
				packetType = StringUtils.substringBefore(packetType, "header");
				log.debug("Packet with not monitored protocol type arrived. Type {}", packetType);
			}
			srcIp = "?";
			destIp = "?";
			protocol = "?";
		}

		return this.packet;
	}

	@Override
	public void performAnalysis() {

		long intervalTime = packet.getProcessor().getAnalysisInterval();
		TimeUnit intervalUnit = packet.getProcessor().getAnalysisIntervalUnit();
		/*
		 * TODO: Verify that this never happens or create a better handler for the case that null is returned. Also use shared constants for
		 * that.
		 */
		if (intervalUnit == null) {
			intervalUnit = TimeUnit.HOURS;
		}
		try {
			if (isAnalysisTimeExpired(getAnalysisIntervalInMinutes(intervalTime, intervalUnit), analysisStartTime)) {
				// save the report to the database
				// set new start time
				// set reportContent stringBuilder
				// initialize new report
				if (report.getDeviceId() != null && report.getStartTime() != null) {
					writeNetworkTrafficResults();
					if(report.getId() == null) {
						report.setId(new ObjectId());
					}
					report.setProcessorName(packet.getProcessor().getName());
					report.setStringContent(content);
					report.setSent(false);
					DBUtil.getInstance().save(report);
				}
				analysisStartTime = new Date();

				report = new NetworkReport();
				report.setDeviceId(ProbeConfiguration.probeId);
				report.setGroupId(new ObjectId(ProbeConfiguration.groupId));
				report.setStartTime(analysisStartTime);
				report.setProcessorName(packet.getProcessor().getName());
				report.setHostname(ProbeConfiguration.hostname);

				// reportContent = new TreeMap<>();
				sourceIp = new TreeMap<>();
				destinationIp = new TreeMap<>();
				sourceMac = new TreeMap<>();
				destinationMac = new TreeMap<>();
				protocols = new TreeMap<>();
				maxLength = 0;
			} else {
				countNetworkTraffic();
			}
		} catch (ParseException e) {
			log.error("Error occured during the expiration time check: " + e.getMessage());
		}
	}

	/**
	 * This function counts the network traffic data provided by each packet.
	 */
	private void countNetworkTraffic() {
		// Count sourceIPs
		if (!Strings.isNullOrEmpty(srcIp)) {
			Integer countSrcIp = sourceIp.get(srcIp);

			if (countSrcIp == null) {
				sourceIp.put(srcIp, 1);
			} else {
				sourceIp.put(srcIp, countSrcIp + 1);
			}
		}

		if (!Strings.isNullOrEmpty(destIp)) {
			// Count destination IPs
			Integer countDestIp = destinationIp.get(destIp);

			if (countDestIp == null) {
				destinationIp.put(destIp, 1);
			} else {
				destinationIp.put(destIp, countDestIp + 1);
			}
		}

		if (!Strings.isNullOrEmpty(srcMac)) {
			// Count source MACs
			Integer countSrcMac = sourceMac.get(srcMac);
			if (countSrcMac == null) {
				sourceMac.put(srcMac, 1);
			} else {
				sourceMac.put(srcMac, countSrcMac + 1);
			}
		}

		if (!Strings.isNullOrEmpty(destMac)) {
			// Count dest MACs
			Integer countDestMac = destinationMac.get(destMac);
			if (countDestMac == null) {
				destinationMac.put(destMac, 1);
			} else {
				destinationMac.put(destMac, countDestMac + 1);
			}
		}

		if (!Strings.isNullOrEmpty(protocol)) {
			// Count protocols
			Integer countProtocol = protocols.get(protocol);
			if (countProtocol == null) {
				protocols.put(protocol, 1);
			} else {
				protocols.put(protocol, countProtocol + 1);
			}
		}

		if (length > maxLength) {
			maxLength = length;
		}
	}

	/*
	 * This function analyzes the collected values and writes the maximum values to the content map
	 */
	private void writeNetworkTrafficResults() {
		sourceIp = sortByValue(sourceIp);
		destinationIp = sortByValue(destinationIp);
		destinationMac = sortByValue(destinationMac);
		sourceMac = sortByValue(sourceMac);
		protocols = sortByValue(protocols);

		Map<String, String> resultEntry = new LinkedHashMap<>();

		if (!sourceIp.isEmpty()) {
			String srcIP = "{";
			for (Map.Entry<String, Integer> entry : sourceIp.entrySet()) {
				srcIP = srcIP + "'" + entry.getKey().replace("/", "") + "' : '" + entry.getValue() + "' , ";
			}
			srcIP = srcIP.substring(0, srcIP.length() - 2);
			srcIP = srcIP + "}";

			resultEntry.put("Source IP", srcIP);
		}
		if (!destinationIp.isEmpty()) {
			String destIP = "{";
			for (Map.Entry<String, Integer> entry : destinationIp.entrySet()) {
				destIP = destIP + "'" + entry.getKey().replace("/", "") + "' : '" + entry.getValue() + "' , ";
			}
			destIP = destIP.substring(0, destIP.length() - 2);
			destIP = destIP + "}";

			resultEntry.put("Destination IP", destIP);
		}
		if (!destinationMac.isEmpty()) {
			String destMAC = "{";
			for (Map.Entry<String, Integer> entry : destinationMac.entrySet()) {
				destMAC = destMAC + "'" + entry.getKey().replace("/", "") + "' : '" + entry.getValue() + "' , ";
			}
			destMAC = destMAC.substring(0, destMAC.length() - 2);
			destMAC = destMAC + "}";

			resultEntry.put("Destination MAC", destMAC);
		}
		if (!sourceMac.isEmpty()) {
			String srcMAC = "{";
			for (Map.Entry<String, Integer> entry : sourceMac.entrySet()) {
				srcMAC = srcMAC + "'" + entry.getKey().replace("/", "") + "' : '" + entry.getValue() + "' , ";
			}
			srcMAC = srcMAC.substring(0, srcMAC.length() - 2);
			srcMAC = srcMAC + "}";

			resultEntry.put("Source MAC", srcMAC);
		}
		if (!protocols.isEmpty()) {
			String protocol = "{";
			for (Map.Entry<String, Integer> entry : protocols.entrySet()) {
				protocol = protocol + "'" + entry.getKey().replace("/", "") + "' : '" + entry.getValue() + "' , ";
			}
			protocol = protocol.substring(0, protocol.length() - 2);
			protocol = protocol + "}";

			resultEntry.put("Protocols", protocol);
		}

		content = JSONUtils.toString(resultEntry);
		content = content.replace("\"{", "{");
		content = content.replace("}\"", "}");
		content = content.replace("'", "\"");
		log.debug(content);

	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue(Comparator.reverseOrder()));

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(Map<K, V> map) {
		map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> {
					throw new AssertionError();
				}, LinkedHashMap::new));
		return map;
	}
}
