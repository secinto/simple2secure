package com.simple2secure.commons.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoService {
	private static Logger log = LoggerFactory.getLogger(EchoService.class);

	public void startConsole() {

		InputStream is = null;
		BufferedReader br = null;

		try {

			is = System.in;
			br = new BufferedReader(new InputStreamReader(is));

			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.equalsIgnoreCase("exit")) {
					log.debug("Exiting service, received exit");
					break;
				}
				log.debug("Line entered {} ", line);
			}

		} catch (IOException ioe) {
			log.error("Exception while reading input. Error {} ", ioe);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ioe) {
				log.error("Exception while closing stream. Error {} ", ioe);
			}

		}

	}

	public void startServer(int portNumber) {
		log.debug("Starting EchoService on port {}", portNumber);
		try (ServerSocket serverSocket = new ServerSocket(portNumber);
				Socket clientSocket = serverSocket.accept();
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
			String inputLine;
			log.debug("Waiting for input!");

			while ((inputLine = in.readLine()) != null) {
				if (inputLine.equals("exit")) {
					break;
				}
				out.println(inputLine);
			}
		} catch (IOException e) {
			log.debug("Exception caught when trying to listen on port {} or listening for a connection. Error {}",
					portNumber, e);
		}
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			new EchoService().startConsole();
		} else if (args.length == 1 && StringUtils.isNumeric(args[0])) {
			new EchoService().startServer(Integer.parseInt(args[0]));
		}

	}
}