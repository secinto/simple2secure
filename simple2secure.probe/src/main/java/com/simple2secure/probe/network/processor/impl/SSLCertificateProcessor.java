package com.simple2secure.probe.network.processor.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pcap4j.packet.IpV4Packet.IpV4Header;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.Packet.Header;

import com.simple2secure.probe.network.PacketContainer;
import com.simple2secure.probe.network.PacketProcessor;

public class SSLCertificateProcessor extends PacketProcessor{

	private Set<byte[]> certificates;
	
	public SSLCertificateProcessor(String name, Map<String, String> options) {
		super(name, options);
		certificates = new HashSet<>();
	}

	@Override
	public PacketContainer processPacket() {
		//checking the port would be pointless, as it is perfectly possible to arbitrarily change it.
		
		Packet packet = this.packet.getPacket()/*Data Link Layer (Ethernet)	*/
				.getPayload()/*Network Layer (IP)*/
				.getPayload()/*Transport Layer (TCP, SCTP...)*/
				.getPayload()/*Application Layer (SSL)*/;
		
		byte[] buffer = packet.getRawData();
		
		//In order to evade detection, it is possible to put an arbitrary number of '0x15' or '0x00' bytes in front of the actual SSL record.
		//0x15 is the 'alert' type, and many SSL implementations will simply ignore it.
		int i;
		for(i=0;i<buffer.length;i++) {
			if(buffer[i]!=0x15&&buffer[i]!=0x00) break;
		}
		if(i!=0) buffer = Arrays.copyOfRange(buffer, i, buffer.length);
		
		if(//SSL 3.0 and TLS start with 0x16 to identify the SSL handshake.
				(buffer[0]==0x16 && (buffer[5]==0x02 || buffer[5]==0x0b))
				//SSL 2.0 doesn't have the 0x16 byte. Very rare, but could still be used.
				|| ((buffer[2]==0x02) || buffer[2]==0x0b)
				) {
			//Usually, the certificate is sent directly after the Server Hello (0x02) or on its own (0x0b),
			//yet this loop also works in case other data is sent
			int length = 0;
			while(length < buffer.length){
				//0x0b is the Certificate of the SSL Connection
				if(buffer[length+5] == 0x0b) {
					//the length of the certificate	
					int totalCertLength = length + (buffer[length + 9] << 16) +(buffer[length + 10] << 8) + buffer[length + 11]+12;
					int certLength = length+12;
					int oldLength;
					
					//A single SSL Connection can transmit multiple Certificates
					while(certLength < totalCertLength) {
						oldLength = certLength;
						certLength += (buffer[certLength] << 16) +(buffer[certLength + 1] << 8) + buffer[certLength + 2]+3;
						//The Certificate itself only starts on the 4th position
						certificates.add(Arrays.copyOfRange(buffer, oldLength+3, certLength));
					}
					
					
					//we can stop searching, since we found the certificate(s)!
					break;
				}
				
				//The length bytes are on the fourth and fifth byte themselves, meaning we need to add 5 bytes to get the total length
				length += (buffer[length + 3] << 8) + buffer[length + 4] + 5;
			}
		}
		
		return null;
	}

	public Set<byte[]> getCertificates(){
		return certificates;
	}
	
	@Override
	public void performAnalysis() {
		// TODO Auto-generated method stub
		
	}

}
