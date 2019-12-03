#!/bin/bash

export directory=simple2secure
echo "**************************************************************"
echo "*** Initializing simple2secure pod and standard tool chain ***"
echo "**************************************************************"
echo "Would you like to skip git checkout (yes/no)?"
read -p "Please Select one of the above options : " choice

if  [ $choice == "yes" ]
then
	cd simple2secure/simple2secure.pod	
	sh ./create_docker.sh 
fi

if  [ $choice == "no" ]
then
	echo "Deleting $directory directory"
	rm -rf $directory
	echo "Creating empty $directory directory!"
	mkdir $directory
	echo "Cloning simple2secure project in the $directory directory..."
	git clone https://github.com/secinto/simple2secure.git $directory
	cd simple2secure/simple2secure.pod	
	sh ./create_docker.sh 
fi
