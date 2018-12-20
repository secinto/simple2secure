package com.simple2secure.probe.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.network.packet.ProbePacketQueueHandler;

public class PacketUtil {

	public static boolean hasProbePacketTableChanged = false;

	/**
	 * This method checks the DB for the provided ProbePacket.
	 *
	 * @param... the ProbePacket to look for @return... true if the packet was found in the DB
	 */
	public static boolean isPacketInDB(ProbePacket probePacket) {
		boolean isPacketInDB = false;
		List<ProbePacket> probePacketList = getAllProbePacketsFromDB();
		for (ProbePacket pPacket : probePacketList) {
			if (pPacket.getId().equals(probePacket.getId())) {
				isPacketInDB = true;
			}
		}
		return isPacketInDB;
	}

	/**
	 * This method merges the ProbePacket in the DB. If the ProbePacket already exists in DB it updates the packet, if the packet don't exist
	 * in the DB it creates a new entry in the DB.
	 */
	public static void updateProbePacketInDB(ProbePacket probePacket) {
		DBUtil.getInstance().merge(probePacket);
	}

	/**
	 * This method removes all entries from the ProbePacketTable in the DB.
	 */
	public static void removeAllEntriesFromProbePacketTable() {
		List<ProbePacket> probePacketList = DBUtil.getInstance().findAll(ProbePacket.class);
		if (probePacketList.size() != 0) {
			for (ProbePacket probePacket : probePacketList) {
				DBUtil.getInstance().delete(probePacket);
			}
		}
	}

	/**
	 * This method checks if the provided ProbePacket which already exists in the DB has been changed.
	 *
	 * @param... the ProbePacket to check if it has changed @return... true if the packet contains one not matching value
	 */
	public static List<ProbePacket> getChangedPackets(Map<String, ProbePacketQueueHandler> taskMap) {
		List<ProbePacket> changedPacketList = null;
		List<ProbePacket> probePacketList = getAllProbePacketsFromDB();

		for (ProbePacket probePacket : probePacketList) {
			ProbePacketQueueHandler task = taskMap.get(probePacket.getId());
			if (!(task.getProbePacket().getGroupId().equals(probePacket.getGroupId())
					&& task.getProbePacket().getRequestCount() == probePacket.getRequestCount()
					&& task.getProbePacket().getAnalysisInterval() == probePacket.getAnalysisInterval()
					&& task.getProbePacket().getAnalysisIntervalUnit().equals(probePacket.getAnalysisIntervalUnit())
					&& task.getProbePacket().getPacketAsHexStream().equals(probePacket.getPacketAsHexStream()))) {
				changedPacketList = new ArrayList<>();
				changedPacketList.add(probePacket);
			}
		}
		return changedPacketList;
	}

	/**
	 * This method returns a list of all ProbePackets stored in the DB
	 */
	public static List<ProbePacket> getAllProbePacketsFromDB() {
		return DBUtil.getInstance().findAll(ProbePacket.class);
	}

	/**
	 * This method returns specifically the provided ProbePacket from the DB.
	 */
	public static ProbePacket getProbePacketFromDB(ProbePacket probePacket) {
		ProbePacket resultPacket = null;
		List<ProbePacket> packetList = DBUtil.getInstance().findByFieldName("name", probePacket.getName(), ProbePacket.class);
		if (packetList.get(0) != null) {
			resultPacket = packetList.get(0);
		}
		return resultPacket;
	}
}
