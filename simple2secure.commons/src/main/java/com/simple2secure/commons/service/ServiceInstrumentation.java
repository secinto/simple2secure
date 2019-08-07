package com.simple2secure.commons.service;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.crypto.CryptoUtils;
import com.simple2secure.commons.license.License;

public class ServiceInstrumentation {
	private static Logger log = LoggerFactory.getLogger(ServiceInstrumentation.class);

	public static final String AUTHENTICATED_TAG = "AUTHENTICATED";
	private InputStream serviceOutput;

	private OutputStream serviceInput;

	private BufferedWriter serviceCommandWriter;

	private PrivateKey privateKey;
	private PublicKey publicKey;

	private PublicKey servicePublicKey;

	private String token;

	private boolean useSignatureAuthentication = true;
	private boolean useAuthentication = true;

	private boolean useFallbackNoSecurity = false;

	public ServiceInstrumentation(InputStream output, OutputStream input) {
		serviceOutput = output;
		serviceInput = input;
		serviceCommandWriter = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(serviceInput)));
	}

	/**
	 * Returns whether signature authentication is used or not.
	 *
	 * @return Boolean value of the current state
	 */
	public boolean isUseSignatureAuthentication() {
		return useSignatureAuthentication;
	}

	/**
	 * Specifies if signature authentication should be used for command authentication. As default it is used.
	 *
	 * @param useSignatureAuthentication
	 *          Boolean value to set signature authentication state
	 */
	public void setUseSignatureAuthentication(boolean useSignatureAuthentication) {
		this.useSignatureAuthentication = useSignatureAuthentication;
	}

	/**
	 * Returns the current state of using command authentication.
	 *
	 * @return Boolean value off the state.
	 */
	public boolean isUseAuthentication() {
		return useAuthentication;
	}

	/**
	 * Sets the value if authentication should be used for each command. Default this also implies to use signature authentication, which is
	 * set implicitly if <code>true</code> is provided as input.
	 *
	 * @param useAuthentication
	 *          True or False whether authentication should be used or not.
	 */
	public void setUseAuthentication(boolean useAuthentication) {
		this.useAuthentication = useAuthentication;
	}

	/**
	 * Returns if NO security is used as fallback.
	 *
	 * @return Boolean if NO security is used.
	 */
	public boolean isUseFallbackNoSecurity() {
		return useFallbackNoSecurity;
	}

	/**
	 * Sets the value for using NO security as fallback to the provided value. As default the value is set to false.
	 *
	 * @param useFallbackNoSecurity
	 *          True if NO security should be used as fallback.
	 */
	public void setUseFallbackNoSecurity(boolean useFallbackNoSecurity) {
		this.useFallbackNoSecurity = useFallbackNoSecurity;
	}

	/**
	 * Returns the public key which is attached to the command sent to the service, if signature authentication is configured. As default
	 * token authentication is used, if a token is provided and authentication is enabled.
	 *
	 * @return
	 */

	public PublicKey getPublicKey() {
		return publicKey;
	}

	/**
	 * Returns the public key which is attached to the command sent to the service, if signature authentication is configured. As default
	 * token authentication is used if a token is provided and authentication is enabled.
	 *
	 * @param publicKey
	 */
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * Returns the private key which is used to generate a signature, if signature authentication is configured. As default token
	 * authentication is used, if a token is provided and authentication is enabled.
	 *
	 * @return
	 */
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * Sets the private key which is used to generate a signature, if signature authentication is configured. As default token authentication
	 * is used, if a token is provided and authentication is enabled.
	 *
	 * @param privateKey
	 */
	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * Returns the public key which is used by the service to authenticate its sensitive data.
	 *
	 * @return The {@link PublicKey} used to verify the service and the received data's authenticity.
	 */
	public PublicKey getServicePublicKey() {
		return servicePublicKey;
	}

	/**
	 * Sets the public which should be used to verify the authenticity of the service and the received data.
	 *
	 * @param servicePublicKey
	 *          The {@link PublicKey} which should be used to verify the authenticity.
	 */
	public void setServicePublicKey(PublicKey servicePublicKey) {
		this.servicePublicKey = servicePublicKey;
	}

	/**
	 * Returns the currently used token which used for authentication.
	 *
	 * @return The current token.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the token which should be used for authentication.
	 *
	 * @param license
	 *          The {@link License} to use.
	 */
	public void setToken(String token) {
		this.token = token;
	}

	public InputStream getServiceOutput() {
		return serviceOutput;
	}

	public void setServiceOutput(InputStream serviceOutput) {
		this.serviceOutput = serviceOutput;
	}

	public OutputStream getServiceInput() {
		return serviceInput;
	}

	public void setServiceInput(OutputStream serviceInput) {
		this.serviceInput = serviceInput;
	}

	/**
	 * Sends the provided command to the connected service input stream. The provided command must be of type {@link ServiceCommands} and the
	 * array of arguments can have any content. The arguments are provided to the connected service as joined string using a blank character.
	 *
	 * @param command
	 *          The command which should be sent to the connected service.
	 * @throws IOException
	 *           Thrown if writing to the output stream fails.
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	public synchronized void sendCommand(ServiceCommand command)
			throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		/*
		 * TODO: Add some authentication mechanism which allows to verify the sender authenticity of the sender at the service.
		 */
		boolean authenticationStatus = true;
		String commandToSend = command.getCommand() + " " + String.join(" ", command.getArguments());
		if (useAuthentication) {
			if (useSignatureAuthentication && privateKey != null) {
				try {
					byte[] signedCommand = CryptoUtils.sign(commandToSend, privateKey);
					commandToSend = new String(signedCommand, "UTF-8");
				} catch (Exception e) {
					log.error("Couldn't sign command. Reason {}", e);
					if (useFallbackNoSecurity) {
						log.info("Proceeding with fallback NO security");
						authenticationStatus = false;
					}
				}
			} else {
				commandToSend = commandToSend + " " + token;
			}
			commandToSend = AUTHENTICATED_TAG + " " + commandToSend;
		}
		if (authenticationStatus) {
			serviceCommandWriter.write(commandToSend);
		}

	}
}
