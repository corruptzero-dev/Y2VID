FROM amazoncorretto:11-alpine-jdk
MAINTAINER corruptzero
COPY build/libs/y2vid-0.0.1-SNAPSHOT.jar y2vid-1.0.0.jar
ENTRYPOINT ["java","-jar","/y2vid-1.0.0.jar"]
