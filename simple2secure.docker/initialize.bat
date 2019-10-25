@echo off
set directory=simple2secure
title simple2secure Docker
echo **************************************************************
echo *** Initializing simple2secure pod and standard tool chain ***
echo **************************************************************
echo Deleting %directory% directory
rmdir /s /q %directory%
echo Creating empty %directory% directory!
mkdir %directory%
echo Cloning simple2secure project in the %directory% directory...
git clone https://github.com/secinto/simple2secure.git %directory%
echo Installing python libraries
pip install -r requirements.txt
echo Starting python script for retrieving license
python login.py
echo Creating new simple2secure pod docker image
docker-compose build
echo Starting new simple2secure pod docker container
docker-compose up -d