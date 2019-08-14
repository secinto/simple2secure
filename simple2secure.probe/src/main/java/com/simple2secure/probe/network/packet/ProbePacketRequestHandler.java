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
package com.simple2secure.probe.network.packet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.PacketUtil;

/**
 * This class starts a Thread, which is periodically monitoring the DB for new ProbePackets. If there is a new ProbePacket in the DB it sets
 * the "dbHasChanged"-flag to true, it also sets the flag to true if the ProbePacket is in the DB but the configuration of the Probe- packet
 * has changed.
 */
public class ProbePacketRequestHandler implements Runnable {
	private static Logger log = LoggerFactory.getLogger(ProbePacketRequestHandler.class);

	public static boolean dbHasChanged = false;
	private boolean running = false;
	private List<ProbePacket> probePacketList;
	private ProcessingQueue<Packet> processingQueue = new ProcessingQueue<>();
	private Map<String, ProbePacketQueueHandler> taskMap = new HashMap<>();

	public ProbePacketRequestHandler() {

	}

	private void initializeTasks() {
		probePacketList = DBUtil.getInstance().findAll(ProbePacket.class);
		if (probePacketList != null) {
			for (ProbePacket probePacket : probePacketList) {
				ProbePacketQueueHandler task = new ProbePacketQueueHandler(probePacket, processingQueue);
				taskMap.put(probePacket.getId(), task);
			}
			startTasks(taskMap);
		}
	}

	private void stopTasks() {
		Timer timer = new Timer(true);
		for (Map.Entry<String, ProbePacketQueueHandler> entry : taskMap.entrySet()) {
			entry.getValue().cancel();
			timer.purge();
		}
	}

	private void startTasks(Map<String, ProbePacketQueueHandler> taskMap) {
		Timer timer = new Timer(true);
		for (Map.Entry<String, ProbePacketQueueHandler> entry : taskMap.entrySet()) {
			if (entry.getValue().scheduledExecutionTime() == 0) {
				timer.schedule(entry.getValue(), entry.getValue().getProbePacket().getAnalysisInterval());
			}
		}
	}

	private void changeTaskMap(ProbePacketQueueHandler taskToChange, String probePacketId) {
		taskMap.remove(probePacketId);
		taskMap.put(probePacketId, taskToChange);
	}

	private ProbePacket findNewProbePacketForMap(Map<String, ProbePacketQueueHandler> taskMap) {
		ProbePacket packetToReturn = null;
		List<ProbePacket> packetsFromDB = DBUtil.getInstance().findAll(ProbePacket.class);
		for (ProbePacket probePacketFromDB : packetsFromDB) {
			if (!taskMap.containsKey(probePacketFromDB.getId())) {
				packetToReturn = probePacketFromDB;
			}
		}
		return packetToReturn;
	}

	@Override
	public void run() {
		running = true;
		initializeTasks();
		while (running) {
			if (DBUtil.hasDBChanged) {
				stopTasks();
				if (taskMap.size() < DBUtil.getInstance().findAll(ProbePacket.class).size()) {
					ProbePacket packet = findNewProbePacketForMap(taskMap);
					taskMap.put(packet.getId(), new ProbePacketQueueHandler(packet, processingQueue));
					DBUtil.hasDBChanged = false;
				}
				List<ProbePacket> changedPacketsList = PacketUtil.getChangedPackets(taskMap);
				if (changedPacketsList != null) {
					stopTasks();
					for (ProbePacket probePacket : changedPacketsList) {
						ProbePacketQueueHandler task = taskMap.get(probePacket.getId());
						changeTaskMap(task, probePacket.getId());
					}
				}
				startTasks(taskMap);
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block e.printStackTrace();
				}
			}
		}
	}
}
