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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.simple2secure.commons.crypto.CryptoUtils;
import com.simple2secure.commons.crypto.KeyUtils;
import com.simple2secure.commons.file.FileUtil;
import com.simple2secure.commons.file.ZIPUtils;

public class LicenseUtil {
	private static Logger log = LoggerFactory.getLogger(LicenseUtil.class);

	private static String workingDirectory = System.getProperty("user.dir");

	public static final String EXPIRATION_DATE_TAG = "expirationDate";
	public static final String LICENSE_ID_TAG = "licenseId";
	public static final String GROUP_ID = "groupId";

	private static boolean initialized = false;

	/**
	 * Specifies the storage location of the generated license files.
	 */
	public static String licenseFilePath = "license" + File.separator;

	/**
	 * The file name which is used for the license file.
	 */
	public static final String licenseFileName = "license.dat";
	/**
	 * The file name which should be used to identify a private key
	 */
	public static String privateKeyFileName = "private.key";
	/**
	 * The file name which should be used to identify a public key
	 */
	public static String publicKeyFileName = "public.key";
	/**
	 * Specifies the private key which is used to generate the signature
	 */
	public static String privateKeyFilePath = "private.key";
	/**
	 * Specifies the public key which is used to verify the signature
	 */
	public static String publicKeyFilePath = "public.key";

	/**
	 * Returns if the {@link LicenseUtil} has been initialized correctly and can be used without problems. If it has not been initialized
	 * before using any other function it tries to initialize it self, creating temporary keys and a temporary directory where it stores all
	 * data required to function.
	 *
	 * @return True if initialized, false otherwise.
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	private static void selfInitialize() {
		try {
			licenseFilePath = LicenseUtil.getLicensePath(licenseFilePath);
			KeyPair ecKeyPair = KeyUtils.generateKeyPair(192);
			File publicKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPublic(), licenseFilePath + publicKeyFilePath);
			File privateKeyFile = KeyUtils.writeKeyToFile(ecKeyPair.getPrivate(), licenseFilePath + privateKeyFilePath);

			publicKeyFilePath = publicKeyFile.getAbsolutePath();
			privateKeyFilePath = privateKeyFile.getAbsolutePath();

			LicenseUtil.initialize(licenseFilePath, privateKeyFilePath, publicKeyFilePath);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't self initialize license utils. Reason {}", e);
		}
	}

	/**
	 * Initializes the {@link LicenseUtil} with just the file path to store the created licenses, ZIP files and keys. Since no keys are
	 * provided the {@link LicenseUtil} generates a private and public key which are used to create the licenses. The keys are stored in the
	 * provided license file path.
	 *
	 * @param filePath
	 */
	public static void initialize(String filePath) {
		licenseFilePath = LicenseUtil.getLicensePath(filePath);
	}

	/**
	 * Initializes the {@link LicenseUtil} using the provided file path for storing the created licenses, private and public key. The paths
	 * are verified and modified if not valid. The used paths are then stored in {@value #licenseFilePath}, {@value #publicKeyFilePath}, and
	 * {@value #privateKeyFilePath}.
	 *
	 * @param filePath
	 *          The file path which should be used for storing the license files and keys.
	 * @param privateKey
	 *          The private key which should be used to create the signatures for the licenses.
	 * @param publicKey
	 *          The public key which is associated with the private key and can be used to verify the signatures.
	 */
	public static void initialize(String filePath, String privateKey, String publicKey) {
		licenseFilePath = LicenseUtil.getLicensePath(filePath);

		publicKeyFilePath = LicenseUtil.getLicenseKeyPath(publicKey, licenseFilePath);

		if (!Strings.isNullOrEmpty(privateKey)) {
			privateKeyFilePath = LicenseUtil.getLicenseKeyPath(privateKey, licenseFilePath);
		} else {
			privateKeyFilePath = workingDirectory + File.separator + privateKeyFileName;
		}

		initialized = true;
	}

	/**
	 * Checks whether the provided path can be used as license path, where all the created licenses and the used keys can be stored. If the
	 * provided path can't be used or doesn't exist the working directory adding the provided path is verified if it can be used, if this is
	 * also not possible the working directory is used instead.
	 *
	 * @param path
	 *          The path which should be used as license storage path.
	 * @return The actual absolute license storage path which can be used.
	 */
	public static String getLicensePath(String path) {
		String checkedPath = System.getProperty("user.dir");

		File file = new File(path);

		if (file.exists() && file.isDirectory()) {
			return FileUtil.correctPathFormat(file.getAbsolutePath(), true);
		} else {
			if (FileUtil.isDirectory(checkedPath + File.separator + path)) {
				return FileUtil.correctPathFormat(checkedPath + File.separator + path, true);
			} else if (FileUtil.createFolder(checkedPath + File.separator + path)) {
				return FileUtil.correctPathFormat(checkedPath + File.separator + path, true);
			}
		}
		return FileUtil.correctPathFormat(checkedPath, true);
	}

	/**
	 * Checks whether the provided file path can be used to load the specified resource. First it is checked if the resource specifies a
	 * classpath resource, thereafter a normal file system resource and in the end it is tried to obtain the resource from the working
	 * directory using the specified path as relative path. If the resource could be located, it is loaded and copied to the provided
	 * localFilePath where all the license data is stored.can be used as license path, where all the created licenses and possibly the keys
	 * used can be stored. If the provided path can't be used or doesn't exist the working directory adding the provided path is verified if
	 * it can be used, if this is also not possible the working directory is used instead.
	 *
	 * @param keyPath
	 *          The path to the key as stored as file.
	 * @param localFilePath
	 *          The localFilePath where all the license data should be stored.
	 * @return The resulting key path for the specified resource.
	 */
	public static String getLicenseKeyPath(String keyPath, String localFilePath) throws IllegalArgumentException {
		ClassLoader classLoader = LicenseUtil.class.getClassLoader();
		URL keyURL = classLoader.getResource(keyPath);
		if (keyURL != null) {
			File file = new File(classLoader.getResource(keyPath).getFile());
			return FileUtil.copyFileToFolder(file, localFilePath);
		} else {
			File keyFile = new File(keyPath);
			if (keyFile.exists() && !keyFile.isDirectory()) {
				return FileUtil.copyFileToFolder(keyFile, localFilePath);
			} else {
				if (keyFile.isDirectory()) {
					if (FileUtil.fileOrFolderExists(keyFile.getAbsolutePath() + keyFile.getName())) {
						return FileUtil.copyFileToFolder(new File(keyFile.getAbsolutePath() + keyFile.getName()), localFilePath);
					}
				}
				if (FileUtil.fileOrFolderExists(workingDirectory + File.separator + keyFile.getName())) {
					return FileUtil.copyFileToFolder(new File(workingDirectory + File.separator + keyFile.getName()), localFilePath);
				}
			}
		}
		throw new IllegalArgumentException("Couldn't find provided key in path " + keyPath);
	}

	/**
	 * Generates a ZIP file from the license in the {@value #licenseFilePath} with the specified ID and and a public key with file name
	 * {@value #publicKeyFileName} which must exist in the working directory. The ZIP file is the complete license since without the
	 * public.key the license.dat file can't be verified.
	 *
	 * @param zipFile
	 *          The filename to which the generated license ZIP file should be written.
	 * @param licenseId
	 *          The Id of the license which should be used to generate the ZIP file.
	 * @throws IOException
	 *           Thrown if the ZIP file couldn't be created
	 */
	public static void generateLicenseZIPFromID(String zipFile, String licenseId) throws IOException {

		String licenseFile = licenseFilePath + licenseFileName;

		if (!Strings.isNullOrEmpty(licenseId)) {
			licenseFile = licenseFilePath + File.separator + licenseId + File.separator + licenseFileName;
		}

		generateLicenseZIPFromFile(licenseFile, publicKeyFilePath, zipFile);
	}

	/**
	 * Generates a ZIP file from the license in the {@value #licenseFilePath} with the specified ID and the specified publicKeyFile. The
	 * licenseId determines the sub folder which is used to store the license. The ZIP file is the complete license since without the
	 * public.key the license.dat file can't be verified.
	 *
	 * @param zipFile
	 *          The filename to which the generated license ZIP file should be written.
	 * @param licenseId
	 *          The Id of the license which should be used to generate the ZIP file.
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @throws IOException
	 *           Thrown if the ZIP file couldn't be created
	 */
	public static void generateLicenseZIPFromID(String zipFile, String licenseId, String publicKeyFile) throws IOException {

		String licenseFile = licenseFilePath + licenseFileName;

		if (!Strings.isNullOrEmpty(licenseId)) {
			licenseFile = licenseFilePath + File.separator + licenseId + File.separator + licenseFileName;
		}

		generateLicenseZIPFromFile(licenseFile, publicKeyFile, zipFile);
	}

	/**
	 * Generates a ZIP file from a license.data which is expected to exist in the working directory and a public key with file name
	 * {@value #publicKeyFileName} which also must exist in the working directory. The resulting ZIP file contains is the complete license
	 * data which is required to use the license.
	 *
	 * @param zipFile
	 *          The filename to which the generated license ZIP file should be written.
	 * @throws IOException
	 *           Thrown if the ZIP file couldn't be created
	 */
	public static void generateLicenseZIPFromFile(String zipFile) throws IOException {
		generateLicenseZIPFromFile(licenseFilePath + licenseFileName, publicKeyFilePath, zipFile);
	}

	/**
	 * Generates a ZIP file from a license.data which is expected to exist in the working directory and the specified public key. The
	 * resulting ZIP file contains is the complete license data which is required to use the license.
	 *
	 * @param publicKeyFile
	 *          The file name of the public key which should be packaged with the license.dat file.
	 * @param zipFile
	 *          The filename to which the generated license ZIP file should be written.
	 * @throws IOException
	 *           Thrown if the ZIP file couldn't be created
	 */
	public static void generateLicenseZIPFromFile(String publicKeyFile, String zipFile) throws IOException {
		generateLicenseZIPFromFile(licenseFilePath + licenseFileName, publicKeyFile, zipFile);
	}

	/**
	 * Generates a ZIP file from the provided licenseFile and publicKeyFile. The ZIP file is the complete license since without the public.key
	 * the license.dat file can't be verified.
	 *
	 * @param licenseFile
	 *          The license file itself.
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @param zipFile
	 *          The filename to which the generated license ZIP file should be written.
	 * @throws IOException
	 *           Thrown if the ZIP file couldn't be created
	 */
	public static void generateLicenseZIPFromFile(String licenseFile, String publicKeyFile, String zipFile) throws IOException {
		if (!initialized) {
			selfInitialize();
		}

		List<File> files = getLicenseFileList(licenseFile, publicKeyFile);

		ByteArrayOutputStream byteOutStream = ZIPUtils.createZIPStreamFromFiles(files);

		OutputStream outputStream = new FileOutputStream(zipFile);

		byteOutStream.writeTo(outputStream);
		outputStream.flush();
		outputStream.close();
	}

	/**
	 * Generates a {@link ByteArrayOutputStream} from the license in the {@value #licenseFilePath} with the specified ID and and a public key
	 * with file name {@value #publicKeyFileName} which must exist in the working directory. The data in the output stream is provided as
	 * compressed data using the ZIP algorithm.
	 *
	 * @param licenseId
	 *          The Id of the license which should be used to generate the output stream.
	 * @return The ZIP content as {@link ByteArrayOutputStream}
	 * @throws IOException
	 *           Thrown if the ZIP file stream couldn't be created
	 */
	public static ByteArrayOutputStream generateLicenseZIPStreamFromID(String licenseId) throws IOException {
		return generateLicenseZIPStreamFromFile(licenseId, publicKeyFilePath);
	}

	/**
	 * Generates a {@link ByteArrayOutputStream} from the license in the {@value #licenseFilePath} with the specified ID and and a public key
	 * with file name {@value #publicKeyFileName} which must exist in the working directory. The data in the output stream is provided as
	 * compressed data using the ZIP algorithm.
	 *
	 * @param licenseId
	 *          The Id of the license which should be used to generate the output stream.
	 * @return The ZIP content as {@link ByteArrayOutputStream}
	 * @throws IOException
	 *           Thrown if the ZIP file stream couldn't be created
	 */
	public static ByteArrayOutputStream generateLicenseZIPStreamFromID(String licenseId, String publicKeyFile) throws IOException {
		String licenseFile = licenseFilePath + licenseFileName;

		if (!Strings.isNullOrEmpty(licenseId)) {
			licenseFile = licenseFilePath + File.separator + licenseId + File.separator + licenseFileName;
		}
		return generateLicenseZIPStreamFromFile(licenseFile, publicKeyFile);
	}

	/**
	 * Generates a {@link ByteArrayOutputStream} which represents the specified license and a public key with file name
	 * {@value #publicKeyFileName} which must exist in the working directory. The data in the output stream is provided as compressed data
	 * using the ZIP algorithm.
	 *
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @return The ZIP content as {@link ByteArrayOutputStream}
	 * @throws IOException
	 *           Thrown if the ZIP file stream couldn't be created
	 */
	public static ByteArrayOutputStream generateLicenseZIPStreamFromFile(String licenseFile) throws IOException {
		return generateLicenseZIPStreamFromFile(licenseFile, publicKeyFilePath);
	}

	/**
	 * Generates a {@link ByteArrayOutputStream} which represents the provided licenseFile and publicKeyFile as compressed data using the ZIP
	 * algorithm.
	 *
	 * @param licenseFile
	 *          The license file itself.
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @return The ZIP content as {@link ByteArrayOutputStream}
	 * @throws IOException
	 *           Thrown if the ZIP file stream couldn't be created
	 */
	public static ByteArrayOutputStream generateLicenseZIPStreamFromFile(String licenseFile, String publicKeyFile) throws IOException {
		return ZIPUtils.createZIPStreamFromFiles(getLicenseFileList(licenseFile, publicKeyFile));
	}

	/**
	 * Checks if the provided List of files contains the license.dat and public.key as required for default settings.
	 *
	 * @param List<File>
	 *          licenseDir
	 * @return true if the list of files contains the correct files, false otherwise.
	 */
	public static boolean checkLicenseDirValidity(List<File> licenseDir) {
		if (!initialized) {
			selfInitialize();
		}

		boolean isValidLicenseDir = false;
		boolean licenseFile = false;
		boolean publicKeyFile = false;

		if (licenseDir == null || licenseDir.size() <= 1) {
			return isValidLicenseDir;
		}

		for (File file : licenseDir) {
			if (file.getName().equals(licenseFileName)) {
				licenseFile = true;
			} else if (file.getName().equals(publicKeyFileName)) {
				publicKeyFile = true;
			}
		}

		if (licenseFile && publicKeyFile) {
			isValidLicenseDir = true;
		}

		return isValidLicenseDir;
	}

	/**
	 * Returns the {@link License} object using the specified license file and public key file from the list. The license is expected to be
	 * named {@value #licenseFileName} and the public key is expected to be named {@link #publicKeyFileName}, otherwise null is returned.
	 *
	 * @param licenseFiles
	 *          The List of license files containing at least license.dat and public key.
	 * @return The {@link License} object if the license could be read and verified.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws SignatureException
	 *           Thrown if an error occurred during signature verification
	 * @throws InvalidKeyException
	 *           Thrown if the provided public key is not compliant with the algorithm.
	 * @throws IOException
	 *           Thrown if reading the license file or public key from the file system fails.
	 * @throws InvalidKeySpecException
	 *           Thrown if the provided public key doesn't comply with the required specification of the key algorithm
	 */
	public static License getLicense(List<File> licenseFiles)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		if (!initialized) {
			selfInitialize();
		}

		String licenseFile = null;
		String publicKeyFile = null;
		if (licenseFiles == null || licenseFiles.size() <= 1) {
			return null;
		}

		for (File file : licenseFiles) {
			if (file.getName().equals(licenseFileName)) {
				licenseFile = file.getAbsolutePath();
			} else if (file.getName().equals(publicKeyFileName)) {
				publicKeyFile = file.getAbsolutePath();
			}
		}

		if (licenseFile != null && publicKeyFile != null) {
			return getLicense(licenseFile, publicKeyFile);
		}
		return null;

	}

	/**
	 * Returns a List of files containing the specified license and public key. If the files do not exist an exception is thrown.
	 *
	 * @return The List<File> containing license.dat and public.key.
	 * @throws IOException
	 */
	public static List<File> getLicenseFileList(String licenseFile, String publicFile) throws IOException {
		if (!initialized) {
			selfInitialize();
		}

		ArrayList<File> files = new ArrayList<>();

		File publicKey = new File(publicFile);

		if (!publicKey.exists() || publicKey.isDirectory()) {
			throw new IOException("Provided public key file " + publicKey.getAbsolutePath() + " doesn't exist or is a directory.");
		}

		File license = new File(licenseFile);

		if (!license.exists() || license.isDirectory()) {
			throw new IOException("Provided license file " + license.getAbsolutePath() + " doesn't exist or is a directory.");
		}

		files.add(publicKey);
		files.add(license);

		return files;
	}

	/**
	 * Returns the {@link License} object using the specified license file. For verifying the license file the default public key from path
	 * {@value #publicKeyFilePath} is used.
	 *
	 * @param licenseFile
	 *          The license file itself.
	 * @return The {@link License} object if the license could be read and verified.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws SignatureException
	 *           Thrown if an error occurred during signature verification
	 * @throws InvalidKeyException
	 *           Thrown if the provided public key is not compliant with the algorithm.
	 * @throws IOException
	 *           Thrown if reading the license file or public key from the file system fails.
	 * @throws InvalidKeySpecException
	 *           Thrown if the provided public key doesn't comply with the required specification of the key algorithm
	 */
	public static License getLicense(File licenseFile)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		return getLicense(licenseFile, publicKeyFilePath);
	}

	/**
	 * Returns the {@link License} object using the specified license file name. For verifying the license file the default public key from
	 * path {@value #publicKeyFilePath} is used.
	 *
	 * @param licenseFile
	 *          The license file itself.
	 * @return The {@link License} object if the license could be read and verified.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws SignatureException
	 *           Thrown if an error occurred during signature verification
	 * @throws InvalidKeyException
	 *           Thrown if the provided public key is not compliant with the algorithm.
	 * @throws IOException
	 *           Thrown if reading the license file or public key from the file system fails.
	 * @throws InvalidKeySpecException
	 *           Thrown if the provided public key doesn't comply with the required specification of the key algorithm
	 */
	public static License getLicense(String licenseFile)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		return getLicense(licenseFile, publicKeyFilePath);
	}

	/**
	 * Returns the {@link License} object using the specified license file name and public key.
	 *
	 * @param licenseFile
	 *          The license file itself.
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @return The {@link License} object if the license could be read and verified.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws SignatureException
	 *           Thrown if an error occurred during signature verification
	 * @throws InvalidKeyException
	 *           Thrown if the provided public key is not compliant with the algorithm.
	 * @throws IOException
	 *           Thrown if reading the license file or public key from the file system fails.
	 * @throws InvalidKeySpecException
	 *           Thrown if the provided public key doesn't comply with the required specification of the key algorithm
	 */
	public static License getLicense(String licenseFile, String publicKeyFile)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		if (!initialized) {
			selfInitialize();
		}

		File file = new File(licenseFile);
		if (file.exists()) {
			return getLicense(file, publicKeyFile);
		} else {
			return null;
		}
	}

	/**
	 * Returns the {@link License} object using the specified license file and public key.
	 *
	 * @param licenseFile
	 *          The license file itself.
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @return The {@link License} object if the license could be read and verified.
	 * @throws NoSuchAlgorithmException
	 *           Thrown if the key algorithm is not available.
	 * @throws SignatureException
	 *           Thrown if an error occurred during signature verification
	 * @throws InvalidKeyException
	 *           Thrown if the provided public key is not compliant with the algorithm.
	 * @throws IOException
	 *           Thrown if reading the license file or public key from the file system fails.
	 * @throws InvalidKeySpecException
	 *           Thrown if the provided public key doesn't comply with the required specification of the key algorithm
	 */
	public static License getLicense(File licenseFile, String publicKeyFile)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		if (!initialized) {
			selfInitialize();
		}

		FileInputStream input = new FileInputStream(licenseFile);
		Properties properties = new OrderedProperties();
		properties.load(input);
		input.close();

		String signature = (String) properties.remove(LicenseGenerator.SIGNATURE_PROPERTY);
		String encoded = properties.toString();

		/*
		 * Close stream otherwise the file can't be deleted immediately afterwards.
		 */
		input.close();

		PublicKey publicKey = KeyUtils.readPublicKeyFromFile(publicKeyFile);

		if (!CryptoUtils.verify(encoded, signature.getBytes(), publicKey)) {
			return null;
		}

		return new License(properties);
	}

	/**
	 * Creates a license from the provided properties. The properties must at least contain a value for the tag {@link #EXPIRATION_DATE_TAG}
	 * otherwise the creation fails and returns <code>null</code>.
	 *
	 * @param properties
	 *          The properties which should be part of the license.
	 * @return The path to the license.dat file which was created from the provided properties or null.
	 */
	public static License createLicense(Properties properties) throws IOException {
		return createLicense(properties, privateKeyFilePath);
	}

	/**
	 * Creates a license file from the provided properties. The properties must at least contain a value for the tag
	 * {@link #EXPIRATION_DATE_TAG} otherwise the creation fails and returns <code>null</code>.
	 *
	 * @param properties
	 *          The properties which should be part of the license.
	 * @return The path to the license.dat file which was created from the provided properties or null.
	 * @throws IOException
	 *           Thrown if writing the license fails.
	 */
	public static String createLicenseFile(Properties properties) throws IOException {
		return createLicenseFile(properties, privateKeyFilePath);
	}

	/**
	 * Creates a license from the provided parameters and uses the default private key file for creating the signature.
	 *
	 * @param groupId
	 *          The groupId which should be used in the license.
	 * @param licenseId
	 *          The licenseId which should be used in the license.
	 * @param expirationDate
	 *          The expiration date which should be used for the license.
	 * @return The created {@link License} object.
	 * @throws Exception
	 */
	public static License createLicense(String groupId, String licenseId, String expirationDate) {
		return createLicense(groupId, licenseId, expirationDate, privateKeyFilePath);
	}

	/**
	 * Creates a license from the provided parameters and uses the default private key file for creating the signature.
	 *
	 * @param groupId
	 *          The groupId which should be used in the license.
	 * @param licenseId
	 *          The licenseId which should be used in the license.
	 * @param expirationDate
	 *          The expiration date which should be used for the license.
	 * @return The file name of the created license stored as file.
	 * @throws IOException
	 *           Thrown if the license couldn't be written to the file system.
	 */
	public static String createLicenseFile(String groupId, String licenseId, String expirationDate) throws IOException {
		return createLicenseFile(groupId, licenseId, expirationDate, privateKeyFilePath);
	}

	/**
	 * Creates a license from the provided parameters and uses the provided private key file for creating the signature.
	 *
	 * @param groupId
	 *          The groupId which should be used in the license.
	 * @param licenseId
	 *          The licenseId which should be used in the license.
	 * @param expirationDate
	 *          The expiration date which should be used for the license.
	 * @param privateKeyFile
	 *          The private key which should be used for the signature.
	 * @return The created {@link License} object.
	 */
	public static License createLicense(String groupId, String licenseId, String expirationDate, String privateKeyFile) {
		if (!initialized) {
			selfInitialize();
		}

		Properties properties = new OrderedProperties();
		properties.setProperty("expirationDate", expirationDate);
		properties.setProperty("groupId", groupId);
		properties.setProperty("licenseId", licenseId.toString());

		FileUtil.createFolder(licenseFilePath + licenseId + File.separator);

		File privateKey = new File(privateKeyFilePath);

		if (privateKey.exists()) {
			return LicenseGenerator.generateLicense(properties, privateKey);
		} else {
			log.error("Couldn't create License in folder {} because public key couldn't ne copied. ", licenseFilePath + licenseId + "/");
		}
		return null;
	}

	/**
	 * Creates a license from the provided parameters and uses the provided private key file for creating the signature.
	 *
	 * @param groupId
	 *          The groupId which should be used in the license.
	 * @param licenseId
	 *          The licenseId which should be used in the license.
	 * @param expirationDate
	 *          The expiration date which should be used for the license.
	 * @param privateKeyFile
	 *          The private key which should be used for the signature.
	 * @return The file name of the created license stored as file.
	 * @throws IOException
	 *           Thrown if the license couldn't be written to the file system.
	 */
	public static String createLicenseFile(String groupId, String licenseId, String expirationDate, String privateKeyFile)
			throws IOException {
		if (!initialized) {
			selfInitialize();
		}

		Properties properties = new OrderedProperties();
		properties.setProperty("expirationDate", expirationDate);
		properties.setProperty("groupId", groupId);
		properties.setProperty("licenseId", licenseId.toString());

		FileUtil.createFolder(licenseFilePath + licenseId + File.separator);

		File privateKey = new File(privateKeyFile);

		if (privateKey.exists()) {
			File licenseFile = new File(licenseFilePath + licenseId + File.separator + licenseFileName);
			FileOutputStream fos = new FileOutputStream(licenseFile);
			LicenseGenerator.generateLicense(properties, fos, privateKey);
			fos.flush();
			fos.close();
			return licenseFile.getAbsolutePath();
		} else {
			log.error("Couldn't create License in folder {} because public key couldn't ne copied. ", licenseFilePath + licenseId + "/");
		}
		return null;
	}

	/**
	 * Creates a license from the provided parameters and uses the provided private key file for creating the signature.
	 *
	 * @param properties
	 *          The properties which should be used to create the license.
	 * @param privateKeyFile
	 *          The private key which should be used for the signature.
	 * @return The file name of the created license stored as file.
	 */
	public static License createLicense(Properties properties, String privateKeyFile) {
		return createLicense(properties, new File(privateKeyFilePath));
	}

	/**
	 * Creates a license from the provided parameters and uses the provided private key file for creating the signature.
	 *
	 * @param properties
	 *          The properties which should be used to create the license.
	 * @param privateKeyFile
	 *          The private key which should be used for the signature.
	 * @return The file name of the created license stored as file.
	 */
	public static License createLicense(Properties properties, File privateKeyFile) {
		if (!initialized) {
			selfInitialize();
		}

		Properties orderProperties = new OrderedProperties(properties);
		if (orderProperties.containsKey(EXPIRATION_DATE_TAG)) {

			if (privateKeyFile.exists()) {
				return LicenseGenerator.generateLicense(orderProperties, privateKeyFile);
			} else {
				log.error("Couldn't create License in folder {} because public key couldn't ne copied. ", licenseFilePath + File.separator);
			}
		}
		return null;
	}

	/**
	 * Creates a license from the provided parameters and uses the provided private key file for creating the signature.
	 *
	 * @param properties
	 *          The properties which should be used to create the license.
	 * @param privateKey
	 *          The private key which should be used for the signature.
	 * @return The file name of the created license stored as file.
	 */
	public static License createLicense(Properties properties, PrivateKey privateKey) {
		if (!initialized) {
			selfInitialize();
		}

		Properties orderProperties = new OrderedProperties(properties);
		if (orderProperties.containsKey(EXPIRATION_DATE_TAG)) {
			return LicenseGenerator.generateLicense(orderProperties, privateKey);
		}
		return null;
	}

	/**
	 * Creates a license from the provided parameters and uses the provided private key file for creating the signature.
	 *
	 * @param groupId
	 *          The groupId which should be used in the license.
	 * @param licenseId
	 *          The licenseId which should be used in the license.
	 * @param expirationDate
	 *          The expiration date which should be used for the license.
	 * @param privateKeyFile
	 *          The private key which should be used for the signature.
	 * @return The file name of the created license stored as file.
	 * @throws IOException
	 *           Thrown if the license couldn't be written to the file system.
	 */
	public static String createLicenseFile(Properties properties, String privateKeyFile) throws IOException {
		if (!initialized) {
			selfInitialize();
		}

		Properties orderProperties = new OrderedProperties(properties);
		if (orderProperties.containsKey(EXPIRATION_DATE_TAG)) {

			FileUtil.createFolder(licenseFilePath + File.separator);

			File privateKey = new File(privateKeyFilePath);

			if (privateKey.exists()) {
				File licenseFile = new File(licenseFilePath + File.separator + licenseFileName);
				FileOutputStream fos = new FileOutputStream(licenseFile);
				LicenseGenerator.generateLicense(orderProperties, fos, privateKey);
				fos.flush();
				fos.close();
				return licenseFile.getAbsolutePath();
			} else {
				log.error("Couldn't create License in folder {} because public key couldn't ne copied. ", licenseFilePath + File.separator);
			}
		}
		return null;
	}

	/**
	 * Generates a random license Id which is a alpha-numeric String of length 20.
	 *
	 * @return
	 */
	public static String generateLicenseId() {
		return RandomStringUtils.randomAlphanumeric(20);
	}
}
