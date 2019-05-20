/**
 *********************************************************************
 *
 * Copyright (C) 2019 by secinto GmbH (http://www.secinto.com)
 *
 *********************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 *********************************************************************
 */
package com.simple2secure.commons.file;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class ZIPUtils {

	private static String workingDirectory = System.getProperty("user.dir");

	public static ByteArrayOutputStream createZIPStreamFromFiles(List<File> files) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

		for (File file : files) {
			zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
			FileInputStream fileInputStream = new FileInputStream(file);
			IOUtils.copy(fileInputStream, zipOutputStream);

			fileInputStream.close();
			zipOutputStream.closeEntry();
		}

		if (zipOutputStream != null) {
			zipOutputStream.finish();
			zipOutputStream.flush();
			zipOutputStream.close();
		}
		bufferedOutputStream.close();
		return byteArrayOutputStream;
	}

	/**
	 * TODO: Write tests to check the different scenarios.
	 *
	 * This function extracts the imported ZIP file and checks if the extracted content is correct. If not then <code>null</code> will be
	 * returned, else the list of the files will be returned.
	 *
	 * @param baseDir
	 *          Specifies the base directory into which the ZIP container should be extracted to.
	 * @param importFile
	 *          The file which should be imported and unzipped in the current working directory.
	 * @return A list of files which have been created from the ZIP container.
	 * @throws IOException
	 */
	public static List<File> unzipImportedFile(File importFile) throws IOException {

		List<File> fileListUnzipped = new ArrayList<>();
		ZipInputStream zis = new ZipInputStream(new FileInputStream(importFile));

		ZipEntry ze = zis.getNextEntry();
		byte[] buffer = new byte[1024];
		int zipFileSize = 0;
		while (ze != null) {
			if (ze.isDirectory()) {
				break;
			} else {
				String fileName = ze.getName();
				File newFile = new File(fileName);

				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				fileListUnzipped.add(newFile);
				zipFileSize++;
				ze = zis.getNextEntry();
			}
		}
		zis.close();

		if (zipFileSize == 2) {
			return fileListUnzipped;
		} else {
			return null;
		}
	}
}
