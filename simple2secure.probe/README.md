# simple2secure.probe

The Probe is a Java application which requires a license.zip in order to get attached to the portal. It provides monitoring
and analysis capabilities for devices capable of running normal Java instances. 

It has several features such as:
- Network traffic monitoring and analysis 
- Collecting and transmitting system and operation information
- Execution of commands from the portal
- Performing of tasks specified via portal on provided data

## License

The probe requires a license in order to be activated and associated with the portal. Therefore a license must be downloaded
from the local portal installation using your registered user (portal and web must be started then https://localhost:9000/ is available). 
Click on the user icon on the upper right corner and go to the My Groups tab. There initially one group "Standard" is available. 
Click on the more icon and select download license. Use this license for all further actions for the probe (it is the same required
also by the pod).


## Execution

The probe can be executed using either a generated java library and providing the following start commands

```
java -jar ./build/libs/simple2secure.probe-1.0.0-RC.jar -l ./release/license.zip
```

or using Gradle

```
gradle run --args="-l ./release/license.zip"
```
or it can also be directly started from Eclipse using either the ProbeCLI or ProbeGUI class. 
It is also possible to install probe directly as windows service and run it directly from there. 
Therefore see the description in simple2secure.service on how to set up the service.

