package com.simple2secure.probe.network.packet;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.utils.PcapUtil;

public class ProbePacketRequestHandler {
	private static Logger log = LoggerFactory.getLogger(ProbePacketRequestHandler.class);

	private String groupId;
	private String name;
	private String probePacketId;
	private boolean always;
	private int defaultRequestCount = 1;
	private long analysisInterval;
	private TimeUnit analysisIntervalUnit;
	private Packet requestPacket;
	private int defaultOffset = 0;
	private int defaultSnapLen = 65536;
	private int defaultReadTimeOut = 10;
	private PcapHandle pcapHandle;

	public ProbePacketRequestHandler(ProbePacket probePacket) {
		groupId = probePacket.getGroupId();
		name = probePacket.getName();
		always = probePacket.isAlways();
		defaultRequestCount = probePacket.getRequestCount();
		analysisInterval = probePacket.getAnalysisInterval();
		analysisIntervalUnit = probePacket.getAnalysisIntervalUnit();

		try {
			requestPacket = PcapUtil.getPacketFromHexString(probePacket.getHexAsStringFrame(), defaultOffset);
		} catch (IllegalRawDataException e) {
			log.error("The provided hex string could not be converted to a valid packet!", e);
		}

		PcapNetworkInterface nif = null;
		try {
			nif = new NifSelector().selectNetworkInterface();
		} catch (IOException e) {
			log.error("No network interface available.", e);
		}

		try {
			pcapHandle = nif.openLive(defaultSnapLen, PromiscuousMode.PROMISCUOUS, defaultReadTimeOut);
		} catch (PcapNativeException e) {
			log.error("Could not acces a network interface.", e);
		}
	}

	public void forwardProbePacket() {
		PacketHandler pH = new PacketHandler();
		// If the always flag is set, mandatory properties are "analysisInterval" and "analysisIntervalUnit".
		if (always) {
			if (!(analysisInterval < 1) || !(analysisIntervalUnit == null)) {
				while (true) {
					pH.sendPackets(pcapHandle, requestPacket);
					try {
						Thread.sleep(analysisIntervalUnit.toSeconds(analysisInterval));
					} catch (InterruptedException e) {
						log.error("The interval of the analysis was not provided correctly.");
					}
				}
			} else {
				log.error("For sending in a endless loop, the \"analysisInterval\" and \"analysisIntervalUnit\" must be provided.");
			}
		}

		if (defaultRequestCount > 1) {
			if (!(analysisInterval < 1) || !(analysisIntervalUnit == null)) {
				for (int i = 0; i < defaultRequestCount; i++) {
					pH.sendPackets(pcapHandle, requestPacket);
					try {
						Thread.sleep(analysisIntervalUnit.toSeconds(analysisInterval));
					} catch (InterruptedException e) {
						log.error("The interval of the analysis was not provided correctly.");
					}
				}
			}
		}

		pH.sendPackets(pcapHandle, requestPacket);
	}

}
