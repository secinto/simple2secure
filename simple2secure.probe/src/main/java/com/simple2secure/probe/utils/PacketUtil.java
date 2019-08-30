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
package com.simple2secure.probe.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pcap4j.packet.IllegalRawDataException;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.probe.network.packet.ProbePacketCrafter;
import com.simple2secure.probe.network.packet.ProbePacketQueueHandler;

public class PacketUtil {

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
	 * This method checks if the provided ProbePacket which already exists in the DB has been changed.
	 *
	 * @param taskMap
	 */
	public static List<ProbePacket> getChangedPackets(Map<String, ProbePacketQueueHandler> taskMap) {
		List<ProbePacket> changedPacketList = new ArrayList<>();
		List<ProbePacket> probePacketList = getAllProbePacketsFromDB();

		for (ProbePacket probePacket : probePacketList) {
			ProbePacketQueueHandler task = taskMap.get(probePacket.getName());
			if (!(task.getProbePacket().getRequestCount() == probePacket.getRequestCount()
					&& task.getProbePacket().getAnalysisInterval() == probePacket.getAnalysisInterval()
					&& task.getProbePacket().getAnalysisIntervalUnit().equals(probePacket.getAnalysisIntervalUnit())
					&& task.getProbePacket().getPacketAsHexStream().equals(probePacket.getPacketAsHexStream()))) {
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

	// only for test
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

	public static ProbePacket craftOneProbePacket(String type, String groupId, String name, boolean always, int requestCount,
			long analysisInterval) {
		ProbePacket packetToCraft = null;
		try {
			packetToCraft = ProbePacketCrafter.craftProbePacket(type, groupId, name, always, requestCount, analysisInterval);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packetToCraft;
	}

}
