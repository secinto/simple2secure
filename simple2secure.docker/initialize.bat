@echo off
set directory=simple2secure
title simple2secure Docker
echo **************************************************************
echo *** Initializing simple2secure pod and standard tool chain ***
echo **************************************************************
echo Would you like to skip git checkout (yes/no)?
set /p choice= "Please Select one of the above options :" 
if %choice%==yes call /simple2secure/simple2secure.pod/create_docker.bat 
if %choice%==no goto GITCHECKOUT

:GITCHECKOUT
echo Deleting %directory% directory
rmdir /s /q %directory%
echo Creating empty %directory% directory!
mkdir %directory%
echo Cloning simple2secure project in the %directory% directory...
git clone https://github.com/secinto/simple2secure.git %directory%
call simple2secure/simple2secure.pod/create_docker.bat