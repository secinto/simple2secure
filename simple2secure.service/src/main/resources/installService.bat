set SERVICE_NAME=TestService
set PR_INSTALL=.\daemon\prunsrv.exe
 
REM Service log configuration
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGPATH=.\logs
set PR_STDOUTPUT=.\logs\stdout.txt
set PR_STDERROR=.\logs\stderr.txt
set PR_LOGLEVEL=Error
 
REM Path to java installation
set PR_JVM=C:\Program Files\Java\jre8\bin\server\jvm.dll
set PR_CLASSPATH=simple2secure.service-0.1.0-SNAPSHOT.jar
 
REM Startup configuration
set PR_STARTUP=auto
set PR_STARTMODE=jvm
set PR_STARTCLASS=com.simple2secure.service.ProbeControllerService
set PR_STARTMETHOD=windowsService
 
REM Shutdown configuration
set PR_STOPMODE=jvm
set PR_STOPCLASS=com.simple2secure.service.ProbeControllerService
set PR_STOPMETHOD=windowsService
 
REM JVM configuration
set PR_JVMMS=256
set PR_JVMMX=1024
set PR_JVMSS=4000
set PR_JVMOPTIONS=-Duser.language=DE;-Duser.region=de
 
REM Install service
prunsrv.exe //IS//%SERVICE_NAME%