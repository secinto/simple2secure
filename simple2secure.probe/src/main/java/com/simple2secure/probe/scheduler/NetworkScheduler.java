package com.simple2secure.probe.scheduler;

import java.util.Date;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;

import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.NetworkReport;
import com.simple2secure.commons.json.JSONUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.network.NetworkMonitor;
import com.simple2secure.probe.utils.DBUtil;
import com.simple2secure.probe.utils.ProbeUtils;

public class NetworkScheduler extends TimerTask {

	private static Logger log = LoggerFactory.getLogger(NetworkScheduler.class);
	private NetworkMonitor monitor;

	public NetworkScheduler(NetworkMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void run() {
		ProbeUtils.isServerReachable();
		getNetworkStatistics();
		checkNetworkFilter();
	}

	private void getNetworkStatistics() {
		try {
			PcapStat statistics = monitor.getReceiverHandle().getStats();
			NetworkReport report = new NetworkReport();
			report.setStartTime(new Date().toString());
			report.setProcessorName("PCAP Network Statistics");
			report.setProbeId(ProbeConfiguration.probeId);
			report.setGroupId(ProbeConfiguration.groupId);
			Map<String, String> content = new TreeMap<String, String>();
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
		String configBPFFilter = ProbeConfiguration.getInstance().getConfig().getBpfFilter();
		try {
			/*
			 * TODO: Verification of BPF expression must be made online during the creation. We assume that they are correct.
			 */
			if (!Strings.isNullOrEmpty(ProbeConfiguration.getInstance().getConfig().getBpfFilter())) {
				monitor.getReceiverHandle().setFilter(ProbeConfiguration.getInstance().getConfig().getBpfFilter(), BpfCompileMode.OPTIMIZE);
			}
		} catch (PcapNativeException e) {
			log.error("Couldn't apply BPF filter {} because some internal PCAP exception. Reason {}", configBPFFilter, e.getStackTrace());
		} catch (NotOpenException e) {
			log.error("Couldn't apply BPF filter {} because PCAP is not open. Reason {}", configBPFFilter, e.getStackTrace());
		} catch (Exception e) {
			log.error("Couldn't apply filter {} for reason {}", ProbeConfiguration.getInstance().getConfig().getBpfFilter(), e.getCause());
		}
	}
}