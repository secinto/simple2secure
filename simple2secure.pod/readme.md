# Simple2Secure PenTest Service

## Getting Started

These instructions will get you a running simple2secure test service docker
image on your local machine for testing purposes.

### Installing

A step by step series of examples that tell you how to get an image running

Load the provided image to the local repository

```
docker load -i flask-service-s2s.tar
```

Run the loaded image in the docker

```
docker run --name=s2s-test-service -d -p 5000:5000 flask-service-s2s
```

(Optional) Access image using bash to update services.json file or install new
tools.
services.json file is located in the root folder of the image.

```
docker exec -i -t s2s-test-service /bin/bash
```

## Running the tests

Our local docker machine is running under http://192.168.99.100 and service runs on the port 5000.
You can discover your docker machine ip address by calling following command:

```
docker-machine ip
```

List all available tests

```
http://192.168.99.100:5000/services
```

Run default test(It will run first test from the list of the available tests)

```
http://192.168.99.100:5000/services/run
```

Run test by testId with all commands and parametes as defined in services.json

```
http://192.168.99.100:5000/services/run?test=test1
```

Run test while changing the test step parameter

```
http://192.168.99.100:5000/services/run?test=test1&step=stp1%parameter=cmdstp1param%value=www.secinto.at
```

Run test while changing the test step command

```
http://192.168.99.100:5000/services/run?test=test1&step=stp1%command=cmdstp1%executable=nmap%20-RF
```

Run test while changing test step parameter and command

```
http://192.168.99.100:5000/services/run?test=test1&step=stp1%command=cmdstp1%executable=nmap%20-RF%parameter=cmdstp1param%value=www.secinto.at
```

## License

This project is licensed under the MIT License
