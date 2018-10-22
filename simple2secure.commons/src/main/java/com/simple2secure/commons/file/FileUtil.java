package com.simple2secure.commons.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * @author <a href="stefan.kraxberger@secinto.com">Stefan Kraxberger</a>
 *
 */
public class FileUtil {

	static Logger log = LoggerFactory.getLogger(FileUtil.class);

	private static File workingDirectory = new File("./");
	private static String absoluteWorkingPath = FileUtil.workingDirectory.getAbsolutePath();

	static {
		setWorkingDirectory(".");
	}

	public static void setWorkingDirectory(String workingDirectory) {
		File dir = new File(workingDirectory);

		if (dir.exists()) {
			absoluteWorkingPath = dir.getAbsolutePath();
			absoluteWorkingPath = absoluteWorkingPath.replace("\\.", "\\");
			FileUtil.workingDirectory = dir;
		}
	}

	/**
	 * Checks whether the specified file or folder exists and is not empty. If it exists <code>true</code> is returned.
	 *
	 * @param path
	 *          The path which should be verified.
	 * @return True if the specified path exists.
	 */
	public static boolean fileOrFolderExists(String folder) {
		if (!Strings.isNullOrEmpty(folder)) {
			File file = new File(folder);

			return file.exists();
		}
		return false;
	}

	public static File getFile(String path) throws IOException {
		return getFile(path, null);
	}

	/**
	 * Returns a file object for the specified path if the file exists and it is not contained in the exclude files list. Otherwise null is
	 * returned.
	 *
	 * @param path
	 *          The path to the file
	 * @param excludeFiles
	 *          A list of files which should not be included in the application
	 *
	 * @return The file created using the specified path
	 * @throws IOException
	 */
	public static File getFile(String path, List<String> excludeFiles) throws IOException {
		return getFile(path, excludeFiles, null);
	}

	/**
	 * Returns a file object for the specified path if the file exists and it is not contained in the exclude files list as well not excluded
	 * by the specified patterns. Otherwise null is returned.
	 *
	 * @param path
	 *          The path to the file
	 * @param excludeFiles
	 *          A list of files which should not be included in the application
	 * @param excludePatterns
	 *          A list of patterns which a file must not have to be included
	 * @return A file object if a file exists at the specified path.
	 * @throws IOException
	 */
	public static File getFile(String path, List<String> excludeFiles, List<String> excludePatterns) throws IOException {
		if (includedByList(path, excludeFiles) && includedByPattern(path, excludePatterns)) {
			File file = new File(path);

			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

	/**
	 * Creates the specified folder in the file system. If the folder has been created <code>true</code> is returned. The absolute path must
	 * be specified otherwise it is supposed that it is a relative path from the directory where the application has been started.
	 *
	 * @param folder
	 *          The folder which should be created.
	 * @return True if the folder is created.
	 */
	public static boolean createFolder(String folder) {
		if (!Strings.isNullOrEmpty(folder)) {
			try {
				FileUtils.forceMkdir(new File(folder));
			} catch (Exception e) {
				log.error("Couldn't create folder " + folder + ". Reason: " + e.getMessage());
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Deletes the specified folder from the file system. If the folder has been deleted <code>true</code> is returned. The absolute path must
	 * be specified otherwise it is supposed that it is a relative path from the directory where the application has been started.
	 *
	 * @param folder
	 *          The folder which should be deleted.
	 * @return True if the folder has been deleted.
	 */
	public static boolean deleteFolder(String folder) {
		if (!Strings.isNullOrEmpty(folder)) {
			return FileUtils.deleteQuietly(new File(folder));
		}
		return false;
	}

	/**
	 * Copies the specified file to the folder. If the file and the folder exists the file is copied and <code>true</code> is returned. The
	 * absolute path must be specified otherwise it is supposed that it is a relative path from the directory where the application has been
	 * started.
	 *
	 * @param file
	 *          The file which should be copied.
	 * @param folder
	 *          The folder to which the file should be copied.
	 * @return True if the file could be copied.
	 */
	public static boolean copyToFolder(File file, String folder) {
		if (file != null && file.exists() && !Strings.isNullOrEmpty(folder) && fileOrFolderExists(folder)) {
			try {
				FileUtils.copyFileToDirectory(file, new File(folder));
				return true;
			} catch (Exception e) {
				log.error("Couldn't copy file: " + file.getAbsolutePath() + " to folder: " + folder + ". Reason: " + e.getMessage()); //$NON-NLS-3$
				return false;
			}

		}

		log.info("File wasn't copied to folder " + folder + " because either the file or folder was null or didn't exist.");

		return false;
	}

	/**
	 * Returns the contents of the specified file as {@link String} using UTf-8 encoding.
	 *
	 * @param file
	 *          The file whose content should be obtained.
	 * @return The content of the file as {@link String}.
	 * @throws IOException
	 */
	public static String getFileContents(File file) throws IOException {
		FileInputStream inStream = new FileInputStream(file);
		String content = IOUtils.toString(inStream, Charset.forName("UTF-8"));
		inStream.close();
		return content;
	}

	/**
	 * Returns a temporary folder in the file system. If a system defined temporary folder exists it is returned, otherwise a folder named
	 * <code>temp</code> is created in the current directory.
	 *
	 * @return The absolute path to the temp folder.
	 */
	public static String getTempFolder() {
		String tempFolder = FileUtils.getTempDirectoryPath();

		if (Strings.isNullOrEmpty(tempFolder)) {
			try {

				FileUtils.forceMkdir(new File("temp"));

				tempFolder = new File("temp").getPath();
			} catch (Exception e) {
				log.error("Couldn't create local temp folder. Reason: " + e.getMessage());
				return null;
			}
		}

		if (!Strings.isNullOrEmpty(tempFolder)) {
			if (!tempFolder.endsWith("//")) {
				tempFolder = tempFolder + "//";
			}

		}
		return tempFolder;
	}

	/**
	 * Generates a list of {@link File} objects from the specified directory. If scanRecursive is set to true all sub folders of the root
	 * folder are scanned too. Using the fileTypes String list allows for filtering for specific file types. For instance use {"java","c",
	 * "h"} to just include java and c source and header files.
	 *
	 * @param directory
	 * @param scanRecursive
	 * @param fileTypes
	 * @return
	 * @throws IOException
	 */
	public static List<File> getFilesFromDirectory(String directory, boolean scanRecursive, List<String> fileTypes) throws IOException {
		return getFilesFromDirectory(directory, scanRecursive, fileTypes, null, null);
	}

	/**
	 * Generates a list of {@link File} objects from the specified directory. If scanRecursive is set to true all sub folders of the root
	 * folder are scanned too. Using the fileTypes String list allows for filtering for specific file types. For instance use {"java","c",
	 * "h"} to just include java and c source and header files. Using the excludePatterns allows for excluding directories which contain the
	 * provided pattern. For instance {@literal *}/temp does not scan directories named temp. {@literal *}temp does not scan directories
	 * ending with temp.
	 *
	 * @param directory
	 * @param scanRecursive
	 * @param fileTypes
	 * @param excludePatterns
	 * @return
	 * @throws IOException
	 */
	public static List<File> getFilesFromDirectory(String directory, boolean scanRecursive, List<String> fileTypes, List<String> excludeFiles,
			List<String> excludePatterns) throws IOException {
		File root = new File(directory);
		List<File> fileList = new ArrayList<File>();

		if (root.isDirectory() && includedByPattern(root.getAbsolutePath(), excludePatterns)) {
			File[] files = root.listFiles();

			for (File file : files) {
				if (file.isDirectory() && scanRecursive && includedByPattern(file.getAbsolutePath(), excludePatterns)
						&& includedByList(file.getAbsolutePath(), excludeFiles)) {
					fileList.addAll(getFilesFromDirectory(file.getPath(), true, fileTypes, excludeFiles, excludePatterns));
				} else if (listOfStringsContains(fileTypes, FilenameUtils.getExtension(file.getName()))) {
					file = getFile(file.getPath(), excludeFiles, excludePatterns);
					if (file != null) {
						fileList.add(file);
					}
				}

			}
		}
		return fileList;

	}

	/**
	 * Helper function for obtaining the include files from a specified file (it must be a file with the same format as the input file used
	 * for doxygen). The file may contain input paths for folders and for specific files. If a folder is specified all files, including all
	 * subfolders, are included.
	 *
	 * @param inputFile
	 *          The file from which the input file specification should be obtained.
	 * @return
	 * @throws IOException
	 */
	public static List<String> readInfoFromFile(String file) throws IOException {
		List<String> fileList = new ArrayList<String>();

		if (!FileUtil.fileOrFolderExists(file)) {
			log.info("The specified file {} doesn't exist.", file);
			return fileList;
		}

		String inputFileContent = FileUtil.getFileContents(new File(file));

		if (!Strings.isNullOrEmpty(inputFileContent)) {

			String[] inputPaths = inputFileContent.split("\n");
			for (String inputPath : inputPaths) {
				if (!Strings.isNullOrEmpty(inputPath)) {
					File testFile = new File(inputPath);

					if (!fileList.contains(inputPath)) {
						fileList.add(FileUtil.getAbsolutePath(testFile.getPath()));
					}
				}
			}
		}

		return fileList;
	}

	/**
	 * Helper function for obtaining the include files from a specified file (it must be a file with the same format as the input file used
	 * for doxygen). The file may contain input paths for folders and for specific files. If a folder is specified all files, including all
	 * subfolders, are included.
	 *
	 * @param inputFile
	 *          The file from which the input file specification should be obtained.
	 * @return
	 * @throws IOException
	 */
	public static List<String> readInfoFromFile(List<String> files) throws IOException {
		List<String> fileList = new ArrayList<String>();

		for (String file : files) {

			if (!FileUtil.fileOrFolderExists(file)) {
				log.info("The specified file {} doesn't exist.", file);
				return fileList;
			}

			String inputFileContent = FileUtil.getFileContents(new File(file));

			if (!Strings.isNullOrEmpty(inputFileContent)) {

				String[] inputPaths = inputFileContent.split("\n");
				for (String inputPath : inputPaths) {
					if (!Strings.isNullOrEmpty(inputPath)) {
						File testFile = new File(inputPath);

						if (!fileList.contains(inputPath)) {
							fileList.add(FileUtil.getAbsolutePath(testFile.getPath()));
						}
					}
				}
			}
		}

		return fileList;
	}

	/**
	 *
	 * @param relativePath
	 * @return
	 * @throws IOException
	 */
	public static String getAbsolutePath(String relativePath) throws IOException {
		String absolutePath = "";

		if (relativePath.contains("..") || relativePath.contains(".")) {
			String parts[] = relativePath.replace("\n", "").replace("\r", "").split(Pattern.quote(File.separator));
			String[] workingPath = absoluteWorkingPath.split(Pattern.quote(File.separator));
			int index = 0;

			for (String part : parts) {
				if (part.equals("..")) {
					index++;
				} else if (part.equals(".")) {
					index = 0;
				} else {
					absolutePath = absolutePath + part + "/";
				}
			}

			int length = workingPath.length;

			if (workingPath[workingPath.length - 1].equalsIgnoreCase(".")) {
				length--;
			}

			String[] newPath = Arrays.copyOfRange(workingPath, 0, length - index);

			String prefixPath = "";
			for (String part : newPath) {
				prefixPath = prefixPath + part + "/";
			}

			absolutePath = prefixPath + absolutePath;
		} else {
			absolutePath = relativePath;
		}

		if (!new File(absolutePath).exists()) {
			log.info("Couldn't determine correct path {}", relativePath);
		}

		return absolutePath;
	}

	/**
	 *
	 * @param path
	 * @return
	 */
	public static boolean isDirectory(String path) {
		if (!Strings.isNullOrEmpty(path)) {
			File file = new File(path);

			return file.isDirectory();
		}
		return false;
	}

	/**
	 *
	 * @param listOfStrings
	 * @param containedEntry
	 * @return
	 */
	public static boolean listOfStringsContains(List<String> listOfStrings, String containedEntry) {
		if (listOfStrings == null) {
			log.warn("No file types specified in provided array. Nothing will be done.");
			return false;
		}

		if (listOfStrings.size() == 1 && listOfStrings.get(0).equalsIgnoreCase("ALL")) {
			return true;
		}

		for (String entry : listOfStrings) {
			if (entry.equalsIgnoreCase(containedEntry)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks the provided list of files whether the provided file is contained. If the file is contained <code>true</code> is returned.
	 *
	 * @param listOfFiles
	 *          The list of files which should be searched.
	 * @param containedEntry
	 *          The file for which should be checked.
	 * @return True if the file is contained in the list.
	 */
	public static boolean listOfFilesContains(List<File> listOfFiles, File containedEntry) {
		for (File entry : listOfFiles) {
			if (entry.equals(containedEntry)) {
				return true;
			}
			if (entry.getAbsolutePath().equalsIgnoreCase(containedEntry.getAbsolutePath())) {
				return true;
			}
		}

		return false;
	}

	public static File getEquivalentFile(List<File> listOfFiles, File containedEntry) {
		for (File entry : listOfFiles) {
			if (entry.equals(containedEntry)) {
				return entry;
			}
			if (entry.getName().equalsIgnoreCase(containedEntry.getName())) {
				return entry;
			}
		}

		return null;
	}

	public static String constructFilename(File originalFile, String outputFolder) {
		if (originalFile != null && originalFile.exists() && !Strings.isNullOrEmpty(outputFolder) && fileOrFolderExists(outputFolder)) {
			return outputFolder + originalFile.getName();
		}

		log.info("No filename was constructed because either the provided file/folder was null or didn't exist.");

		return null;
	}

	private static boolean includedByPattern(String absolutePath, List<String> excludePatterns) throws IOException {
		boolean result = true;
		if (excludePatterns != null && excludePatterns.size() > 0) {
			for (String pattern : excludePatterns) {
				if (absolutePath.contains("./")) {
					absolutePath = getAbsolutePath(absolutePath);
				}
				if (pattern.startsWith("*/")) {
					pattern = pattern.replace("*", "");
					absolutePath = absolutePath.replace("\\\\", "/");
					absolutePath = absolutePath.replace("\\", "/");
					if (absolutePath.endsWith(pattern)) {
						return false;
					}
				}
			}
		}
		return result;
	}

	private static boolean includedByList(String absolutePath, List<String> excludeFiles) throws IOException {
		boolean result = true;
		if (excludeFiles != null && excludeFiles.size() > 0) {
			for (String file : excludeFiles) {
				if (absolutePath.contains("./")) {
					absolutePath = getAbsolutePath(absolutePath);
				}
				File absolutePathFile = new File(absolutePath);

				File checkFolder = new File(file);
				if (checkFolder.exists() && checkFolder.isDirectory()) {
					String excludeFile = file.replace("\\", "/");
					if (excludeFile.endsWith("/")) {
						excludeFile = excludeFile.substring(0, excludeFile.length() - 1);
					}
					absolutePath = absolutePath.replace("\\", "/");

					String filePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));

					if (excludeFile.equalsIgnoreCase(absolutePath)) {
						return false;
					}

					if (excludeFile.equalsIgnoreCase(filePath)) {
						return false;
					}

					if (checkFolder.getAbsolutePath().equalsIgnoreCase(absolutePathFile.getAbsolutePath())) {
						return false;
					}

				} else if (checkFolder.exists() && checkFolder.isFile()) {
					if (file.equalsIgnoreCase(absolutePath)) {
						return false;
					}

					if (checkFolder.getAbsolutePath().equalsIgnoreCase(absolutePathFile.getPath())) {
						return false;
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("File does not exist and cannot be used for comparison. {}", file);
					}
				}
			}
		}
		return result;
	}
}