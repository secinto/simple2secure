package com.simple2secure.commons.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.crypto.KeyUtils;

public class TestKeyUtils {
	private static Logger log = LoggerFactory.getLogger(TestKeyUtils.class);

	@Test
	public void testGenerateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPair ecKeyPair = KeyUtils.generateKeyPair(192);
		assertNotNull(ecKeyPair);
	}

	@Test
	public void testGenerateSecretKey() throws NoSuchAlgorithmException {
		SecretKey secretKey = KeyUtils.generateSecretKey(256);
		assertNotNull(secretKey);
	}

	@Test
	public void testWriteKeyPairToFile() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPair ecKeyPair = KeyUtils.generateKeyPair(192);
		File publicKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPublic(), "licenses/public.key");
		assertNotNull(publicKeyFile);
		File privateKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPrivate(), "licenses/private.key");
		assertNotNull(privateKeyFile);
	}

	@Test
	public void testReadKeyPairToFile() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		KeyPair ecKeyPair = KeyUtils.generateKeyPair(192);
		File publicKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPublic(), "licenses/public.key");
		assertNotNull(publicKeyFile);
		File privateKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPrivate(), "licenses/private.key");
		assertNotNull(privateKeyFile);

		PrivateKey privateKey = KeyUtils.readPrivateKeyFromFile(privateKeyFile.getAbsolutePath());
		assertNotNull(privateKey);

		PublicKey publicKey = KeyUtils.readPublicKeyFromFile(publicKeyFile.getAbsolutePath());
		assertNotNull(publicKey);
	}

}
