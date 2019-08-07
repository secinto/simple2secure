package com.simple2secure.commons.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple2secure.commons.crypto.CryptoUtils;
import com.simple2secure.commons.crypto.KeyUtils;

public class TestCryptoUtils {
	private static Logger log = LoggerFactory.getLogger(TestCryptoUtils.class);

	@Test
	public void testSign() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		KeyPair keyPair = KeyUtils.generateKeyPair(192);

		byte[] signature = CryptoUtils.sign("Das ist eine Nachricht", keyPair.getPrivate(), "SHA1withECDSA");
		assertNotNull(signature);
		signature = CryptoUtils.sign("Das ist eine Nachricht", keyPair.getPrivate(), "SHA256withECDSA");
		assertNotNull(signature);
		signature = CryptoUtils.sign("Das ist eine Nachricht", keyPair.getPrivate(), "SHA512withECDSA");
		assertNotNull(signature);
	}

	@Test
	public void testSignFailWrongAlgorithm()
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
		KeyPair keyPair = KeyUtils.generateKeyPair(192);

		Executable closureContainingCodeToTest = () -> {
			CryptoUtils.sign("Das ist eine Nachricht", keyPair.getPrivate(), "SHA1withEDSA");
		};

		assertThrows(NoSuchAlgorithmException.class, closureContainingCodeToTest);
	}

	@Test
	public void testSignFailIncorrectKey()
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, InvalidKeySpecException {
		KeyPair keyPair = KeyUtils.generateKeyPair("DSA", 1024);
		Executable closureContainingCodeToTest = () -> {
			CryptoUtils.sign("Das ist eine Nachricht", keyPair.getPrivate(), "SHA1withECDSA");
		};

		assertThrows(InvalidKeyException.class, closureContainingCodeToTest);
	}

	@Test
	public void testVerifySignature() throws Exception {
		KeyPair keyPair = KeyUtils.generateKeyPair(192);

		byte[] signature = CryptoUtils.sign("Das ist eine Nachricht", keyPair.getPrivate(), "SHA1withECDSA");

		assertTrue(CryptoUtils.verify("Das ist eine Nachricht", signature, keyPair.getPublic(), "SHA1withECDSA"));
	}

	@Test
	public void testVerifySignatureFail() throws Exception {
		KeyPair keyPair = KeyUtils.generateKeyPair(192);

		byte[] signature = CryptoUtils.sign("Das ist eine Nachricht", keyPair.getPrivate(), "SHA1withECDSA");

		assertFalse(CryptoUtils.verify("Das ist eine Nachrich", signature, keyPair.getPublic(), "SHA1withECDSA"));
	}

}
