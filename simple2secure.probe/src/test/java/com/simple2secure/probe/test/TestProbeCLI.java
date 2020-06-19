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
package com.simple2secure.probe.test;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.simple2secure.commons.license.LicenseUtil;
import com.simple2secure.probe.ProbeStarter;

public class TestProbeCLI {

   @Test
   public void testProbeCLIInstrumentationUsingSystemIn() throws IOException {
      File licenseZIP = createLicense();

      String[] args = new String[2];
      args[0] = "-l";
      args[1] = licenseZIP.getAbsolutePath();
      ProbeStarter.main(args);
   }

   private File createLicense() throws IOException {
      LicenseUtil.initialize("src/test/resources/licenses/");
      LicenseUtil.createLicenseFile("testgroup", "1", "18/12/2022");
      String pathOfZipFile = "src/test/resources/licenses" + File.separator + "license.zip";
      LicenseUtil.generateLicenseZIPFromID(pathOfZipFile, "1");
      return new File(pathOfZipFile);

   }
}
