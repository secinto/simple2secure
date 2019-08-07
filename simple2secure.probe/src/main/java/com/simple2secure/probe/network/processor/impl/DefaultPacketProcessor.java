package com.simple2secure.probe.network.processor.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.network.PacketContainer;
import com.simple2secure.probe.network.PacketProcessor;

public class DefaultPacketProcessor extends PacketProcessor {

	private static Logger log = LoggerFactory.getLogger(DefaultPacketProcessor.class);

	private Date analysisStartTime;

	private NetworkReport report;

	private int packetCounter;

	private Map<String, String> reportContent;

	private String reportContentString;

	public DefaultPacketProcessor(String name, Map<String, String> options) {
		super(name, options);

		analysisStartTime = new Date();
		report = new NetworkReport();
		report.setProbeId(ProbeConfiguration.probeId);
		report.setStartTime(analysisStartTime.toString());

		reportContent = new HashMap<>();
		packetCounter = 0;
		reportContentString = "";
	}

	@Override
	public PacketContainer processPacket() {
		packet.setPacketInProcess(false);
		return packet;
	}

	@Override
	public void performAnalysis() {
		long intervalTime = packet.getProcessor().getAnalysisInterval();
		TimeUnit intervalUnit = packet.getProcessor().getAnalysisIntervalUnit();
		if (intervalUnit == null) {
			intervalUnit = TimeUnit.HOURS;
		}

		try {
			if (isAnalysisTimeExpired(getAnalysisIntervalInMinutes(intervalTime, intervalUnit), analysisStartTime)) {
				// save the report to the database
				// set new start time
				// set reportContent stringBuilder
				// initialize new report
				if (!Strings.isNullOrEmpty(report.getProbeId()) && !Strings.isNullOrEmpty(report.getStartTime())) {
					report.setContent(reportContent);
					report.setProcessorName(packet.getProcessor().getName());
					report.setSent(false);
					/*
					 * TODO: Need to find a compacted way
					 */
					// DBUtil.getInstance().save(report);
				}
				analysisStartTime = new Date();
				report = new NetworkReport();
				report.setProbeId(ProbeConfiguration.probeId);
				report.setStartTime(analysisStartTime.toString());
				reportContent = new HashMap<>();
				packetCounter = 0;
				reportContentString = "";
			} else {
				packetCounter++;
				reportContentString = "";

				if (packet.getPacket() != null) {
					if (packet.getPacket().getPayload() != null && packet.getPacket().getPayload().getHeader() != null) {
						if (packet.getPacket().getPayload().getPayload() != null
								&& packet.getPacket().getPayload().getPayload().getHeader() != null) {
							if (packet.getPacket().getPayload().getPayload().getPayload() != null
									&& packet.getPacket().getPayload().getPayload().getPayload().getHeader() != null) {
								reportContentString = "Data Link Layer Header: " + packet.getPacket().getHeader().toString()
										+ "\n Network Layer Header: " + packet.getPacket().getPayload().getHeader().toString()
										+ "\n Transport Layer Header: "
										+ packet.getPacket().getPayload().getPayload().getHeader().toString()
										+ "\n Application Layer Header"
										+ packet.getPacket().getPayload().getPayload().getPayload().getHeader().toString()
										+ "\n Packet Timestamp: " + packet.getTimestamp() + "\n";

							} else {
								reportContentString = "Data Link Layer Header: " + packet.getPacket().getHeader().toString()
										+ "\n Network Layer Header: " + packet.getPacket().getPayload().getHeader().toString()
										+ "\n Transport Layer Header: "
										+ packet.getPacket().getPayload().getPayload().getHeader().toString() + "\n Packet Timestamp: "
										+ packet.getTimestamp() + "\n";
							}
						} else {
							reportContentString = "Data Link Layer Header: " + packet.getPacket().getHeader().toString()
									+ "\n Network Layer Header: " + packet.getPacket().getPayload().getHeader().toString()
									+ "\n Packet Timestamp: " + packet.getTimestamp() + "\n";
						}
					} else {
						reportContentString = "Data Link Layer Header: " + packet.getPacket().getHeader().toString()
								+ "\n Packet Timestamp: " + packet.getTimestamp() + "\n";
					}
				} else {
					log.debug("No actual packet received.");
					reportContentString = "No packet content available \n Packet Timestamp: " + packet.getTimestamp() + "\n";
				}
				reportContent.put(String.valueOf(packetCounter), reportContentString);

			}
		} catch (ParseException e) {
			log.error("Error occured during the expiration time check: " + e.getMessage());
		}

	}

}
