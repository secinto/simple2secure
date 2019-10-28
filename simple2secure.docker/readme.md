# Simple2Secure Docker

## Prerequisites
Following programs must be installed on your system:

- Python (latest available version)
- Git
- Docker

### Installing

Run the **initialize.bat** script from the **simple2secure.docker** folder,
and all steps will be executed automatically.

### Tools
Standard tools which are installed with the simple2secure pod are:
  - **Sqlmap** - SQL injection tool
  - **wafw00f** - WAFW00F identifies and fingerprints Web Application Firewall
    (WAF) products.
  - **Commix** - Automated tool that can be used to test web-based applications
  with the view to find bugs, errors or vulnerabilities related to command
  injection attack
  - **nmap** - tool for network discovery and security auditing.
  - **traceroute** - computer network diagnostic tool.


You can install additional tools by adding the entry in the
**Dockerfile** file in the **simple2secure.docker/simple2secure/simple2secure.pod**
folder and rerunning **initialize.bat** script from the **simple2secure.docker**
folder. This type you must set the option to skip the git checkout.
