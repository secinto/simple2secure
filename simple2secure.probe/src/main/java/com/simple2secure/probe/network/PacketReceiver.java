package com.simple2secure.probe.network;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.exceptions.NetworkException;
import com.simple2secure.probe.utils.LocaleHolder;

public class PacketReceiver implements PacketListener, Runnable {
	private static Logger log = LoggerFactory.getLogger(PacketReceiver.class);

	private PcapHandle handle;

	private boolean running = false;
	private ProcessingQueue<PacketContainer> processingQueue;

	public PacketReceiver(PcapHandle handle, ProcessingQueue<PacketContainer> processingQueue) {
		this.handle = handle;
		this.processingQueue = processingQueue;
	}

	@Override
	public void gotPacket(Packet packet) {
		try {
			processingQueue.push(new PacketContainer(packet, handle.getTimestamp().getTime()));
		} catch (NetworkException ne) {
			log.info("Received packet couldn't be parsed: " + ne.getMessage());
		}
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		try {
			running = true;
			handle.loop(-1, this);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(LocaleHolder.getMessage("interruption_occured").getMessage());
		} catch (PcapNativeException e) {
			log.error(LocaleHolder.getMessage("unspecified_pcap_native_error").getMessage());
		} catch (NotOpenException e) {
			log.error(LocaleHolder.getMessage("pcap_interface_open_error").getMessage());
		} finally {
			running = false;
		}
	}

	public void stop() {
		try {
			if (running) {
				handle.breakLoop();
			}
		} catch (NotOpenException e) {
			log.debug("PacketReceiver.close called although loop wasn't running!", e);
		}
	}

	public PcapHandle getHandle() {
		return handle;
	}

}
