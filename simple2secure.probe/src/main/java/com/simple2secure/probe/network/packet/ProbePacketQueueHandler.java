package com.simple2secure.probe.network.packet;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;

import com.simple2secure.api.model.ProbePacket;
import com.simple2secure.commons.collections.ProcessingQueue;
import com.simple2secure.probe.utils.PacketUtil;
import com.simple2secure.probe.utils.PcapUtil;

public class ProbePacketQueueHandler implements Runnable {

	private ProcessingQueue<ProbePacket> probePacketQueue;
	private Timer timer = new Timer(true);
	private boolean isRunning = false;

	public ProbePacketQueueHandler() {
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			if (ProbePacketRequestHandler.dbHasChanged) {
				probePacketQueue = new ProcessingQueue<>();
				for (ProbePacket probeP : PacketUtil.getAllProbePacketsFromDB()) {
					probePacketQueue.push(probeP);
				}
				ProbePacketRequestHandler.dbHasChanged = false;
			}

			if (probePacketQueue != null && probePacketQueue.hasElement()) {
				ProbePacket probePFromQ = probePacketQueue.pop();

				Packet packet;
				try {
					packet = PcapUtil.convertHexStreamToPacket(probePFromQ.getPacketAsHexStream(), 0);
					timer.schedule(new PacketHandler(packet), TimeUnit.SECONDS.toMillis(20));
				} catch (IllegalRawDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					isRunning = false;
				}
			}
		}
	}
}
