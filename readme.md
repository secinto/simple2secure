# simple2secure installation instructions
You can use our official BETA portal on https://simple2secure.info:51003/#/ 

or 

you can make your own local build and host the portal, testservice, web and so on locally.

Prerequisites:

- Java 8, Gradle
- Tested with Eclipse IDE (with Spring Suite, JavaFX Plugin)
- MongoDB 
- Python, Pip, NodeJS, Redis
- In order that the tests can be run it is required that any Virus scanner is muted, since they usually block sending Emails directly


### Installing simple2secure.portal ###
1) Download and install MongoDB Community Edition
2) Start mongodb by running "mongod" command from the shell
3) Download and install gradle from from https://gradle.org/releases/
3) Navigate to the simple2secure root folder and run "gradle eclipse"
4) Open project in eclipse and you can run it directly from the eclipse IDE


### Installing simple2secure.web
1) Download and install NodeJS (verified with 10.16.3 LTS) from https://nodejs.org/en/download/
2) Run "npm install" from the simple2secure.web directory
3) install Angular CLI by executing "npm install -g @angular/cli" from the command line
4) After everything is installed successfully you can start the web by executing "start.bat" batch file from the simple2secure.web directory 

Please make sure that Python, Pip and Redis are installed on your PC

### WINDOWS
Download and install redis as windows service from:
https://github.com/downloads/rgl/redis/redis-2.4.6-setup-64-bit.exe

### LINUX
1) sudo apt-get update
2) sudo apt-get upgrade
3) sudo apt-get install redis-server
4) sudo systemctl enable redis-server.service

## Install all dependencies from requirements.txt
pip install -r requirements.txt

## Start python application
python app.py

## Start celery worker from the console (Open console and type)
celery -A src.celery.celery_tasks.celery worker --loglevel=info


Activating and running the pod from our server (currently in maintenance mode - will be announced as soon as it has been updated to current release version)

1) Visit our portal at https://simple2secure.info:51003 and register if you do not have an account

2) Activate an account and download license from the "My Groups" tab under the following link https://simple2secure.info:51003/#/user

3) Copy this license file in the /static/license/ folder

4) Check in config.py file if the parameter PORTAL_URL is set to the "https://simple2secure.info:51001/s2s/api"

5) After that install all required libraries by executing pip install -r requirements.txt from the root folder of our testservice project

6) When all libraries are installed you can start the project by running python app.py
```
