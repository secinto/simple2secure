package com.simple2secure.probe.network.packet;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.utils.DBUtil;

public class ReceiveProbePacket {

	private ProbePacket probePacket;

	public ReceiveProbePacket(ProbePacket probePacket) {
		this.probePacket = probePacket;
		storeReceivedProbePacketToDB(probePacket);
	}

	private void storeReceivedProbePacketToDB(ProbePacket probePacket) {
		DBUtil.getInstance().merge(probePacket);
		DBUtil.hasDBChanged = true;
	}

}
