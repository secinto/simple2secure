# simple2secure.portal

You can make your own local build and host the portal 

Prerequisites:

- Java 8, Gradle (min. 4.6)
- Tested with Eclipse IDE (with Spring Suite, JavaFX Plugin)
- MongoDB 

There are two different executions possible, either via an Servlet container such as Tomcat or directly via Spring Boot using Eclipse or Gradle.
We just show how to do it using Spring Boot. For Tomcat you need to create a WAR file which can be done via Gradle.

### General remark:

If you have a virus scanner running on your machine it is required that you either postpone it or you make an exception for the portal (Java task).
This is necessary in order to guarantee correct execution of the simple2secure.portal since it tries to send emails if required which can fail 
depending on the virus scanner used. 

## Installation

See general readme for installation instruction. 


## Execution using Eclipse and Spring Boot

Load the whole project using:
 
1) Import 
2) Gradle
3) Existing Gradle Project
4) Select folder where repository has been checked out (root folder simple2secure)
5) Press finish and let Gradle manage all dependencies. It can be that the main project also gets some src attached which is not correct and must be modified manually via the Properties of the simple2secure project in Eclipse / Java Build Path / Source - remove src folder.

If Spring Tools are not already installed, do it now via the Eclipse Marketplace (Spring Tools 4).

To start the portal you need to right click on simple2secure.portal project and select Run as ... / Spring Boot App and use Simple2SecurePortal as class.

## Execution via Gradle

Change to the simple2secure.portal folder and issue the following command

```
gradle bootRun
```

This will perform all necessary tasks in order to start the simple2secure.portal using Gradle.