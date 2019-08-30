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
