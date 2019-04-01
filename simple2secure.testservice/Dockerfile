FROM ubuntu:16.04

MAINTAINER Martin "emir.sahinovic@secinto.com"

RUN apt-get update -y && \
 		apt-get install -y python3-pip python3-dev nmap build-essential

COPY ./requirements.txt /app/requirements.txt

WORKDIR /app

RUN pip3 install -r requirements.txt

COPY . /app

CMD [ "python3", "app.py" ]