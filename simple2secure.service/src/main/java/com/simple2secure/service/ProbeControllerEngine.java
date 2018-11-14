package com.simple2secure.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ProbeControllerEngine implements Engine {
	private static final String VERSION = "0.1.0";
	private static final String NAME = "ProbeController Service";
	private static final String COMPLETE_NAME = NAME + ":" + VERSION;
	private boolean stopped = true;

	private ServerSocket serverSocket;

	@Override
	public boolean isStopped() {
		return stopped;
	}

	@Override
	public boolean start() {
		stopped = false;
		int portNumber = 8000;

		try {
			serverSocket = new ServerSocket(portNumber);

			Socket clientSocket = serverSocket.accept();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				out.println(inputLine);
			}
			out.close();
			serverSocket.close();
			stopped = true;
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
			if (!serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (Exception ex) {
					System.out.println("Couldn't close server socket!");
				}
			}
		}
		return true;
	}

	@Override
	public boolean stop() {
		stopped = true;
		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (Exception ex) {
				System.out.println("Couldn't close server socket!");
			}
		}
		return stopped;
	}

	@Override
	public String getName() {
		return COMPLETE_NAME;
	}
}
