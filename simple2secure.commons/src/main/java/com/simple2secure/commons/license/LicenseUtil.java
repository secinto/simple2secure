package com.simple2secure.commons.license;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.PublicKey;
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

	/**
	 * Specifies the storage location of the generated license files.
	 */
	public static String licenseFilePath = "license/";
	/**
	 * Specifies the private key which is used to generate the signature
	 */
	public static String privateKeyFilePath = "private.key";
	/**
	 * Specifies the public key which is used to verify the signature
	 */
	public static String publicKeyFilePath = "public.key";

	public static void initialize(String filePath, String publicKey) {
		initialize(filePath, null, publicKey);
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
			privateKeyFilePath = "private.key";
		}
	}

	/**
	 * Checks whether the provided path can be used as license path, where all the created licenses and possibly the keys used can be stored.
	 * If the provided path can't be used or doesn't exist the working directory adding the provided path is verified if it can be used, if
	 * this is also not possible the working directory is used instead.
	 *
	 * @param path
	 *          The path which should be used as license storage path.
	 * @return The actual absolute license storage path which can be used.
	 */
	public static String getLicensePath(String path) {
		String checkedPath = System.getProperty("user.dir");
		if (FileUtil.isDirectory(path)) {
			try {
				checkedPath = FileUtil.getFile(path).getAbsolutePath();
			} catch (Exception ioe) {
				log.error("Couldn't verify license file path. Reason {}", ioe);
			}
		} else {
			if (FileUtil.isDirectory(checkedPath + "\\" + path)) {
				return FileUtil.correctPathFormat(checkedPath + "\\" + path, true);
			} else if (FileUtil.createFolder(checkedPath + "\\" + path)) {
				return FileUtil.correctPathFormat(checkedPath + "\\" + path, true);
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
	public static String getLicenseKeyPath(String keyPath, String localFilePath) {
		ClassLoader classLoader = LicenseUtil.class.getClassLoader();
		URL keyURL = classLoader.getResource(keyPath);
		if (keyURL != null) {
			File file = new File(classLoader.getResource(keyPath).getFile());
			if (FileUtil.copyToFolder(file, localFilePath)) {
				return FileUtil.correctPathFormat(localFilePath + file.getName(), false);
			} else {
				return FileUtil.correctPathFormat(file.getAbsolutePath(), false);
			}
		} else {
			if (FileUtil.fileOrFolderExists(keyPath)) {
				return FileUtil.correctPathFormat(keyPath, false);
			} else {
				if (FileUtil.fileOrFolderExists(workingDirectory + "\\" + keyPath)) {
					return FileUtil.correctPathFormat(workingDirectory + "\\" + keyPath, false);
				}
				throw new RuntimeException("Couldn't find provided key in path " + keyPath);
			}
		}
	}

	/**
	 * Generates a ZIP file from the default license (working directory license.dat file) and public key (working directory public.key file).
	 * The ZIP file is the complete license since without the public.key, the license.dat file can't be verified.
	 *
	 * @param zipFile
	 *          The filename to which the generated license ZIP file should be written.
	 * @throws IOException
	 */
	public static void generateLicenseZIPFile(String zipFile) throws IOException {
		generateLicenseZIPFile(publicKeyFilePath, zipFile);
	}

	/**
	 * Generates a ZIP file from the default license (working directory license.dat file) and the publicKeyFile. The ZIP file is the complete
	 * license since without the public.key the license.dat file can't be verified.
	 *
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @param zipFile
	 *          The filename to which the generated license ZIP file should be written.
	 * @throws IOException
	 */
	public static void generateLicenseZIPFile(String publicKeyFile, String zipFile) throws IOException {
		generateLicenseZIPFile(licenseFilePath, publicKeyFile, zipFile);
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
	 */
	public static void generateLicenseZIPFile(String licenseFile, String publicKeyFile, String zipFile) throws IOException {

		List<File> files = getLicenseFileList();
		ByteArrayOutputStream byteOutStream = ZIPUtils.createZIPStreamFromFiles(files);

		OutputStream outputStream = new FileOutputStream(zipFile);

		byteOutStream.writeTo(outputStream);
	}

	public static List<File> getLicenseFileList() {
		return getLicenseFileList(licenseFilePath);
	}

	/**
	 * Returns a List of files containing the default license (working directory license.dat file) and public key (working directory
	 * public.key file).
	 *
	 * @return The List<File> containing license.dat and public.key.
	 */
	public static List<File> getLicenseFileList(String licenseFile) {
		ArrayList<File> files = new ArrayList<>();

		File publicKey = new File(publicKeyFilePath);
		File certificate = null;
		if (!Strings.isNullOrEmpty(licenseFile)) {
			certificate = new File(licenseFile);
		} else {
			certificate = new File(licenseFilePath + "/license.dat");
		}

		files.add(publicKey);
		files.add(certificate);

		return files;
	}

	/**
	 * Generates a {@link ByteArrayOutputStream} which represents the default license (working directory license.dat file) and public key
	 * (working directory public.key file) as compressed data using the ZIP algorithm.
	 *
	 * @return The ZIP content as {@link ByteArrayOutputStream}
	 * @throws IOException
	 */
	public static ByteArrayOutputStream generateLicenseZIPStream() throws IOException {
		return generateLicenseZIPStream(publicKeyFilePath);
	}

	/**
	 * Generates a {@link ByteArrayOutputStream} which represents the default license (working directory license.dat file) and provided public
	 * key as compressed data using the ZIP algorithm.
	 *
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @return The ZIP content as {@link ByteArrayOutputStream}
	 * @throws IOException
	 */
	public static ByteArrayOutputStream generateLicenseZIPStream(String publicKeyFile) throws IOException {
		return generateLicenseZIPStream(licenseFilePath, publicKeyFile);
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
	 */
	public static ByteArrayOutputStream generateLicenseZIPStream(String licenseFile, String publicKeyFile) throws IOException {
		return ZIPUtils.createZIPStreamFromFiles(getLicenseFileList(licenseFile));
	}

	/**
	 * Checks if the provided List of files contains the license.dat and public.key as required for default settings.
	 *
	 * @param List<File>
	 *          licenseDir
	 * @return true if the list of files contains the correct files, false otherwise.
	 */
	public static boolean checkLicenseDirValidity(List<File> licenseDir) {
		boolean isValidLicenseDir = false;
		boolean licenseFile = false;
		boolean publicKeyFile = false;

		for (File file : licenseDir) {
			if (file.getName().equals("license.dat")) {
				licenseFile = true;
			} else if (file.getName().equals("public.key")) {
				publicKeyFile = true;
			}
		}

		if (licenseFile && publicKeyFile) {
			isValidLicenseDir = true;
		}

		return isValidLicenseDir;
	}

	/**
	 * Returns the {@link License} object from the default license location (working directory license.dat file) and public key (working
	 * directory public.key file).
	 *
	 * @return The {@link License} object from the default license location.
	 * @throws Exception
	 */
	public static License getLicense() throws Exception {
		return getLicense(publicKeyFilePath);
	}

	/**
	 *
	 * @param publicKeyFile
	 * @return
	 * @throws Exception
	 */
	public static License getLicense(String publicKeyFile) throws Exception {
		return getLicense(licenseFilePath, publicKeyFile);
	}

	/**
	 * Returns the {@link License} object using the specified license file and public key.
	 *
	 * @param licenseFile
	 *          The license file itself.
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @return
	 * @throws Exception
	 */
	public static License getLicense(String licenseFile, String publicKeyFile) throws Exception {
		FileInputStream input = new FileInputStream(licenseFile);
		Properties properties = new OrderedProperties();
		properties.load(input);

		String signature = (String) properties.remove(LicenseGenerator.SIGNATURE_PROPERTY);
		String encoded = properties.toString();

		PublicKey publicKey = KeyUtils.readPublicKeyFromFile(publicKeyFile);

		if (!CryptoUtils.verify(encoded, signature.getBytes(), publicKey)) {
			return null;
		}

		return new License(properties);
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
	 * @throws Exception
	 */
	public static String createLicense(String groupId, String licenseId, String expirationDate) throws Exception {
		return createLicense(groupId, licenseId, expirationDate, privateKeyFilePath);
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
	 * @throws Exception
	 *           Thrown if something goes wrong.
	 */
	public static String createLicense(String groupId, String licenseId, String expirationDate, String privateKeyFile) throws Exception {

		Properties properties = new OrderedProperties();
		properties.setProperty("expirationDate", expirationDate);
		properties.setProperty("groupId", groupId);
		properties.setProperty("licenseId", licenseId.toString());

		FileUtil.createFolder(licenseFilePath + "\\" + licenseId + "\\");

		File privateKeyOriginal = new File(privateKeyFilePath);
		FileUtil.copyToFolder(privateKeyOriginal, licenseFilePath + "\\" + licenseId + "\\");
		File privateKey = new File(licenseFilePath + "\\" + licenseId + "\\" + privateKeyOriginal.getName());

		if (privateKey.exists()) {
			File licenseFile = new File(licenseFilePath + "\\" + licenseId + "\\license.dat");
			FileOutputStream fos = new FileOutputStream(licenseFile);
			LicenseGenerator.generateLicense(properties, fos, privateKey.getAbsolutePath());
			fos.flush();
			fos.close();
			return licenseFile.getAbsolutePath();
		} else {
			log.error("Couldn't create License in folder {} because public key couldn't ne copied. ", licenseFilePath + licenseId + "/");
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
