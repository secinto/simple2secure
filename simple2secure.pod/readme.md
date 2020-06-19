# simple2secure Pod

For development it is easiest to use the Pod directly and not within a Docker container, since it is easier to modify things. 
Therefore, some tools are required to be installed. The Pod uses Celery (Distributed Task Queue) for managing its different tasks
which itself requires a third part Message Queue provider such as RabbitMQ, Redis or others. We have based your solution on Redis 
and depending on whether you are developing on Windows or Linux it must be installed for this system. Find below the installation 
instructions for the different OS.

### Installation of Redis 

#### WINDOWS
Download and install redis as windows service from:
https://github.com/downloads/rgl/redis/redis-2.4.6-setup-64-bit.exe

#### LINUX
```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install redis-server
sudo systemctl enable redis-server.service
```
### Install all dependencies from requirements.txt
```
pip install -r requirements.txt
```
### Start python application
```
python app.py
```

## General info on the portal and the pod

Here it is described how to activate and run the Pod using our server but it would be the same (except that you need to use localhost:9000 
instead of simple2secure.info:51003).

Activating and running the pod from our server (currently in maintenance mode - will be announced as soon as it has been updated to current release version)

1) Visit our portal at https://simple2secure.info:51003 and register if you do not have an account

2) Activate an account and download license from the "My Groups" tab under the following link https://simple2secure.info:51003/#/user by clicking on the Burger symbol on the Standard group.

3) Copy this license file in the /static/license/ folder (create the folder /static/license/ in simple2secure.pod folder if not available)

4) Check in config.py file if the parameter PORTAL_URL is set to the "https://simple2secure.info:51001/s2s/api" (Is by default)

5) After that install all required libraries (see above).

6) When all libraries are installed you can start the project by running python app.py
