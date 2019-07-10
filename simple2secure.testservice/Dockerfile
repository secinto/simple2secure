FROM ubuntu:18.04

MAINTAINER "Secinto GmbH"

RUN apt-get update -y && \
 		apt-get install -y python3-pip python3-dev nmap build-essential libssl-dev libffi-dev

COPY ./requirements.txt /app/requirements.txt

WORKDIR /app

RUN pip3 install -r requirements.txt

COPY . /app