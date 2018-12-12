package com.simple2secure.probe.network.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.utils.PacketUtil;

public class ProbePacketRequestHandler implements Runnable {
	private static Logger log = LoggerFactory.getLogger(ProbePacketRequestHandler.class);

	private ProbePacket probePacket;
	public static boolean dbHasChanged = false;
	private boolean running = false;

	public ProbePacketRequestHandler() {
	}

	public ProbePacketRequestHandler(ProbePacket probePacket) {
		this.probePacket = probePacket;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			if (probePacket != null) {
				if (!PacketUtil.isPacketInDB(probePacket)) {
					PacketUtil.updateProbePacketInDB(probePacket);
					dbHasChanged = true;
				}

				if (PacketUtil.isPacketChanged(probePacket)) {
					PacketUtil.updateProbePacketInDB(probePacket);
					dbHasChanged = true;
				}
			}
		}
	}
}
