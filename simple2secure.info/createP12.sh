#!/bin/bash
cp /etc/letsencrypt/live/simple2secure.info/*.pem .
openssl pkcs12 -export -in nginx.pem -inkey nginx.key -chain -CAfile chain.crt -name "tomcat" -out keystore.p12
rm *.pem