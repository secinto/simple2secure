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
package com.simple2secure.probe.network;

import java.io.Serializable;
import java.util.UUID;

import org.pcap4j.packet.Packet;

import com.simple2secure.api.model.Processor;

public class PacketContainer implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3618774679600159967L;

	private Packet packet;

	private String id;

	private boolean packetInProcess = false;

	private int next;

	private Processor processor;

	private long timestamp;

	public PacketContainer(Packet packet, long timestamp) {
		this.packet = packet;
		this.timestamp = timestamp;
		/*
		 * Create a UUID for this packet in order to identify it later on.
		 */
		id = UUID.randomUUID().toString();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * public Vertex getVertex() { return vertex; }
	 *
	 * public void setVertex(Vertex vertex) { this.vertex = vertex; }
	 */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public boolean isPacketInProcess() {
		return packetInProcess;
	}

	public void setPacketInProcess(boolean packetInProcess) {
		this.packetInProcess = packetInProcess;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}
}
