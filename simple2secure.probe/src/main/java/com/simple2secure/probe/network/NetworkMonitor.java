package com.simple2secure.probe.network;

import java.util.List;

import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapIpV4Address;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.api.model.Config;
import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.commons.config.StaticConfigItems;
import com.simple2secure.commons.network.NetUtils;
import com.simple2secure.probe.config.ProbeConfiguration;
import com.simple2secure.probe.exceptions.NetworkException;
import com.simple2secure.probe.exceptions.ProbeException;
import com.simple2secure.probe.utils.LocaleHolder;
import com.simple2secure.probe.utils.PcapUtil;

public class NetworkMonitor {

	private static Logger log = LoggerFactory.getLogger(NetworkMonitor.class);

	private static NetworkMonitor instance;

	private List<PcapNetworkInterface> interfaces;
	private PcapNetworkInterface singleInterface;
	private PacketReceiver receiver;
	private PacketProcessorFSM packetProcessor;

	private ProcessingQueue<PacketContainer> processingQueue;
	private PcapHandle receiverHandle;
	private PcapHandle senderHandle;

	public static NetworkMonitor startMonitor() {
		if (instance == null) {
			instance = new NetworkMonitor();
		}
		return instance;
	}

	private NetworkMonitor() {
		initMonitor();
	}

	private void initMonitor() {
		try {
			interfaces = Pcaps.findAllDevs();
		} catch (PcapNativeException e1) {
			throw new ProbeException(LocaleHolder.getMessage("pcap_no_interfaces"));
		}

		if (interfaces == null || interfaces.isEmpty()) {
			throw new ProbeException(LocaleHolder.getMessage("pcap_no_interfaces"));
		}

		Config configuration = ProbeConfiguration.getInstance().getCurrentConfigObj();

		boolean show = configuration.isShow_interfaces();

		String previousAddress = "";

		boolean use_iface = configuration.isUse_configured_iface();
		if (use_iface) {
			int config = ProbeConfiguration.getInstance().getCurrentConfigObj().getInterface_number();
			int iface = config;
			if (iface < interfaces.size()) {
				singleInterface = interfaces.get(iface);
			}
		}

		if (singleInterface == null) {

			for (int i = 0; i < interfaces.size(); i++) {
				PcapNetworkInterface currentInterface = interfaces.get(i);
				if (show) {
					log.info(i + ":" + currentInterface.getName() + "(" + currentInterface.getDescription() + ")");
				}
				/*
				 * Iterate through the addresses of the interfaces and check if someone fits. TODO: We should store the interfaces which have
				 * relevant addresses.
				 */
				List<PcapAddress> addresses = interfaces.get(i).getAddresses();
				for (PcapAddress address : addresses) {
					if (address instanceof PcapIpV4Address) {
						String ipAddress = ((PcapIpV4Address) address).getAddress().getHostAddress();
						if (NetUtils.isUseableIPv4Address(ipAddress) && PcapUtil.checkAddress(ipAddress)) {
							if (singleInterface != null) {
								log.info("Found another usable address {}, discarding old one {}", ipAddress, previousAddress);
							}
							singleInterface = currentInterface;
							previousAddress = ipAddress;
						}
					}
				}
			}
		}

		if (singleInterface == null) {
			throw new NetworkException(LocaleHolder.getMessage("no_usable_address"));
		}

		try {
			receiverHandle = singleInterface.openLive(StaticConfigItems.SNAPLEN, PromiscuousMode.PROMISCUOUS, StaticConfigItems.READ_TIMEOUT);

			/*
			 * TODO: Verify if this setting works and is correctly applied. A verification for inconsistent or incorrect BPF filter strings must
			 * be developed
			 */
			if (!Strings.isNullOrEmpty(ProbeConfiguration.getInstance().getConfig().getBpfFilter())) {
				try {
					receiverHandle.setFilter(ProbeConfiguration.getInstance().getConfig().getBpfFilter(), BpfCompileMode.OPTIMIZE);
				} catch (Exception e) {
					log.error("Couldn't apply filter {} for reason {}", ProbeConfiguration.getInstance().getConfig().getBpfFilter(), e.getCause());
				}
			}
			processingQueue = new ProcessingQueue<PacketContainer>();

			receiver = new PacketReceiver(receiverHandle, processingQueue);
			packetProcessor = new PacketProcessorFSM(processingQueue);

			new Thread(packetProcessor).start();
			new Thread(receiver).start();

		} catch (Exception e) {
			if (receiver != null) {
				receiver.stop();
			}
			if (packetProcessor != null) {
				packetProcessor.stop();
			}
			throw new NetworkException(LocaleHolder.getMessage("pcap_interface_open_error"));
		}

	}

	public void stop() {
		if (receiver != null) {
			receiver.stop();
		}
		if (packetProcessor != null) {
			packetProcessor.stop();
		}
		if (instance != null) {
			instance = null;
		}
	}

	public PacketReceiver getReceiver() {
		return receiver;
	}

	public PcapHandle getReceiverHandle() {
		return receiverHandle;
	}

}
