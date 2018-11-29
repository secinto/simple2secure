package com.simple2secure.commons.crypto;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyUtils {

	private static Logger log = LoggerFactory.getLogger(KeyUtils.class);

	public static final String ASYMMETRIC_KEY_ALGORITHM = "EC";
	public static final String SYMMETRIC_KEY_ALGORITHM = "AES";
	private static KeyGenerator keyGenerator;
	private static KeyPairGenerator keyPairGenerator;

	private static void initAsymmetric(String algorithm) throws NoSuchAlgorithmException {
		log.debug("Creating asymmetric key generator for algorithm {}", algorithm);
		keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
	}

	private static void initSymmetric(String algorithm) throws NoSuchAlgorithmException {
		log.debug("Creating symmetric key generator for algorithm {}", algorithm);
		keyGenerator = KeyGenerator.getInstance(algorithm);
	}

	/**
	 * Generates a {@link KeyPair} using the specified length and the default key algorithm {@value #ASYMMETRIC_KEY_ALGORITHM}, which is
	 * Elliptic Curve (EC). Returns the {@link SecretKey} if successful.
	 *
	 * @param algorithm
	 *          The key algorithm which should be used to generate the {@link SecretKey}.
	 * @param keyLength
	 *          The length of the generated key.
	 * @return The {@link SecretKey} object which has been generated.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws NoSuchProviderException
	 *           Thrown if no provider is available for the provided algorithm
	 */
	public static KeyPair generateKeyPair(int keyLength) throws NoSuchAlgorithmException, NoSuchProviderException {
		return generateKeyPair(ASYMMETRIC_KEY_ALGORITHM, keyLength);
	}

	/**
	 * Generates a {@link KeyPair} using the specified length and the specified key algorithm. Returns the {@link SecretKey} if successful.
	 *
	 * @param algorithm
	 *          The key algorithm which should be used to generate the {@link SecretKey}.
	 * @param keyLength
	 *          The length of the generated key.
	 * @return The {@link SecretKey} object which has been generated.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws NoSuchProviderException
	 *           Thrown if no provider is available for the provided algorithm
	 */
	public static KeyPair generateKeyPair(String algorithm, int keyLength) throws NoSuchAlgorithmException, NoSuchProviderException {
		initAsymmetric(algorithm);
		log.debug("Creating asymmetric key pair for algorithm {} with length {}", keyPairGenerator.getProvider().getName(), keyLength);
		keyPairGenerator.initialize(keyLength);
		return keyPairGenerator.genKeyPair();
	}

	/**
	 * Generates a {@link SecretKey} using the specified length and the default key algorithm {@value #SYMMETRIC_KEY_ALGORITHM}, which is
	 * <code>AES</code>. Returns the {@link SecretKey} if successful.
	 *
	 * @param keyLength
	 *          The length of the generated key.
	 * @return The {@link SecretKey} object which has been generated.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 */
	public static SecretKey generateSecretKey(int keyLength) throws NoSuchAlgorithmException {
		return generateSecretKey(SYMMETRIC_KEY_ALGORITHM, keyLength);
	}

	/**
	 * Generates a {@link SecretKey} using the specified length and the specified key algorithm. Returns the {@link SecretKey} if successful.
	 *
	 * @param algorithm
	 *          The key algorithm which should be used to generate the {@link SecretKey}.
	 * @param keyLength
	 *          The length of the generated key.
	 * @return The {@link SecretKey} object which has been generated.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 */
	public static SecretKey generateSecretKey(String algorithm, int keyLength) throws NoSuchAlgorithmException {
		initSymmetric(algorithm);
		log.debug("Creating symmetric key for algorithm {} with length {}", keyGenerator.getProvider().getName(), keyLength);
		keyGenerator.init(keyLength);
		return keyGenerator.generateKey();
	}

	/**
	 * Writes the provided {@link Key} to the file system using the specified file name. Returns the {@link File} object of the created key if
	 * successful, otherwise null. The key is encoded using the default encoding, which is usually DER.
	 *
	 * @param key
	 *          The key which should be written to the specified file name.
	 * @param fileName
	 *          The file name of the key to be used.
	 * @return The {@link File} object if successful, otherwise null.
	 */
	public static File writeKeyToFile(Key key, String fileName) {
		try {
			File publicKeyFile = new File(fileName);

			if (!publicKeyFile.exists() && publicKeyFile.getParentFile().isDirectory() && !publicKeyFile.getParentFile().exists()) {
				if (!publicKeyFile.getParentFile().mkdirs()) {
					log.error("Couldn't create file {} for key export.", fileName);
					return null;
				}
			}

			FileUtils.writeByteArrayToFile(publicKeyFile, key.getEncoded());
			return publicKeyFile;
		} catch (Exception e) {
			log.error("Couldn't write file {} for key export. Reason {}", fileName, e);
		}
		return null;
	}

	/**
	 * Reads the private key from the file system using the specified file name creates a {@link PrivateKey} using the default algorithm
	 * {@value #ASYMMETRIC_KEY_ALGORITHM}, which is Elliptic Curve (EC). The key file is expected to be DER encoded, otherwise the operation
	 * fails.
	 *
	 * @param algorithm
	 *          The key algorithm to be used.
	 * @param fileName
	 *          The file name from which the {@link PrivateKey} is to be read.
	 * @return The {@link PrivateKey} if successful, otherwise null.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws InvalidKeySpecException
	 *           Thrown if the key doesn't match the algorithm.
	 */
	public static PrivateKey readPrivateKeyFromFile(String fileName) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return readPrivateKeyFromFile(ASYMMETRIC_KEY_ALGORITHM, fileName);
	}

	/**
	 * Reads the private key from the file system using the specified file name creates a {@link PrivateKey} using the specified algorithm.
	 * The key file is expected to be DER encoded, otherwise the operation fails.
	 *
	 * @param algorithm
	 *          The key algorithm to be used.
	 * @param fileName
	 *          The file name from which the {@link PrivateKey} is to be read.
	 * @return The {@link PrivateKey} if successful, otherwise null.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws InvalidKeySpecException
	 *           Thrown if the key doesn't match the algorithm.
	 */
	public static PrivateKey readPrivateKeyFromFile(String algorithm, String fileName)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		File keyToRead = new File(fileName);
		try {
			if (keyToRead != null && keyToRead.exists()) {
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(FileUtils.readFileToByteArray(keyToRead));
				KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
				PrivateKey key = keyFactory.generatePrivate(keySpec);
				return key;
			}
		} catch (IOException ioe) {
			log.error("Couldn't read file {} for key import. Reason {}", fileName, ioe);
		}
		return null;
	}

	/**
	 * Reads the private key from the file system using the specified file name creates a {@link PublicKey} using the default algorithm
	 * {@value #ASYMMETRIC_KEY_ALGORITHM}, which is Elliptic Curve (EC). The key file is expected to be DER encoded, otherwise the operation
	 * fails.
	 *
	 * @param algorithm
	 *          The key algorithm to be used.
	 * @param fileName
	 *          The file name from which the {@link PublicKey} is to be read.
	 * @return The {@link PublicKey} if successful, otherwise null.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws InvalidKeySpecException
	 *           Thrown if the key doesn't match the algorithm.
	 */
	public static PublicKey readPublicKeyFromFile(String fileName) throws InvalidKeySpecException, NoSuchAlgorithmException {
		return readPublicKeyFromFile(ASYMMETRIC_KEY_ALGORITHM, fileName);
	}

	/**
	 * Reads the private key from the file system using the specified file name creates a {@link PublicKey} using the specified algorithm. The
	 * key file is expected to be DER encoded, otherwise the operation fails.
	 *
	 * @param algorithm
	 *          The key algorithm to be used.
	 * @param fileName
	 *          The file name from which the {@link PublicKey} is to be read.
	 * @return The {@link PublicKey} if successful, otherwise null.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws InvalidKeySpecException
	 *           Thrown if the key doesn't match the algorithm.
	 */
	public static PublicKey readPublicKeyFromFile(String algorithm, String fileName)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		File keyToRead = new File(fileName);
		try {
			if (keyToRead != null && keyToRead.exists()) {

				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(FileUtils.readFileToByteArray(keyToRead));
				KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
				PublicKey key = keyFactory.generatePublic(keySpec);
				return key;
			}
		} catch (IOException ioe) {
			log.error("Couldn't read file {} for key import. Reason {}", fileName, ioe);
		}
		return null;
	}
}
