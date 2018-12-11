package com.simple2secure.probe.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.pcap4j.packet.IllegalRawDataException;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.network.packet.ProbePacketCrafter;

public class PacketUtil {

	public static boolean isPacketInDB(ProbePacket probePacket) {
		boolean isPacketInDB = false;
		List<ProbePacket> probePacketList = getAllProbePacketsFromDB();
		for (ProbePacket pPacket : probePacketList) {
			if (pPacket.getId() == probePacket.getId()) {
				isPacketInDB = true;
			}
		}
		return isPacketInDB;
	}

	public static void updateProbePacketInDB(ProbePacket probePacket) {
		DBUtil.getInstance().merge(probePacket);
	}

	public static boolean isPacketChanged(ProbePacket updatedProbePacket) {
		ProbePacket probePacketToUpdate = getProbePacketFromDB(updatedProbePacket);
		boolean hasValsToUpdate = false;
		if (!(probePacketToUpdate.getName().equals(updatedProbePacket.getName())
				&& probePacketToUpdate.getGroupId().equals(updatedProbePacket.getGroupId())
				&& probePacketToUpdate.getAnalysisInterval() == updatedProbePacket.getAnalysisInterval()
				&& probePacketToUpdate.getAnalysisIntervalUnit().equals(updatedProbePacket.getAnalysisIntervalUnit())
				&& probePacketToUpdate.getHexAsStringFrame().equals(updatedProbePacket.getHexAsStringFrame())
				&& probePacketToUpdate.getRequestCount() == updatedProbePacket.getRequestCount())) {

			hasValsToUpdate = true;
		}
		return hasValsToUpdate;
	}

	public static List<ProbePacket> getAllProbePacketsFromDB() {
		return DBUtil.getInstance().findAll(ProbePacket.class);
	}

	public static ProbePacket getProbePacketFromDB(ProbePacket probePacket) {
		return (ProbePacket) DBUtil.getInstance().findByFieldName("name", probePacket.getName(), ProbePacket.class);
	}

	public static void craftProbePacketsForTest() throws IllegalRawDataException {

		try {
			ProbePacket arpPacket = ProbePacketCrafter.craftProbePacket("arp", "1", "arp-packet", false, 10, 1);
			arpPacket.setId("1");
			ProbePacket pingPacket = ProbePacketCrafter.craftProbePacket("ping", "2", "ping-packet", false, 50, 1);
			pingPacket.setId("2");
			ProbePacket icmpCommonPacket = ProbePacketCrafter.craftProbePacket("icmpCommon", "2", "common-packet", false, 1, 1);
			icmpCommonPacket.setId("3");

			DBUtil.getInstance().merge(arpPacket);
			DBUtil.getInstance().merge(pingPacket);
			DBUtil.getInstance().merge(icmpCommonPacket);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
