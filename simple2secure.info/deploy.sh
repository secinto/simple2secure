#!/bin/bash
cp /etc/letsencrypt/live/simple2secure.info/*.pem .
cp fullchain.pem nginx.crt
cp privkey.pem nginx.key
openssl pkcs12 -export -in cert.pem -inkey nginx.key -chain -CAfile nginx.crt -name "tomcat" -out keystore.p12
rm *.pem
cp keystore.p12 ./ssl/.
chmod 644 nginx.key
chmod 755 simple2secure.portal/gradlew
sudo docker-compose build
