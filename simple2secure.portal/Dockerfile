FROM tomcat:8.5-alpine
VOLUME /tmp
COPY ./build/libs/simple2secure.portal.war /usr/local/tomcat/webapps/simple2secure.portal.war
RUN sh -c 'touch /usr/local/tomcat/webapps/simple2secure.portal.war'
ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /usr/local/tomcat/webapps/simple2secure.portal.war" ]