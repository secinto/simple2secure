# simple2secure.service

This sub project contains functions which provide a local service which installs the probe as windows service and 
monitors it as well as the portal. If the probe crashes for any reason it is automatically restarted. 
 
Currently we are working on providing an automated update system which installs a new probe library if one is available
from the portal and restarts the probe thereafter.
 
## Service deployment
 
Currently no specific convenience method is provided to install the service but it can be done using the TestServiceInstallation test
class. It requires a probe and service library in the release/libs folder. The libraries must be named simple2secure.service.jar 
and simple2secure.probe.jar. 
 
The service can be executed using either a generated java library and providing the following start commands. The release

```
java -jar ./build/libs/simple2secure.service-1.0.0-RC.jar -a install
```

or using Gradle

```
gradle run --args="-a install"
```
 
 The probe and the service are configured to use the release folder for storing all relevant data, the DB and the log files.