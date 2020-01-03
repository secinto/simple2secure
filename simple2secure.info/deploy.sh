#!/bin/bash
cp /etc/letsencrypt/live/simple2secure.info/*.pem .
cp fullchain.pem ./simple2secure.web/nginx/nginx.crt
cp privkey.pem ./simple2secure.web/nginx/nginx.key
chmod 644 ./simple2secure.web/nginx/nginx.key
openssl pkcs12 -export -in cert.pem -inkey nginx.key -chain -CAfile nginx.crt -name "tomcat" -out keystore.p12 -password pass:tomcat
rm *.pem
cp keystore.p12 ./ssl/.
rm keystore.p12
chmod 755 simple2secure.portal/gradlew
sudo docker-compose build
