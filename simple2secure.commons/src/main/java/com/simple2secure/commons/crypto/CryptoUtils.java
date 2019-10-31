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
package com.simple2secure.commons.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoUtils {
	private static Logger log = LoggerFactory.getLogger(CryptoUtils.class);

	public static String SIGNATURE_ALGORITHM = "SHA512withECDSA";

	/**
	 * Verifies the Base64 encoded signature of the provided message using the {@link PublicKey}, the message and the default signature
	 * algorithm {@value #SIGNATURE_ALGORITHM} which is <code>SHA512withECDSA</code>. If the signature can be verified true is returned.
	 * Otherwise an exception is thrown.
	 *
	 * @param message
	 *          The message which matches the one used for creating the signature.
	 * @param signature
	 *          The signature obtained from the message
	 * @param publicKey
	 *          The public key associated with the signature
	 * @param algorithm
	 *          The signature algorithm which has been used.
	 * @return True if the verification is successful.
	 * @throws SignatureException
	 *           If the verification of the signature failed.
	 * @throws InvalidKeyException
	 *           If the public key doesn't match the algorithm.
	 * @throws NoSuchAlgorithmException
	 *           If the algorithm is not supported.
	 */
	public static boolean verify(String message, byte[] signature, PublicKey publicKey)
			throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		return verify(message, signature, publicKey, SIGNATURE_ALGORITHM);
	}

	/**
	 * Verifies the Base64 encoded signature of the provided message using {@link PublicKey}, the message, and the specified algorithm. If the
	 * signature can be verified true is returned. Otherwise an exception is thrown.
	 *
	 * @param message
	 *          The message which matches the one used for creating the signature.
	 * @param signature
	 *          The signature obtained from the message
	 * @param publicKey
	 *          The public key associated with the signature
	 * @param algorithm
	 *          The signature algorithm which has been used.
	 * @return True if the verification is successful.
	 * @throws SignatureException
	 *           If the verification of the signature failed.
	 * @throws InvalidKeyException
	 *           If the public key doesn't match the algorithm.
	 * @throws NoSuchAlgorithmException
	 *           If the algorithm is not supported.
	 */
	public static boolean verify(String message, byte[] signature, PublicKey publicKey, String algorithm)
			throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		Signature signatureAlgorithm = Signature.getInstance(algorithm);
		signatureAlgorithm.initVerify(publicKey);
		signatureAlgorithm.update(message.getBytes());

		log.debug("Signature encoded {}", new String(signature));

		byte[] decoded = Base64.getDecoder().decode(signature);

		log.debug("Signature decoded {}", new String(decoded));

		return signatureAlgorithm.verify(decoded);
	}

	/**
	 * Creates a signature from the provided message using the specified {@link PrivateKey} and the default signature algorithm
	 * {@value #SIGNATURE_ALGORITHM} which is <code>SHA512withECDSA</code>. If successful the signature is returned as Base64 encoded byte
	 * array.
	 *
	 * @param message
	 *          The message to create the signature from
	 * @param privateKey
	 *          The private key to be used in the signature algorithm
	 * @param algorithm
	 *          The signature algorithm to be used
	 * @return The signature as byte array if successful, otherwise null
	 * @throws SignatureException
	 *           Throw if during signature creation an error occurred.
	 * @throws InvalidKeyException
	 *           Thrown if the key doesn't match the signature algorithm
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the signature algorithm is not available
	 */
	public static byte[] sign(String message, PrivateKey privateKey)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		return sign(message, privateKey, SIGNATURE_ALGORITHM);
	}

	/**
	 * Creates a signature from the provided message using the specified {@link PrivateKey} and signature algorithm. If successful, the
	 * signature is returned as Base64 encoded byte array.
	 *
	 * @param message
	 *          The message to create the signature from
	 * @param privateKey
	 *          The private key to be used in the signature algorithm
	 * @param algorithm
	 *          The signature algorithm to be used
	 * @return The signature as byte array if successful, otherwise null
	 * @throws SignatureException
	 *           Throw if during signature creation an error occurred.
	 * @throws InvalidKeyException
	 *           Thrown if the key doesn't match the signature algorithm
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the signature algorithm is not available
	 */
	public static byte[] sign(String message, PrivateKey privateKey, String algorithm)
			throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
		Signature dsa = Signature.getInstance(algorithm);
		dsa.initSign(privateKey);
		dsa.update(message.getBytes());

		byte[] signature = dsa.sign();

		log.debug("Signature decoded {}", new String(signature));

		byte[] signatureEncoded = Base64.getEncoder().encode(signature);

		log.debug("Signature encoded {}", new String(signatureEncoded));

		return signatureEncoded;
	}

	/**
	 * Generates a SHA3-512 message digest from the provided content and returns the digest as byte array.
	 *
	 * @param content
	 *          The content to be hashed
	 * @return The hash value of the content.
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] generateSecureHash(byte[] content) throws NoSuchAlgorithmException {
		final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
		final byte[] hashbytes = digest.digest(content);
		return hashbytes;
	}

	/**
	 * Generates a SHA3-512 message digest from the provided content and returns the digest as string in HEX representation.
	 *
	 * @param content
	 *          The content to be hashed
	 * @return The hash value of the content.
	 * @throws NoSuchAlgorithmException
	 */
	public static String generateSecureHashHexString(String content) throws NoSuchAlgorithmException {
		return bytesToHex(generateSecureHash(content.getBytes(StandardCharsets.UTF_8)));
	}

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
}
