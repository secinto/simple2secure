Tutorial : https://rominirani.com/tutorial-getting-started-with-kubernetes-on-your-windows-laptop-with-minikube-3269b54a226
REST API : https://docs.openshift.com/enterprise/3.0/rest_api/kubernetes_v1.html

AWS ID: 800588509216

minikube start - to start kubernetes
minikube dashboard - to open the dashboard

https://192.168.99.100:8443/api/v1/componentstatuses

with bearer token which can be found in the dashboard->Secrets we get the list of the different services

/api/v1/events - returns the list of all events
/api/v1/endpoints - returns the list of all endpoints

current custom container not running because it has to contain an endlos loop

Kubernetes WIKI https://github.com/frozenfoxx/frozenfoxx.github.io/wiki/Kubernetes

###Deploying docker image to the kubernetes ###

	- Open the docker console
	- Run "minikube start"
	- Run "minikube docker-env"
	- Run "docker images" - to see if we are working from the kubernetes environment
	- Go to the project root directory and run "docker build -t <projectName>:<version> ."
	- To run image in container "kubectl run <container_name> --image=<name_of_the_image> --port=<port_number>"
	- If we check kubernetes dashboard or if we run "kubectl get pods" we should see our image under the pods
	- To expose container to the external traffic we have to run following command "kubectl expose deployment <container_name>"
	- minikube service <service_name> --url
	- After that the generated url will be created and our container should be also seen under the services
	
###Deploying Metasploit to the kubernetes ###

	- Open the docker console
	- Run "minikube start"
	- Run "minikube docker-env"
	- Run "docker pull phocean/msf"
	
###Deploying Metasploit to the kubernetes v2###

	- Open the docker console
	- Run "minikube start"
	- Run "minikube docker-env"
	- Run "docker pull remnux/metasploit"	
	
	
https://github.com/enaqx/awesome-pentest - Includes list of all tools and frameworks for the penetration testing

kubectl run -i -t <container_name
kubectl attach metasploit-7cd9cfd48b-j5x74 -c metasploit -i -t - attach the command prompt to the container
kubectl attach <pod_name> -c <container_name> -i -t

docker pull networkstatic/nmap

Running metasploit in kubernetes

sudo apt-get update
sudo apt-get install ruby
gem install bundler

- packet generator
- packet crafting
- packet manipulation
- packet generation

- packet sender
	nping
	scapy

manipulating interfaces
handling methode damit man die Daten in das Image
schnitstelle auswählen

kubectl run -i -t kalilinux/kali-linux-docker

docker pull uzyexe/nmap

