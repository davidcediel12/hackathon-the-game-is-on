FROM amazoncorretto:21
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 3000
ENTRYPOINT ["java","-jar","/app.jar"]