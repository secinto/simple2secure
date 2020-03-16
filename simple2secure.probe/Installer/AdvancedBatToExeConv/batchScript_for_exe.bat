@ECHO OFF
REM BFCPEOPTIONSTART
REM Advanced BAT to EXE Converter www.BatToExeConverter.com
REM BFCPEEXE=C:\Users\Benjamin Velic\Desktop\test\setup_s2s_probe.exe
REM BFCPEICON=K:\work\projects\simple2secure\simple2secure.service\release\simple2secure.ico
REM BFCPEICONINDEX=-1
REM BFCPEEMBEDDISPLAY=0
REM BFCPEEMBEDDELETE=1
REM BFCPEADMINEXE=0
REM BFCPEINVISEXE=0
REM BFCPEVERINCLUDE=0
REM BFCPEVERVERSION=1.0.0.0
REM BFCPEVERPRODUCT=Product Name
REM BFCPEVERDESC=Product Description
REM BFCPEVERCOMPANY=Your Company
REM BFCPEVERCOPYRIGHT=Copyright Info
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\buildRelease.bat
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\deleteService.bat
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\installService.bat
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\Probe Service.exe
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\prunmgr.exe
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\prunsrv.exe
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\startService.bat
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\stopService.bat
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\libs\simple2secure.probe.jar
REM BFCPEEMBED=K:\work\projects\simple2secure\simple2secure.service\release\libs\simple2secure.service.jar
REM BFCPEOPTIONEND
@ECHO ON
set current_workdir=%~dp0

::Checks if Java is installed on current machine.
where java >nul 2>nul
if %errorlevel%==1 (
	::Checks if scoop is installed on current machine.
	where scoop >nul 2>nul
	if %errorlevel%==1 (
		::Installs scoop to install java on the machine via powershell.
		@echo Scoop not found on your machine.
		powershell -Command "& { Set-ExecutionPolicy RemoteSigned -scope CurrentUser -Confirm:$false } "
		powershell -Command "& { iex (new-object net.webclient).downloadstring('https://get.scoop.sh') }"
		powershell -Command "& { $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User") }"
		where scoop >nul 2>nul
		if not %errorlevel%==1 (
			@echo Scoop has been installed succesfully!
		)
	)else (
		@echo Scoop is already installed on your machine!
	)
	::Installs java via scoop.
    @echo Java not found in path.
	powershell -Command "& { scoop bucket add java }"
	powershell -Command "& { scoop install java }"
	where java >nul 2>nul
	if not %errorlevel%==1 (
		@echo Java has been installed successfully!
	)
)else (
	@echo Java is already installed on your machine!
)

::Check if the OS has already the npcap service installed.
sc query npcap >nul 2>nul
if not %errorlevel%==0 (
	::Installs npcap on the machine. 
	@echo No npcap service found on your machine!
	powershell -Command "Invoke-WebRequest https://nmap.org/npcap/dist/npcap-0.9987.exe -OutFile npcap-0.9987.exe"
	powershell -Command "Start-Process -filepath .\npcap-0.9987.exe"
	@echo Npcap has been installed succesfully!
)else (
	@echo Npcap is already running on your machine!
)

::Creates directory for the jar libs.
mkdir libs

::Copy the in the .exe embedded files to their proper directory.
robocopy /njh /njs /ndl /nc /ns %MyFiles% libs simple2secure.probe.jar
robocopy /njh /njs /ndl /nc /ns %MyFiles% libs simple2secure.service.jar
robocopy /njh /njs /ndl /nc /ns %MyFiles% . installService.bat
robocopy /njh /njs /ndl /nc /ns %MyFiles% . startService.bat
robocopy /njh /njs /ndl /nc /ns %MyFiles% . stopService.bat
robocopy /njh /njs /ndl /nc /ns %MyFiles% . deleteService.bat
robocopy /njh /njs /ndl /nc /ns %MyFiles% . prunmgr.exe
robocopy /njh /njs /ndl /nc /ns %MyFiles% . prunsrv.exe
robocopy /njh /njs /ndl /nc /ns %MyFiles% . "Probe Service.exe"

::Creates directory for the license to be copied in.
mkdir license

::Copy the license zip to the proper directory.
move .\license-* license\

::Installs the probe service on the machine.
java -jar %MyFiles%\simple2secure.service.jar -a install