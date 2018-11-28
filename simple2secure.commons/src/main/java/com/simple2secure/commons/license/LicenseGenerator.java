package com.simple2secure.commons.license;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.crypto.CryptoUtils;
import com.simple2secure.commons.crypto.KeyUtils;

public class LicenseGenerator {
	private static Logger log = LoggerFactory.getLogger(LicenseGenerator.class);

	public static String SIGNATURE_PROPERTY = "signature";
	public static String EXPIRATION_DATE_PROPERTY = "expirationDate";

	/**
	 * Generates a license using the provided properties as input for the license. If the {@value #EXPIRATION_DATE_PROPERTY} property is not
	 * available in the properties the generation fails. For generating the license the default key algorithm
	 * {@link KeyUtils#ASYMMETRIC_KEY_ALGORITHM}, which is <code>EC</code> and the default signature algorithm
	 * {@link CryptoUtils#SIGNATURE_ALGORITHM}, which is <code>SHA512withECDSA</code> are used. If the provided private key or the used
	 * algorithm are not available or do not match the provided key, no license is created. If the signature generation fails also null is
	 * returned.
	 *
	 * @param properties
	 *          The properties which are part of the license.
	 * @param privateKey
	 *          The private key which should be used to sign the license.
	 * @return The {@link License} object as generated.
	 */
	public static License generateLicense(Properties properties, PrivateKey privateKey) {
		try {
			String encoded = properties.toString();
			byte[] signature = CryptoUtils.sign(encoded, privateKey);
			String encodedSignature = Base64.getEncoder().encodeToString(signature);

			Properties orderedProperties = new OrderedProperties();
			orderedProperties.putAll(properties);
			orderedProperties.put(SIGNATURE_PROPERTY, encodedSignature);

			License license = new License(orderedProperties);

			return license;
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
			log.error("Couldn't create signature for license. Reason {}", e);
		}

		return null;
	}

	/**
	 * Generates a license using the provided properties as input for the license. If the {@value #EXPIRATION_DATE_PROPERTY} property is not
	 * available in the properties the generation fails. For generating the license the default key algorithm
	 * {@link KeyUtils#ASYMMETRIC_KEY_ALGORITHM}, which is <code>EC</code> and the default signature algorithm
	 * {@link CryptoUtils#SIGNATURE_ALGORITHM}, which is <code>SHA512withECDSA</code> are used. If the provided private key or the used
	 * algorithm are not available or do not match the provided key, no license is created. If the signature generation fails also null is
	 * returned.
	 *
	 * @param properties
	 *          The properties which are part of the license.
	 * @param outputStream
	 *          An output stream to which the license should be directly written after generation.
	 * @param privateKey
	 *          The private key which should be used to sign the license
	 * @return The {@link License} object as generated.
	 */
	public static License generateLicense(Properties properties, OutputStream outputStream, PrivateKey privateKey) {
		License license = generateLicense(properties, privateKey);
		if (license != null) {
			try {
				if (outputStream != null) {
					license.getProperties().store(outputStream, "License property file");
				} else {
					log.error("The provided output stream can't be used to write to!");
				}
			} catch (IOException e) {
				log.error("Couldn't write license to output stream. Reason {}", e);
			}
		}
		return license;
	}

	/**
	 * Generates a license using the provided properties as input for the license. If the {@value #EXPIRATION_DATE_PROPERTY} property is not
	 * available in the properties the generation fails. For generating the license the default key algorithm
	 * {@link KeyUtils#ASYMMETRIC_KEY_ALGORITHM}, which is <code>EC</code> and the default signature algorithm
	 * {@link CryptoUtils#SIGNATURE_ALGORITHM}, which is <code>SHA512withECDSA</code> are used. If the provided private key or the used
	 * algorithm are not available or do not match the provided key, no license is created. If the signature generation fails also null is
	 * returned.
	 *
	 * @param properties
	 *          The properties which are part of the license.
	 * @param outputStream
	 *          An output stream to which the license should be directly written after generation.
	 * @param privateKeyFile
	 *          The private key as file name which should be used to sign the license
	 * @return The {@link License} object as generated.
	 */
	public static License generateLicense(Properties properties, OutputStream outputStream, String privateKeyFile) {
		PrivateKey privateKey;
		try {
			privateKey = KeyUtils.readPrivateKeyFromFile(privateKeyFile);
			if (privateKey != null) {
				return generateLicense(properties, outputStream, privateKey);
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("Couldn't generate license because private key file {} couldn't be loaded. Reason {}", privateKeyFile, e);
		}
		return null;
	}

}
