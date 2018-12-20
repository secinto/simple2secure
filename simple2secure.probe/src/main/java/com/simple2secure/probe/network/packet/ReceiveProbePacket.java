package com.simple2secure.probe.network.packet;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.PacketUtil;

public class ReceiveProbePacket {

	public ReceiveProbePacket(ProbePacket probePacket) {
		if (probePacket != null) {
			storeReceivedProbePacketToDB(probePacket);
		}
	}

	private void storeReceivedProbePacketToDB(ProbePacket probePacket) {
		DBUtil.getInstance().merge(probePacket);
		PacketUtil.hasProbePacketTableChanged = true;
	}
}
