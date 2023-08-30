FROM openjdk:17-jdk
VOLUME /tmp
ADD ./build/libs/*SNAPSHOT.jar batch.jar
ENTRYPOINT ["java", "-Dspring.profiles.active={dev}", "-jar", "/batch.jar"]
