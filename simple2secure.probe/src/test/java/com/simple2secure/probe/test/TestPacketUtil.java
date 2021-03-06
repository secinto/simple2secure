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
package com.simple2secure.probe.test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.network.packet.ProbePacketCrafter;
import com.simple2secure.probe.network.packet.ProbePacketQueueHandler;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.PacketUtil;

public class TestPacketUtil {

	@AfterEach
	public void cleanTestSetup() {
		DBUtil.getInstance().clearDB(ProbePacket.class);
	}

	@Test
	public void isPacketInDB_PacketStoredInDB_True() throws UnsupportedEncodingException {
		ProbePacket arpPacket;
		arpPacket = ProbePacketCrafter.craftProbePacket("arp", "1", "arp-packet", false, 10, 1);
		arpPacket.setId(new ObjectId("5"));
		DBUtil.getInstance().merge(arpPacket);

		Assertions.assertTrue(PacketUtil.isPacketInDB(arpPacket));
	}

	@Test
	public void updateProbePacketInDB_ModifiedProbePacket_ModifiedProbePacket() throws UnsupportedEncodingException {
		ProbePacket arpPacket;
		arpPacket = ProbePacketCrafter.craftProbePacket("arp", "1", "arp-packet", false, 10, 1);
		arpPacket.setId(new ObjectId("1"));
		DBUtil.getInstance().merge(arpPacket);

		arpPacket = ProbePacketCrafter.craftProbePacket("arp", "1", "arp-packet1", false, 15, 5);
		arpPacket.setId(new ObjectId("1"));

		PacketUtil.updateProbePacketInDB(arpPacket);

		ProbePacket actualPacket = PacketUtil.getProbePacketFromDB(arpPacket);

		Assertions.assertEquals("arp-packet1", actualPacket.getName());
		Assertions.assertEquals(15, actualPacket.getRequestCount());
		Assertions.assertEquals(5, actualPacket.getAnalysisInterval());
	}

	@Test
	public void getChangedPackets_TaskMapAsParam_ChangedPackets() throws UnsupportedEncodingException {
		ProbePacket arpPacket;
		ProbePacket pingPacket;
		ProbePacket icmpCommonPacket;
		arpPacket = ProbePacketCrafter.craftProbePacket("arp", "1", "arp-packet", false, 10, 1);
		// arpPacket.setId("2");
		pingPacket = ProbePacketCrafter.craftProbePacket("ping", "2", "ping-packet", false, 50, 1);
		// pingPacket.setId("3");
		icmpCommonPacket = ProbePacketCrafter.craftProbePacket("icmpCommon", "2", "common-packet", false, 1, 1);
		// icmpCommonPacket.setId("4");
		DBUtil.getInstance().merge(arpPacket);
		DBUtil.getInstance().merge(pingPacket);
		DBUtil.getInstance().merge(icmpCommonPacket);

		arpPacket = ProbePacketCrafter.craftProbePacket("arp", "1", "arp-packet", false, 15, 5);
		// arpPacket.setId("2");
		Map<ObjectId, ProbePacketQueueHandler> taskMap = new HashMap<>();
		taskMap.put(new ObjectId(arpPacket.getName()), new ProbePacketQueueHandler(arpPacket, new ProcessingQueue<>()));
		taskMap.put(new ObjectId(pingPacket.getName()), new ProbePacketQueueHandler(pingPacket, new ProcessingQueue<>()));
		taskMap.put(new ObjectId(icmpCommonPacket.getName()), new ProbePacketQueueHandler(icmpCommonPacket, new ProcessingQueue<>()));

		List<ProbePacket> changedPackets = PacketUtil.getChangedPackets(taskMap);

		Assertions.assertEquals(1, changedPackets.size());
	}

}
