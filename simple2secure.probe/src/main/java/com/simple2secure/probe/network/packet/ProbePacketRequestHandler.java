package com.simple2secure.probe.network.packet;

import java.util.ArrayList;
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

import javafx.util.Pair;

/**
 * This class starts a Thread, which is periodically monitoring the DB for new ProbePackets. If there is a new ProbePacket in the DB it sets
 * the "dbHasChanged"-flag to true, it also sets the flag to true if the ProbePacket is in the DB but the configuration of the Probe- packet
 * has changed.
 */
public class ProbePacketRequestHandler implements Runnable {
	private static Logger log = LoggerFactory.getLogger(ProbePacketRequestHandler.class);

	private boolean running = false;
	private List<ProbePacket> probePacketList;
	private ProcessingQueue<Packet> processingQueue = new ProcessingQueue<>();
	private Map<String, ProbePacketQueueHandler> taskMap = new HashMap<>();
	private Timer timer = new Timer(true);
	private List<ProbePacketQueueHandler> taskList = new ArrayList<>();

	public ProbePacketRequestHandler() {
		PacketHandler pH = new PacketHandler(processingQueue);
		Thread pHT = new Thread(pH);
		pHT.start();
	}

	/**
	 * This method initializes the taskMap with the DB entries the first time this thread is started and fills the taskList which is going to
	 * be scheduled.
	 */
	private void initializeTasks() {
		if (taskMap.size() == 0) {
			probePacketList = DBUtil.getInstance().findAll(ProbePacket.class);
			if (probePacketList != null) {
				for (ProbePacket probePacket : probePacketList) {
					taskMap.put(probePacket.getId(), new ProbePacketQueueHandler(probePacket, processingQueue));
					for (int i = 0; i < probePacket.getRequestCount(); i++) {
						taskList.add(new ProbePacketQueueHandler(probePacket, processingQueue));
					}
				}
				startTasks(taskList);
			}
		}
	}

	/**
	 * This method stops the tasks started from the taskList.
	 */
	private void stopTasks() {
		for (ProbePacketQueueHandler pPQH : taskList) {
			pPQH.cancel();
		}
		timer.purge();
		log.info("The tasks have been stopped!");
	}

	/**
	 * This method starts the tasks from the provided taskList.
	 *
	 * @param taskMap...
	 *          contains the tasks which should be scheduled.
	 */
	private void startTasks(List<ProbePacketQueueHandler> taskList) {
		for (ProbePacketQueueHandler pPQH : taskList) {
			if (pPQH.scheduledExecutionTime() == 0) {
				timer.schedule(pPQH, pPQH.getProbePacket().getAnalysisInterval());
			}
		}
		log.info("The tasks have been started!");
	}

	/**
	 * This method removes the task with the provided probePacketId and add the changed task with the sam probePacketId to the taskMap.
	 *
	 * @param taskToChange...
	 *          the new task to be added to the taskMap
	 * @param probePacketId...
	 *          the id of the task which will be changed
	 */
	private void changeTaskMap(ProbePacketQueueHandler taskToChange, String probePacketId) {
		taskMap.remove(probePacketId);
		taskMap.put(probePacketId, taskToChange);
	}

	/**
	 * This method checks if there are new tasks in the DB which should be added to the taskMap or if a entry was removed from the DB and
	 * should be removed from the taskMap.
	 *
	 * @param taskMap...
	 *          the taskMap which should be checked against the tasks in the DB
	 *
	 * 					@return... a tuple which contains the keys "add" if something has to be added to the TaskMap or "rem" if something should be
	 *          removed from the taskMap and the task as value which should be added or removed from the taskMap.
	 *
	 */
	private Pair<String, ProbePacket> handleSurplusProbePacket(Map<String, ProbePacketQueueHandler> taskMap) {
		List<ProbePacket> packetListFromDB = DBUtil.getInstance().findAll(ProbePacket.class);
		Pair<String, ProbePacket> result = null;
		if (taskMap.size() < packetListFromDB.size()) {
			for (ProbePacket probePacketFromDB : packetListFromDB) {
				if (!taskMap.containsKey(probePacketFromDB.getId())) {
					result = new Pair<String, ProbePacket>("add", probePacketFromDB);
				}
			}
		} else if (taskMap.size() > packetListFromDB.size()) {
			for (Map.Entry<String, ProbePacketQueueHandler> entry : taskMap.entrySet()) {
				if (!packetListFromDB.contains(entry.getValue().getProbePacket())) {
					result = new Pair<String, ProbePacket>("rem", entry.getValue().getProbePacket());
				}
			}
		}
		return result;
	}

	@Override
	public void run() {
		running = true;
		initializeTasks();
		while (running) {
			// If there is a new entry in the DB
			if (PacketUtil.hasProbePacketTableChanged) {
				// If there is a new entry in the db stop actual tasks
				stopTasks();
				// Checks if the taskMap contains the same amount of tasks as the DB or the other way around
				if (taskMap.size() != DBUtil.getInstance().findAll(ProbePacket.class).size()) {
					Pair<String, ProbePacket> result = handleSurplusProbePacket(taskMap);
					// Removes or adds the tasks obtained from the result of handleSurplusProbePacket
					if (result != null) {
						if (result.getKey().equals("add")) {
							taskMap.put(result.getValue().getId(), new ProbePacketQueueHandler(result.getValue(), processingQueue));
							for (int i = 0; i < result.getValue().getRequestCount(); i++) {
								taskList.add(new ProbePacketQueueHandler(result.getValue(), processingQueue));
							}
						} else if (result.getKey().equals("rem")) {
							taskMap.remove(result.getValue().getId());
						}
					}
				}
				// Checks which packet has to be changed in the taskMap. And replaces the tasks in the taskMap.
				List<ProbePacket> changedPacketsList = PacketUtil.getChangedPackets(taskMap);
				if (changedPacketsList != null) {
					stopTasks();
					for (ProbePacket probePacket : changedPacketsList) {
						ProbePacketQueueHandler task = taskMap.get(probePacket.getId());
						changeTaskMap(task, probePacket.getId());
					}
				}
				PacketUtil.hasProbePacketTableChanged = false;
				startTasks(taskList);
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("The thread could not be set to wait.", e);
				}
			}
		}
	}
}
