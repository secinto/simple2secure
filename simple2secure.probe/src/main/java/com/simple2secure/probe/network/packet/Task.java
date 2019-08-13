package com.simple2secure.probe.network.packet;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;

public class Task implements Runnable {

	private PcapHandle handle;
	private PacketListener listener;
	private int count;

	public Task(PcapHandle handle, PacketListener listener, int count) {
		this.handle = handle;
		this.listener = listener;
		this.count = count;
	}

	@Override
	public void run() {
		try {
			handle.loop(count, listener);
		} catch (PcapNativeException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NotOpenException e) {
			e.printStackTrace();
		}
	}

}
