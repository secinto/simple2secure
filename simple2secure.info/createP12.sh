#!/bin/bash
openssl pkcs12 -export -in nginx.pem -inkey nginx.key -chain -CAfile chain.crt -name "tomcat" -out keystore.p12
