- Download Eclipse Oxygen
- Install gradle on windows
	1) Download it from https://gradle.org/releases/
	2) Extract the zip file
	3) set environment varibale for gradle
		3.1) set GRADLE_HOME={path_of_the_extracted_directory}
		3.2) set PATH=%GRADLE_HOME%\bin;%PATH%
	4) Navigate to the root simple2secure folder and run gradle eclipse to build an eclipse project

- Open eclipse - File - Import - General - Existing projects into workspace - {simple2secure folder}
- Install e(fx)clipse from "Install new software" in Eclipse		
- Install mongodb on windows
	1) Download installer from https://www.mongodb.com/download-center#community
	2) Install it
	3) Set environment variable for mongodb
		3.1) set MONGODB_HOME={path_of_the_installation_directory}
		3.2) set PATH=%MONGODB_HOME%\bin;%PATH%
		
- Download and install nodejs from https://nodejs.org/en/download/
	1) Run "npm install" from the root directory of the simple2secure.web
	2) install Angular CLI "npm install -g @angular/cli"

- Download and install WinPcap from http://www.win10pcap.org/download/
		
- Use the keystores in src/main/resources/keystores for Probe and Portal either by importing the 
  certificate as administrator into the common cacerts of Java. NOT RECOMMENDED
  
  keytool -import -trustcacerts -alias {something} -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -file {path_to_certificate file}	
  
  OR better by using the cacerts keystore in the src/main/resources/keys with System properties as start parameters. 
  
  In eclipse you can enter the VM arguments for the RUN/DEBUG configuration like
  
  -Djavax.net.ssl.trustStore=<path_to_cacerts> -Djavax.net.ssl.trustStorePassword=<password>
  
  e.g.
  
  -Djavax.net.ssl.trustStore=D:/development/simple2secure/simple2secure.portal/src/main/resources/keystore/cacerts -Djavax.net.ssl.trustStorePassword=<password>
  
  Additionally it will be necessary for chrome to enable the following flag. Insert this in the address bar and set it to 
  enabled. 
	
	chrome://flags/#allow-insecure-localhost 
	
  For firefox it is necessary to surf to https://localhost:8443/ and create an exception by downloading the certificate and trusting
  it manually. Thereafter it works.
		
***simple2secure.probe***
- Run ProbeGUI as Java Application

***simple2secure.web***
- Navigate to the root folder of the simple2secure.web, open console and run "ng serve"

	
	