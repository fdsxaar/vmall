FROM openjdk:8
LABEL maintainer="fdsxaar"
ENV REFRESHED_AT 2020-11-29

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} /data-init.jar

CMD ["java", "-jar", "/data-init.jar"]

