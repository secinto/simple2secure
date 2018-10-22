package com.simple2secure.api.model;

public class PacketInfo {

	private String destination_ip;
	private String source_ip;
	private String destination_mac;
	private String source_mac;
	private int length;
	private String protocol;

	public PacketInfo() {

	}

	public PacketInfo(String destination_ip, String source_ip, String destination_mac, String source_mac,
			int length, String protocol) {
		super();
		this.destination_ip = destination_ip;
		this.source_ip = source_ip;
		this.destination_mac = destination_mac;
		this.source_mac = source_mac;
		this.length = length;
		this.protocol = protocol;
	}

	public String getDestination_ip() {
		return destination_ip;
	}

	public void setDestination_ip(String destination_ip) {
		this.destination_ip = destination_ip;
	}

	public String getSource_ip() {
		return source_ip;
	}

	public void setSource_ip(String source_ip) {
		this.source_ip = source_ip;
	}

	public String getDestination_mac() {
		return destination_mac;
	}

	public void setDestination_mac(String destination_mac) {
		this.destination_mac = destination_mac;
	}

	public String getSource_mac() {
		return source_mac;
	}

	public void setSource_mac(String source_mac) {
		this.source_mac = source_mac;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
