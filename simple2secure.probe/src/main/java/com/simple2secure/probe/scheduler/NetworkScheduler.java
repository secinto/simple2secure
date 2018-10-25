package com.simple2secure.probe.scheduler;

import java.util.Date;
import java.util.TimerTask;

import org.pcap4j.core.PcapStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.api.config.ConfigItems;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.commons.general.TimingUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.network.NetworkMonitor;
import com.simple2secure.probe.utils.DBUtil;

public class NetworkScheduler extends TimerTask {

	private static Logger log = LoggerFactory.getLogger(NetworkScheduler.class);
	private NetworkMonitor monitor;

	public NetworkScheduler(NetworkMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void run() {
		isServerReachable();
		getNetworkStatistics();
		checkNetworkFilter();
	}

	/**
	 * This function checks if the server is reachable
	 */
	private void isServerReachable() {
		if (TimingUtils.netIsAvailable(ConfigItems.BASE_URL)) {
			ProbeConfiguration.setAPIAvailablitity(true);
			log.info("SERVER REACHABLE!");
		} else {
			ProbeConfiguration.setAPIAvailablitity(false);
			log.error("SERVER NOT REACHABLE!");
		}
	}

	private void getNetworkStatistics() {
		try {
			PcapStat statistics = monitor.getReceiverHandle().getStats();
			NetworkReport report = new NetworkReport();
			report.setStartTime(new Date().toString());
			report.setProcessorName("PCAP Network Statistics");
			report.setProbeId(ProbeConfiguration.probeId);
			report.addContent("PacketsCaptured", String.valueOf(statistics.getNumPacketsCaptured()));
			report.addContent("PacketsDropped", String.valueOf(statistics.getNumPacketsDropped()));
			report.addContent("PacketsDroppedByIf", String.valueOf(statistics.getNumPacketsDroppedByIf()));
			report.addContent("PacketsReceived", String.valueOf(statistics.getNumPacketsReceived()));
			DBUtil.getInstance().save(report);

		} catch (Exception e) {
			log.error("Couldn't obtain network statistics from PCAP. Reason {}", e.getCause());
		}
	}

	private void checkNetworkFilter() {
		String currentBPFFilter = monitor.getReceiverHandle().getFilteringExpression();
		//ProbeConfiguration.getInstance().get
	}
}