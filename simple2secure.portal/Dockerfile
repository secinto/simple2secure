FROM gradle:jdk10 as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :simple2secure.portal:build -x test

FROM tomcat:8.5
MAINTAINER secinto GmbH
COPY --from=builder /home/gradle/src/simple2secure.portal/build/libs/simple2secure.portal.war /usr/local/tomcat/webapps/s2s.war