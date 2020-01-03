FROM ubuntu:18.04

MAINTAINER "secinto GmbH"

# Required linux packages
RUN apt-get update -y && \
 		apt-get install -y python3-pip python3-dev python-minimal python-setuptools build-essential libssl-dev libffi-dev wget git bsdmainutils 

# Standard tools		
RUN apt-get -y install nmap
RUN apt-get -y install traceroute
RUN apt-get -y install sqlmap
RUN git clone https://github.com/EnableSecurity/wafw00f.git && cd wafw00f && python setup.py install  
RUN git clone https://github.com/commixproject/commix.git commix && cd commix && python commix.py --install
RUN git clone https://github.com/drwetter/testssl.sh.git
ENV PATH "$PATH:/testssl.sh"

# Copy requirements.txt to the Working directory
COPY ./requirements.txt /app/requirements.txt

# Set working directory
WORKDIR /app

# Install python libraries for pod project
RUN pip3 install -r requirements.txt

# Copy whole app folder to the working directory
COPY . /app