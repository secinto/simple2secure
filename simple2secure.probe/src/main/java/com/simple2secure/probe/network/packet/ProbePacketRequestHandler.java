package com.simple2secure.probe.network.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.utils.PacketUtil;

public class ProbePacketRequestHandler implements Runnable {
	private static Logger log = LoggerFactory.getLogger(ProbePacketRequestHandler.class);

	// private Packet requestPacket;
	// private int defaultOffset = 0;
	// private int defaultSnapLen = 65536;
	// private int defaultReadTimeOut = 10;
	// private PcapHandle pcapHandler;
	// private PcapHandle pcapReceiveHandle;
	private ProbePacket probePacket;
	private ProcessingQueue<ProbePacket> processingQueue;
	private boolean running = false;

	public ProbePacketRequestHandler(ProbePacket probePacket) {
		this.probePacket = probePacket;

		// try {
		// requestPacket = PcapUtil.convertHexStreamToPacket(probePacket.getHexAsStringFrame(), defaultOffset);
		// } catch (IllegalRawDataException e) {
		// log.error("The provided hex string could not be converted to a valid packet!", e);
		// }
		//
		// PcapNetworkInterface nif = null;
		// try {
		// nif = PcapUtil.getNetworkInterfaceByInetAddr(PcapUtil.getIpAddrOfNetworkInterface());
		// } catch (UnknownHostException e) {
		// log.error("Could not provide a Network Interface.", e);
		// } catch (SocketException e) {
		// log.error("Could not provide a Network Interface.", e);
		// }
		//
		// if (nif != null) {
		// try {
		// pcapHandler = nif.openLive(defaultSnapLen, PromiscuousMode.PROMISCUOUS, defaultReadTimeOut);
		// } catch (PcapNativeException e) {
		// log.error("Could not acces a network interface.", e);
		// }
		// }
		// }
		//
		// public void forwardProbePacket() {
		// PacketHandler pH = new PacketHandler();
		// // If the always flag is set, mandatory properties are the interval of the analysis on its own and the unit of the analysis interval.
		// if (probePacket.isAlways()) {
		// if (!(probePacket.getAnalysisInterval() < 1) || !(probePacket.getAnalysisIntervalUnit() == null)) {
		// while (true) {
		// pH.sendPackets(pcapHandler, requestPacket);
		// try {
		// Thread.sleep(probePacket.getAnalysisIntervalUnit().toSeconds(probePacket.getAnalysisInterval()));
		// } catch (InterruptedException e) {
		// log.error("The interval of the analysis was not provided correctly.");
		// }
		// }
		// } else {
		// log.error("For sending in a endless loop, the \"analysisInterval\" and \"analysisIntervalUnit\" must be provided.");
		// }
		// }
		//
		// if (probePacket.getRequestCount() > 1) {
		// if (!(probePacket.getAnalysisInterval() < 1) || !(probePacket.getAnalysisIntervalUnit() == null)) {
		// for (int i = 0; i < probePacket.getRequestCount(); i++) {
		// pH.sendPackets(pcapHandler, requestPacket);
		// try {
		// Thread.sleep(probePacket.getAnalysisIntervalUnit().toSeconds(probePacket.getAnalysisInterval()));
		// } catch (InterruptedException e) {
		// log.error("The interval of the analysis was not provided correctly.");
		// }
		// }
		// }
		// }
		//
		// pH.sendPackets(pcapHandler, requestPacket);
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			if (!PacketUtil.isPacketInDB(probePacket)) {
				PacketUtil.updateProbePacketInDB(probePacket);
			}

			if (PacketUtil.isPacketChanged(probePacket)) {
				PacketUtil.updateProbePacketInDB(probePacket);
			}

			for (ProbePacket packet : PacketUtil.getAllProbePacketsFromDB()) {
				processingQueue.push(packet);
				System.out.println(packet);
			}

		}

	}

}
