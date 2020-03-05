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
	
	/**
	 * @param files
	 * 					A list of files which are going to be zipped.
	 * @return ByteArrayOutputStream of the zipped file/s. 
	 * @throws IOException
	 */
	public static ByteArrayOutputStream createZIPStreamFromFiles(List<File> files) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

		for (File file : files) {
			if(!file.isDirectory()) {
				zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
				FileInputStream fileInputStream = new FileInputStream(file);
				IOUtils.copy(fileInputStream, zipOutputStream);

				fileInputStream.close();
				zipOutputStream.closeEntry();
			}else {
				for(File dirFile : file.listFiles()) {
					zipOutputStream.putNextEntry(new ZipEntry(file.getName() + File.separator + dirFile.getName()));
					FileInputStream fileInputStream = new FileInputStream(dirFile);
					IOUtils.copy(fileInputStream, zipOutputStream);
					fileInputStream.close();
					zipOutputStream.closeEntry();
				}
			}
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
