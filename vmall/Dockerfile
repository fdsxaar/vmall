FROM tomcat:8
LABEL maintainer="fdsxaar"
ENV REFRESHED_AT 2020-10-1

ARG WAR_FILE=target/*.war
COPY ${WAR_FILE} ${CATALINA_HOME}/webapps/vmall.war
ADD setenv.sh ${CATALINA_HOME}/bin/
RUN chmod +x ${CATALINA_HOME}/bin/setenv.sh
RUN rm -fr ${CATALINA_HOME}/webapps/examples ${CATALINA_HOME}/webapps/docs

EXPOSE 8080

CMD ["catalina.sh", "run"]
