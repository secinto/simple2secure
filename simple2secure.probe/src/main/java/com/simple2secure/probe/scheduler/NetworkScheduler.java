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
package com.simple2secure.probe.scheduler;

import java.util.Date;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;

import org.bson.types.ObjectId;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.network.NetworkMonitor;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;

public class NetworkScheduler extends TimerTask {

	private static Logger log = LoggerFactory.getLogger(NetworkScheduler.class);
	private NetworkMonitor monitor;
	private boolean filterApplied = false;

	public NetworkScheduler(NetworkMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void run() {
		log.debug("Running network scheduler");
		log.debug("Checking if server is reachable");
		ProbeUtils.isServerReachable();
		log.debug("Obtaining network statistics");
		getNetworkStatistics();
		log.debug("Checking network filtering");
		checkNetworkFilter();
		log.debug("Finished network scheduler run");
	}

	/**
	 * Obtains the network statistics as recorded by the PCAP library.
	 */
	private void getNetworkStatistics() {
		try {
			log.debug("Obtaining current network statistics from Pcap driver");
			PcapStat statistics = monitor.getReceiverHandle().getStats();
			NetworkReport report = new NetworkReport();
			report.id = new ObjectId();
			report.setStartTime(new Date());
			report.setProcessorName("PCAP Network Statistics");
			report.setDeviceId(ProbeConfiguration.probeId);
			report.setGroupId(new ObjectId(ProbeConfiguration.groupId));
			report.setHostname(ProbeConfiguration.hostname);
			Map<String, String> content = new TreeMap<>();
			content.put("PacketsCaptured", String.valueOf(statistics.getNumPacketsCaptured()));
			content.put("PacketsDropped", String.valueOf(statistics.getNumPacketsDropped()));
			content.put("PacketsDroppedByIf", String.valueOf(statistics.getNumPacketsDroppedByIf()));
			content.put("PacketsReceived", String.valueOf(statistics.getNumPacketsReceived()));
			report.setStringContent(JSONUtils.toString(content));
			DBUtil.getInstance().save(report);

		} catch (Exception e) {
			log.error("Couldn't obtain network statistics from PCAP. Reason {}", e.getCause());
		}
	}

	private void checkNetworkFilter() {
		try {
			/*
			 * TODO: Verification of BPF expression must be made online during the creation. We assume that they are correct.
			 */
			if (!filterApplied) {
				log.debug("Applying network filter");
				monitor.getReceiverHandle().setFilter(
						"not (host 127.0.0.1 and host 144.76.93.104 and port (8080 or 8443 or 9000 or 51003 or 51001))", BpfCompileMode.OPTIMIZE);
				filterApplied = true;
			}
		} catch (Exception e) {
			log.error("Couldn't apply filter for reason {}", e.getCause());
		}
	}
}