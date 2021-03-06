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
package com.simple2secure.commons.license;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
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
	 * @param privateKeyFile
	 *          The file name of the private key which should be used to sign the license.
	 * @return The {@link License} object as generated.
	 * @throws InvalidKeySpecException
	 *           Thrown if the private key couldn't be loaded using the default key algorithm.
	 */
	public static License generateLicense(Properties properties, String privateKeyFile) {
		return generateLicense(properties, new File(privateKeyFile));
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
	 * @param privateKeyFile
	 *          The file name of the private key which should be used to sign the license.
	 * @return The {@link License} object as generated.
	 * @throws InvalidKeySpecException
	 *           Thrown if the private key couldn't be loaded using the default key algorithm.
	 */
	public static License generateLicense(Properties properties, File privateKeyFile) {
		try {
			return generateLicense(properties, KeyUtils.readPrivateKeyFromFile(privateKeyFile));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("Couldn't read private key from provided file. Reason {}", e);
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
	 * @param privateKey
	 *          The private key which should be used to sign the license.
	 * @return The {@link License} object as generated.
	 */
	public static License generateLicense(Properties properties, PrivateKey privateKey) {
		try {
			if (properties == null || privateKey == null) {
				return null;
			}

			final String encoded = properties.toString();
			final byte[] signature = CryptoUtils.sign(encoded, privateKey);
			final String encodedSignature = new String(signature);

			final Properties orderedProperties = new OrderedProperties();
			orderedProperties.putAll(properties);
			orderedProperties.put(SIGNATURE_PROPERTY, encodedSignature);

			final License license = new License(orderedProperties);

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
		final License license = generateLicense(properties, privateKey);
		if (license != null) {
			try {
				if (outputStream != null) {
					license.getProperties().store(outputStream, "License property file");
				} else {
					log.error("The provided output stream can't be used to write to!");
				}
			} catch (final IOException e) {
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
	 *          The private key as file object which should be used to sign the license
	 * @return The {@link License} object as generated.
	 */
	public static License generateLicense(Properties properties, OutputStream outputStream, File privateKeyFile) {
		try {
			return generateLicense(properties, outputStream, KeyUtils.readPrivateKeyFromFile(privateKeyFile));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("Couldn't generate license because private key file {} couldn't be loaded. Reason {}", privateKeyFile, e);
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
	 * @param privateKeyFile
	 *          The private key as file name which should be used to sign the license
	 * @return The {@link License} object as generated.
	 */
	public static License generateLicense(Properties properties, OutputStream outputStream, String privateKeyFile) {
		try {
			return generateLicense(properties, outputStream, KeyUtils.readPrivateKeyFromFile(privateKeyFile));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("Couldn't generate license because private key file {} couldn't be loaded. Reason {}", privateKeyFile, e);
		}
		return null;
	}

}
