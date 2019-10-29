FROM ubuntu:18.04

MAINTAINER "secinto GmbH"

RUN apt-get update -y && \
 		apt-get install -y python3-pip python3-dev nmap build-essential libssl-dev libffi-dev wget git bsdmainutils 
		
RUN apt-get install nmap
RUN apt-get install traceroute
RUN git clone https://github.com/sqlmapproject/sqlmap.git
RUN git clone https://github.com/EnableSecurity/wafw00f.git && export PATH=$PATH:/wafw00f/wafw00f/bin 
RUN git clone https://github.com/commixproject/commix.git commix
RUN git clone https://github.com/drwetter/testssl.sh.git

COPY ./requirements.txt /app/requirements.txt

WORKDIR /app

RUN pip3 install -r requirements.txt

COPY . /app