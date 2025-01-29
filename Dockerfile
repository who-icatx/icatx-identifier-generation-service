FROM openjdk:17
MAINTAINER protege.stanford.edu

EXPOSE 7769
ARG JAR_FILE
COPY target/${JAR_FILE} icatx-identity-generation-service.jar
ENTRYPOINT ["java","--add-opens=java.management/sun.net=ALL-UNNAMED","-jar","/icatx-identity-generation-service.jar"]