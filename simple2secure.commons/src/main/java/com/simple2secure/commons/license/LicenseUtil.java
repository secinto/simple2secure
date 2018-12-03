package com.simple2secure.commons.license;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
	 * Needs to be performed to modify the default license path which should be used to store and load licenses as well as the default public
	 * key file paths.
	 *
	 * @param filePath
	 *          The path where the licenses should be stored and loaded from.
	 * @param publicKey
	 *          The file path to the default private key.
	 */
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
			privateKeyFilePath = workingDirectory + File.separator + privateKeyFileName;
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
	 * Generates a ZIP file from the default license in the {@value #licenseFilePath} and the specified publicKeyFile. The ZIP file is the
	 * complete license since without the public.key the license.dat file can't be verified.
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

		List<File> files = getLicenseFileList(licenseFile, publicKeyFile);
		ByteArrayOutputStream byteOutStream = ZIPUtils.createZIPStreamFromFiles(files);

		OutputStream outputStream = new FileOutputStream(zipFile);

		byteOutStream.writeTo(outputStream);
		outputStream.flush();
		outputStream.close();
	}

	/**
	 * Generates a {@link ByteArrayOutputStream} which represents the default license (working directory license.dat file) and provided public
	 * key as compressed data using the ZIP algorithm.
	 *
	 * @param publicKeyFile
	 *          The public key of the license file.
	 * @return The ZIP content as {@link ByteArrayOutputStream}
	 * @throws IOException
	 *           Thrown if the ZIP file stream couldn't be created
	 */
	public static ByteArrayOutputStream generateLicenseZIPStream(String licenseFile) throws IOException {
		return generateLicenseZIPStream(licenseFile, publicKeyFilePath);
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
	public static ByteArrayOutputStream generateLicenseZIPStream(String licenseFile, String publicKeyFile) throws IOException {
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
		ArrayList<File> files = new ArrayList<>();

		File publicKey = new File(publicFile);

		if (!publicKey.exists() || publicKey.isDirectory()) {
			throw new IOException("Provided public key file doesn't exist");
		}

		File certificate = new File(licenseFile);

		if (!certificate.exists() || certificate.isDirectory()) {
			throw new IOException("Provided license file doesn't exist");
		}

		files.add(publicKey);
		files.add(certificate);

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

		Properties properties = new OrderedProperties();
		properties.setProperty("expirationDate", expirationDate);
		properties.setProperty("groupId", groupId);
		properties.setProperty("licenseId", licenseId.toString());

		FileUtil.createFolder(licenseFilePath + licenseId + File.separator);

		File privateKeyOriginal = new File(privateKeyFilePath);
		FileUtil.copyToFolder(privateKeyOriginal, licenseFilePath + licenseId + File.separator);
		File privateKey = new File(licenseFilePath + licenseId + File.separator + privateKeyOriginal.getName());

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

		Properties properties = new OrderedProperties();
		properties.setProperty("expirationDate", expirationDate);
		properties.setProperty("groupId", groupId);
		properties.setProperty("licenseId", licenseId.toString());

		FileUtil.createFolder(licenseFilePath + licenseId + File.separator);

		File privateKey = new File(privateKeyFilePath);

		if (privateKey.exists()) {
			File licenseFile = new File(licenseFilePath + licenseId + File.separator + licenseFileName);
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
