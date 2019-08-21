# simple2secure Pod

## Getting Started

These instructions will get you a running simple2secure Pod on your local machine for testing purposes.

## Installing & Execution

The installation steps are explained in the main readme in the simple2secure folder. 

### Start python application

Change to the simple2secure.pod folder and execute:

```
python app.py
```

## Start celery worker from the console (Open console and type)
```
celery -A src.celery.celery_tasks.celery worker --loglevel=info
```

## Running the tests

Our local Pod is running under https://localhost:5000/.

List all available tests

```
http://localhost:5000/services
```

Run default test(It will run first test from the list of the available tests)

```
http://localhost:5000/services/run
```

Run test by testId with all commands and parametes as defined in services.json

```
http://localhost:5000/services/run?test=test1
```

Run test while changing the test step parameter

```
http://localhost:5000/services/run?test=test1&step=stp1%parameter=cmdstp1param%value=www.secinto.at
```

Run test while changing the test step command

```
http://localhost:5000/services/run?test=test1&step=stp1%command=cmdstp1%executable=nmap%20-RF
```

Run test while changing test step parameter and command

```
http://localhost:5000/services/run?test=test1&step=stp1%command=cmdstp1%executable=nmap%20-RF%parameter=cmdstp1param%value=www.secinto.at
```
